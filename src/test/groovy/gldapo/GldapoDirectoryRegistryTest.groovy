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
import gldapo.exception.GldapoNoDefaultDirectoryException
import gldapo.exception.GldapoException
import gldapo.directory.GldapoDirectory
import gldapo.directory.GldapoDirectory

class GldapoDirectoryRegistryTest extends GroovyTestCase 
{
	void testGetDefaultWhenIsNone() 
	{
		def registry = new GldapoDirectoryRegistry()
		shouldFail (GldapoNoDefaultDirectoryException) {
			registry.defaultDirectory
		}
	}
	
	void testGetDefaultWhenDoesntExist() 
	{
		def registry = new GldapoDirectoryRegistry()
		registry.defaultDirectoryName = "abc"
		shouldFail (GldapoException) {
			registry.defaultDirectory
		}
	}
	
	void testGetDefault()
	{
		def registry = new GldapoDirectoryRegistry()
		def directory = new GldapoDirectory(beanName: "test")

		registry.defaultDirectoryName = "test"
		registry << directory
		assertSame(directory, registry.defaultDirectory)
	}
	
	void testNewFromOkConfig()
	{
		def c = new ConfigObject()
		
		c.directories.t1.url = "ldap://example.com"
		c.directories.t1.defaultDirectory = true
		c.directories.t2.url = "ldap://example2.com"
	
		def r = GldapoDirectoryRegistry.newInstance(c)
		
		assert(r instanceof GldapoDirectoryRegistry)
		assertEquals(2, r.size())
		assertNotNull(r["t1"])
		assertNotNull(r["t2"])
		assert(r["t1"] instanceof GldapoDirectory)
		assertSame(r.defaultDirectory, r["t1"])
	}
	
}