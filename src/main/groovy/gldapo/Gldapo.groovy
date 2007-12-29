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
     * 
     */
    static final CONFIG_KEY_TYPEMAPPINGS = 'typemappings'
    
    /**
     *
     */
    static final CONFIG_KEY_DIRECTORIES = 'directories'
    
    /**
     * 
     */
    static final CONFIG_KEY_DEFAULT_DIRECTORY = 'defaultdirectory'

    /**
     * 
     */
    static final CONFIG_KEY_SCHEMAS = 'schemas'

    /**
     * The singleton instance
     */
    static Gldapo instance = new Gldapo()

    /**
     * 
     */
    GldapoDirectoryRegistry directories = new GldapoDirectoryRegistry()
    
    /**
     * 
     */
    GldapoSchemaRegistry schemas = new GldapoSchemaRegistry()
    
    /**
     * 
     */
    GldapoTypeMappingRegistry typemappings = new GldapoTypeMappingRegistry()

    /**
     * 
     */
    GldapoOperationRegistry operations = new GldapoOperationRegistry()
    
        
    /**
     * Does nothing.
     */
    private Gldapo() {
        
    }
    
    void consumeConfig(Map config) {
        if (config) {
            def directories = extractDirectoriesFromConfig(config)
            if (directories) directories.each { this.directories << it }

            def typemappings = extractTypeMappingsFromConfig(config)
            if (typemappings) typemappings.each { this.typemappings << it }

            def schemas = extractSchemasFromConfig(config)
            if (schemas) schemas.each { this.schemas << it }
        }
    }
    
    void resetWithConfig(Map config) {
       this.directories.clear()
       this.schemas.clear()
       this.typemappings.clear()
       this.consumeConfig(config)
    }
    
    /**
     * Calls {@link #initialize(String)} with a null string.
     */
    static initialize() {
        initialize(null)
    }
    
    /**
     * Tries to find the {@link DEFAULT_CONFIG_FILENAME default config} file and passes it's URL along with {@code environment}
     * to {@link #initialize(URL,String)}
     * 
     * @param environment the environment to parse the default config file with
     * @throws GldapoInitializationException If no {@link DEFAULT_CONFIG_FILENAME default config} file can be found
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
     * Initializes using the config script @ {@code configUrl} and parses it for @{@code environment}
     * <p>
     * Creates a {@link http://http://groovy.codehaus.org/ConfigSlurper ConfigSlurper} with the context of {@code environment}
     * and uses it to parse {@code configUrl} into a {@link http://fisheye.codehaus.org/browse/groovy/trunk/groovy/groovy-core/src/main/groovy/util/ConfigObject.groovy?r=trunk groovy.util.ConfigObject}.
     * The config object is then passed to {@link initialize(Map)}.
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
     * Initializes Gldapo from the given config map.
     * <p>
     * This method simply passes the config to the following methods, setting the respective instance to the return value ...
     * <ul>
     *     <li>{@link GldapoSchemaRegistry#newInstance(Map)}
     *     <li>{@link GldapoDirectoryRegistry#newInstance(Map)}
     *     <li>{@link GldapoTypeMappingRegistry#newInstance(Map)}
     * </ul>
     * For the details of the config map format, see the above methods.
     * <p>
     * Also creates an instance of {@link GldapoOperationRegistry}
     * 
     * @param config A map containing the desired config for Gldapo
     * @throws GldapoInitializationException If there is something wrong with the config
     */
    static initialize(Map config) throws GldapoInitializationException {
        this.instance.resetWithConfig(config)
    }

    static extractSchemasFromConfig(Map config) {
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

    static extractTypeMappingsFromConfig(Map config) {
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
    
    static extractDirectoriesFromConfig(Map config) {
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
}