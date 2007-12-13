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
import gldapo.exception.GldapoNoDefaultDirectoryException
import gldapo.exception.GldapoException
import gldapo.exception.GldapoInvalidConfigException
import gldapo.directory.*

class GldapoDirectoryRegistry extends LinkedList<GldapoDirectory>
{
	static final CONFIG_DIRECTORIES_KEY = 'directories'
	
	String defaultDirectoryName
	
	def getDefaultDirectory()
	{
		if (this.size() == 1) return this[0]
		
		if (defaultDirectoryName == null) throw new GldapoNoDefaultDirectoryException()
		def defaultDirectory = this[defaultDirectoryName]
		if (defaultDirectory == null) throw new GldapoException("The default directory name of '${defaultDirectoryName} does not match any registered directory")
		return defaultDirectory
	}
	
	def getAt(String name)
	{
		def directory = this.find { it.beanName.equals(name) }
		if (directory == null) throw new GldapoException("There is no directory registered by the name of ${name}")
		return directory
	}
	
	static newInstance(ConfigObject config)
	{
		def registry = new GldapoDirectoryRegistry()
		
		config[CONFIG_DIRECTORIES_KEY]?.each { dirName, dirConfig -> 
			registry << GldapoDirectory.newInstance(dirName, dirConfig)
			if (dirConfig.defaultDirectory) registry.defaultDirectoryName = dirName
		}
		
		return registry
	}
}