/* 
 * Copyright 2007 Luke Daley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gldapo
import gldapo.exception.GldapoInitializationException
import gldapo.exception.GldapoInvalidConfigException
import org.springframework.validation.Validator
import gldapo.schema.EntryValidator
import gldapo.schema.annotation.*
import gldapo.schema.constraint.ConstraintValidatorFactory
import gldapo.schema.constraint.DelegatingConstraintValidatorFactory
import gldapo.schema.constraint.InvalidConstraintTypeException

/**
 * The singleton instance of this class provides access to the various registries and settings at runtime. 
 * It is also the entry point for initializing Gldapo.
 */
class Gldapo {
    
    /**
     * The name of the file that is looked for if initialize is called with no URL for the config file ({@value})
     */
    static final DEFAULT_CONFIG_FILENAME = 'gldapo-conf.groovy'
    
    /**
     * The key that the array of type mapping classes is expected to be under in the config ({@value})
     * 
     * @see #extractTypeMappingsFromConfig()
     */
    static final CONFIG_KEY_TYPEMAPPINGS = 'typemappings'
    
    /**
    * The key that the map of directories is expected to be under in the config ({@value})
    * 
    * @see #extractDirectoriesFromConfig()
     */
    static final CONFIG_KEY_DIRECTORIES = 'directories'
    
    /**
     * The config item that holds the name of the default directory ({@value})
     */
    static final CONFIG_KEY_DEFAULT_DIRECTORY = 'defaultdirectory'

    /**
    * The key that the array of schema classes is expected to be under in the config ({@value})
    * 
    * @see #extractSchemasFromConfig()
     */
    static final CONFIG_KEY_SCHEMAS = 'schemas'

    /**
     * The singleton instance
     */
    static Gldapo instance = new Gldapo()

    /**
     * Holds the directory objects
     */
    GldapoDirectoryRegistry directories = new GldapoDirectoryRegistry()
    
    /**
     * Holds the registrations for schema classes
     */
    GldapoSchemaRegistry schemas = new GldapoSchemaRegistry(this)
    
    /**
     * Holds classes that implement type mappings
     */
    GldapoTypeMappingRegistry typemappings = new GldapoTypeMappingRegistry()
    
    ConstraintValidatorFactory constraintValidatorFactory = new DelegatingConstraintValidatorFactory()

    static final defaultConstraintTypes = [Required, Matches]
    
    /**
     * Create a Gldapo instance with only defaults.
     */
    public Gldapo() {
        this([:])
    }
    
    /**
     * Create a Gldapo instance with a config
     * 
     * @see #resetWithConfig(Map)
     */
    public Gldapo(Map config) {
        this.consumeConfig(config)
    }
    
    /**
     * Calls {@code clear()} on the directory, schema and type mapping registries before passing the config to
     * {@link #consumeConfig(Map)}
     */
    void resetWithConfig(Map config) {
       this.directories.clear()
       this.schemas.clear()
       this.typemappings.clear()
       this.consumeConfig(config)
    }

    /**
     * Uses the {@code extract*} methods to interpret the config, then iteratively add's the items
     * to the respective registry.
     * 
     * @see #extractDirectoriesFromConfig(Map)
     * @see #extractTypeMappingsFromConfig(Map)
     * @see #extractSchemasFromConfig(Map)
     */
    void consumeConfig(Map config) {
        if (config != null) {
            extractDirectoriesFromConfig(config).each { this.directories << it }
            if (config.containsKey(CONFIG_KEY_DEFAULT_DIRECTORY)) 
                this.directories.defaultDirectoryName = config."$CONFIG_KEY_DEFAULT_DIRECTORY"

            extractTypeMappingsFromConfig(config).each { this.typemappings << it }
            
            [defaultConstraintTypes,extractConstraintTypesFromConfig(config)].each {
                it.each {
                    if (constraintValidatorFactory.registers(it)) {
                        constraintValidatorFactory.register(it)
                    } else {
                        throw new InvalidConstraintTypeException("Constraint type $it is unregisterable")
                    }
                }
            }
            
            extractSchemasFromConfig(config).each { this.schemas << it }
        }
    }
    
    
    /**
     * Calls {@link #initialize(String)} with a null string.
     */
    static initialize() {
        initialize(null)
    }
    
    /**
     * Tries to find the {@link #DEFAULT_CONFIG_FILENAME default config} file and passes it's URL along with {@code environment}
     * to {@link #initialize(URL,String)}
     * 
     * @param environment the environment to parse the default config file with
     * @throws GldapoInitializationException If no {@link #DEFAULT_CONFIG_FILENAME default config} file can be found
     */
    static initialize(String environment) throws GldapoInitializationException {
        
        def configUrl = getClassLoader().findResource(DEFAULT_CONFIG_FILENAME)

        if (configUrl == null) 
            throw new GldapoInitializationException("Could not find default config resource ${DEFAULT_CONFIG_FILENAME}" as String)
        
        initialize(configUrl, environment)
    }
    
    /**
     * Calls {@link #initialize(URL,String)}, passing {@code configUrl} and a {@code null} {@code environment}
     */
    static initialize(URL configUrl) {
        initialize(configUrl, null)
    }
    
    /**
     * Initializes using the config script {@code configUrl} and parses it for {@code environment}
     * <p>
     * Creates a {@link groovy.util.ConfigSlurper} with the context of {@code environment}
     * and uses it to parse {@code configUrl} into a {@link groovy.util.ConfigObject}.
     * The config object is then passed to {@link #initialize(Map)}.
     * 
     * @param configUrl The location of the config script (must not be null)
     * @param environment The environment context to parse configUrl with (can be null)
     * @throws GldapoInitializationException If configUrl is {@code null}
     */
    static initialize(URL configUrl, String environment) throws GldapoInitializationException {
        if (configUrl == null) throw new GldapoInitializationException("Gldapo.initialize called with null URL")

        def slurper
        if (environment) {
            slurper = new ConfigSlurper(environment)
        } else {
            slurper = new ConfigSlurper()
        }

        initialize(slurper.parse(configUrl))
    }
    
    /**
     * Calls {@link #resetWithConfig(Map)} on the gldapo {@link #instance}
     */
    static initialize(Map config) throws GldapoInitializationException {
        instance.resetWithConfig(config)
    }

    /**
     * Fetches the schema classes indictated by the config.
     * <p>
     * Looks in the config for a {@link Collection Collection} under the key indicated by {@link #CONFIG_KEY_SCHEMAS}
     * 
     * @return The schema classes
     * @throws GldapoInvalidConfigException if the relevant item in the config is not a {@link Collection}
     */
    static Collection extractSchemasFromConfig(Map config) throws GldapoInvalidConfigException {
        if (config.containsKey(CONFIG_KEY_SCHEMAS)) {
            def schemas = config[CONFIG_KEY_SCHEMAS]
            if (schemas instanceof Collection) {
                return schemas
            }
            else {
                throw new GldapoInvalidConfigException("Key '${CONFIG_KEY_SCHEMAS}' in config is not a collection")
            }
        }
    }

    /**
     * Fetches the type mapping classes indictated by the config.
     * <p>
     * Looks in the config for a {@link Collection} under the key indicated by {@link #CONFIG_KEY_TYPEMAPPINGS}
     * 
     * @return The type mapping classes
     * @throws GldapoInvalidConfigException if the relevant item in the config is not a {@link Collection}
     */
    static Collection extractTypeMappingsFromConfig(Map config) throws GldapoInvalidConfigException {
        if (config.containsKey(CONFIG_KEY_TYPEMAPPINGS)) {
            def typemappings = config[CONFIG_KEY_TYPEMAPPINGS]
            if (typemappings instanceof Collection) {
                return typemappings
            }
            else {
                throw new GldapoInvalidConfigException("Key '${CONFIG_KEY_TYPEMAPPINGS}' in config is not a collection")
            }
        }
    }

    /**
     * Creates a {@code List} of {@link GldapoDirectory} objects from the given config.
     * <p>
     * Looks in the config for a {@link Map} under the key indicated by {@link #CONFIG_KEY_DIRECTORIES}.
     * <p>
     * Each key/value pair in the directories config is turned into an instance of {@link GldapoDirectory} by using the
     * {@link GldapoDirectory#GldapoDirectory(String,Map)} constructor where the key is the {@link String} and the value is the
     * {@link Map}.
     * <p>
     * An example config map for this method might look like this ...
     * <pre>
     * [
     *  directories: [
     *      d1: [
     *          url: "ldap://example.com"
     *      ],
     *      d2: [
     *          url: "ldap://example.com"
     *      ]
     *  ]
     * ]
     * </pre>
     * See {@link GldapoDirectory#GldapoDirectory(String,Map)} for the format of the directory map.
     * 
     * @return A list of {@link GldapoDirectory} objects
     * @throws GldapoInvalidConfigException if the relevant item in the config is not a {@link Map}
     */
    static List extractDirectoriesFromConfig(Map config) throws GldapoInvalidConfigException {
        if (config.containsKey(CONFIG_KEY_DIRECTORIES)) {
            def directoryConfigs = config[CONFIG_KEY_DIRECTORIES]
            if (directoryConfigs instanceof Map) {
                def directories = []
                directoryConfigs.each { dirName, dirConfig -> 
                    directories << new GldapoDirectory(dirName, dirConfig)
                }
                return directories
            }
            else {
                throw new GldapoInvalidConfigException("Key '${CONFIG_KEY_DIRECTORIES}' in config is not a map")
            }
        }   
    }
    
    static List extractConstraintTypesFromConfig(Map config) throws GldapoInvalidConfigException {
        def constraintTypes = config.constraintTypes
        if (constraintTypes) {
            if (constraintTypes instanceof Collection)
                return constraintTypes
            else
                throw new GldapoInvalidConfigException("'constraintTypes' is not a Collection")
        } else {
            return []
        }
    }
}