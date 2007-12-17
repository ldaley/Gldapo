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
	 * The singleton instance
	 */
	static private Gldapo instance

	/**
	 * 
	 */
	private GldapoDirectoryRegistry directories
	
	/**
	 * 
	 */
	private GldapoSchemaRegistry schemas
	
	/**
	 * 
	 */
	private GldapoSettings settings
	
	/**
	 * 
	 */
	private GldapoTypeMappingRegistry typemappings

	/**
	 * 
	 */
	private GldapoOperationRegistry operations
	
		
	/**
	 * Does nothing.
	 */
	private Gldapo() {
		
	}

	/**
	 * The directory registry holds all the known about {@link GldapoDirectory directory objects}.
	 */
	GldapoDirectoryRegistry getDirectories() {
		return this.directories
	}

	/**
	 * The schema registry holds all schema classes. 
	 * 
	 * Schema classes <strong>must</strong> be registered with the registry before they can be used.
	 */
	GldapoSchemaRegistry getSchemas() {
		return this.schemas
	}
	
	/**
	 * The type mapping registry holds global type mappings for converting data between the LDAP and Java worlds.
	 */
	GldapoTypeMappingRegistry getTypeMappings() {
		return this.typemappings
	}

	/**
	 * A collection of run time global settings.
	 */
	GldapoSettings getSettings() { 
		return this.settings
	}

	/**
	 * The operation registry contains objects that performing LDAP operations
	 */
	GldapoOperationRegistry getOperations() {
		return this.operations
	}

	/**
	 * Retrieves the instance.
	 * 
	 * Must be called <strong>after</strong> {@link #initialize(Map)}.
	 * 
	 * @return The singleton instance of {@code Gldapo}
	 * @throws GldapoInitializationException if intialize hasn't been called
	 */
	static Gldapo getInstance() throws GldapoInitializationException {
		if (instance != null) {
			return instance
		} else {
			throw new GldapoInitializationException("Gldapo not initialized")
		}
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
	 * 
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
	 * 
	 * This method simply passes the config to the following methods, setting the respective instance to the return value ...
	 * 
	 * <ul>
	 * 	<li>{@link GldapoSchemaRegistry#newInstance(Map)}
	 * 	<li>{@link GldapoDirectoryRegistry#newInstance(Map)}
	 * 	<li>{@link GldapoTypeMappingRegistry#newInstance(Map)}
	 * 	<li>{@link GldapoSettings#newInstance(Map)}
	 * </ul>
	 * 
	 * Also creates an instance of {@link GldapoOperationRegistry}
	 * 
	 * @param config A map containing the desired config for Gldapo
	 * @throws GldapoInitializationException If there is something wrong with the config
	 */
	static initialize(Map config) throws GldapoInitializationException {
		
		instance = new Gldapo()
		instance.typemappings = GldapoTypeMappingRegistry.newInstance(config)
		instance.schemas = GldapoSchemaRegistry.newInstance(config)
		instance.directories = GldapoDirectoryRegistry.newInstance(config)


		instance.settings = GldapoSettings.newInstance(config)
		instance.operations = GldapoOperationRegistry.newInstance()
	}
}