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
import org.springframework.beans.factory.InitializingBean;
import gldapo.exception.GldapoInitializationException;

class Gldapo
{
	static final DEFAULT_CONFIG_FILENAME = 'gldapo-conf.groovy'

	static templateRegistry
	static schemaRegistry
	
	static initialize()
	{
		initialize(null)
	}
	
	static initialize(String environment)
	{
		def configUrl = getClassLoader().findResource(DEFAULT_CONFIG_FILENAME)
		if (configUrl == null)
		{
			throw new GldapoInitializationException("Could not find default config resource ${DEFAULT_CONFIG_FILENAME}" as String)
		}
		
		initialize(configUrl, environment)
	}
	
	static initialize(URL configUrl)
	{
		initialize(configUrl, null)
	}
	
	static initialize(URL configUrl, String environment)
	{
		if (configUrl == null)
		{ 
			throw new GldapoInitializationException("Gldapo.initializeFromConfigURL called with null URL")
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
	
	static initialize(ConfigObject config)
	{
		schemaRegistry = GldapoSchemaRegistry.newFromConfig(config)
		templateRegistry = GldapoTemplateRegistry.newFromConfig(config)
	}
}