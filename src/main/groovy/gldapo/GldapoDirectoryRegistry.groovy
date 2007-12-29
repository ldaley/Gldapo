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

import gldapo.exception.GldapoDirectoryNotFoundException
import gldapo.exception.GldapoNoDefaultDirectoryException
import gldapo.exception.GldapoException
import gldapo.exception.GldapoInvalidConfigException
import gldapo.*

/**
 * A directory registry holds instances of {@link GldapoDirectory}, retrievable by their {@link GldapoDirectory#getName()}.
 */
class GldapoDirectoryRegistry extends LinkedList<GldapoDirectory> {

	/**
	 * The default directory is the directory used when no directory is specified for an operation.
	 */
	String defaultDirectoryName
	
	/**
	 * Returns the current default directory.
	 * <p>
	 * If there is only one directory in the registry, it is returned regardless. Else the registry is searched for a directory with
	 * a name matching {@link #defaultDirectoryName}.
	 * 
	 * @return The default directory
	 * @throws GldapoNoDefaultDirectoryException If there is more then one registered directory, and {@link #defaultDirectoryName} is null.
	 * @throws GldapoDirectoryNotFoundException If there is no directory registered that has a name of {@link #defaultDirectoryName}
	 */
	GldapoDirectory getDefaultDirectory() throws GldapoNoDefaultDirectoryException, GldapoDirectoryNotFoundException {
		if (this.size() == 1) return this[0]
		
		if (defaultDirectoryName == null) throw new GldapoNoDefaultDirectoryException()
		def defaultDirectory = this[defaultDirectoryName]
		if (defaultDirectory == null) throw new GldapoDirectoryNotFoundException(defaultDirectoryName)
		return defaultDirectory
	}
	
	/**
	 * Allows the directories to be retrieved by name.
	 * 
	 * @param name The target directory
	 * @throws GldapoDirectoryNotFoundException If there is no directory registered with {@code name}
	 */
	def getAt(String name)
	{
		def directory = this.find { it.name.equals(name) }
		if (directory == null) throw new GldapoDirectoryNotFoundException(name)
		return directory
	}
	
	/**
	 * Does simple type check to make sure what's being added is an instance of {@link GldapoDirectory}
	 * 
	 * @param directory
	 */
	boolean add(directory) {
		if (directory instanceof GldapoDirectory) {
			super.add(directory)
		} else { 
			throw new IllegalArgumentException("Can only add GldapoDirectory objects to GldapoDirectoryRegistry")
		}
	}
}