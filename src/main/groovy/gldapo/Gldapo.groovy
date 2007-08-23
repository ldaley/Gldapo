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
package gldapo;
import gldapo.exception.GldapoInitializationException;

/**
 * A static style class that is responsible for initialising the Gldapo system and providing access
 * to global type objects.
 */
class Gldapo
{
	/**
	 * The name of the file that is looked for if initialize is called with no URL for the config file
	 */
	static final DEFAULT_CONFIG_FILENAME = 'gldapo-conf.groovy'

	/**
	 * Used to obtain templates by name
	 */
	GldapoDirectoryRegistry directories
	
	/**
	 * Used to Gldapify a schema class, registering in registry injects dynamic behaviour
	 */
	GldapoSchemaRegistry schemas
	
	/**
	 * A collection of run time global settings
	 */
	GldapoSettings settings
	
	/**
	 * 
	 */
	GldapoOperationRegistry operations
	
	/**
	 * 
	 */
	GldapoTypeMappingRegistry typeMappings
	
	/**
	 * The singleton instance
	 */
	static private Gldapo instance
	
	/**
	 * Singleton getter
	 */
	static Gldapo getInstance()
	{
		if (!instance) instance = new Gldapo()
		return instance
	}
	
	/**
	 * Initialises with the default config file and no environment
	 */
	static initialize()
	{
		initialize(null)
	}
	
	/**
	 * Initialise with default config file and specified environment
	 */
	static initialize(String environment)
	{
		def configUrl = getClassLoader().findResource(DEFAULT_CONFIG_FILENAME)
		if (configUrl == null)
		{
			throw new GldapoInitializationException("Could not find default config resource ${DEFAULT_CONFIG_FILENAME}" as String)
		}
		
		initialize(configUrl, environment)
	}
	
	/**
	 * Initialise with config file at URL and no environment
	 */
	static initialize(URL configUrl)
	{
		initialize(configUrl, null)
	}
	
	/**
	 * Initialise with config file at URL and specified environment
	 */
	static initialize(URL configUrl, String environment)
	{
		if (configUrl == null)
		{ 
			throw new GldapoInitializationException("Gldapo.initialize called with null URL")
		}
		
		def slurper
		if (environment)
		{
			slurper = new ConfigSlurper(environment)
		}
		else
		{
			slurper = new ConfigSlurper()
		}

		initialize(slurper.parse(configUrl))
	}
	
	/**
	 * Initialise from the passed config object
	 */
	static initialize(ConfigObject config)
	{
		def gldapo = getInstance()
		
		gldapo.schemas = GldapoSchemaRegistry.newInstance(config)
		gldapo.directories = GldapoDirectoryRegistry.newInstance(config)
		gldapo.settings = GldapoSettings.newInstance(config)
	}
	
	def getSchemas()
	{
		if (this.schemas == null) this.schemas = new GldapoSchemaRegistry()
		return this.schemas
	}
	
	def getDirectories()
	{
		if (this.directories == null) this.directories = new GldapoDirectoryRegistry()
		return this.directories
	}
	
	def getSettings()
	{
		if (this.settings == null) this.settings = new GldapoSettings()
		return this.settings
	}
	
	def getOperations()
	{
		if (this.operations == null) this.operations = new GldapoOperationRegistry()
		return this.operations
	}
	
	def getTypeMappings()
	{
		if (this.typeMappings == null) this.typeMappings = new GldapoTypeMappingRegistry()
		this.typeMappings
	}
	
}