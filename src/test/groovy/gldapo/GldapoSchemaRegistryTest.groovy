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
import gldapo.schema.GldapoSchemaRegistration

class GldapoSchemaRegistryTest extends GroovyTestCase 
{	
	void testNewInstanceFromConfig()
	{
		def c = new ConfigObject()
		c[GldapoSchemaRegistry.CONFIG_SCHEMAS_KEY] = [RegistryTestSchema1, RegistryTestSchema1]
		
		def r = GldapoSchemaRegistry.newInstance(c)
		assertEquals(2, r.size())
		
		def t1 = r[RegistryTestSchema1]
		def t2 = r[RegistryTestSchema2]
		
		assertNotNull(t1)
		assertNotNull(t2)

		assertEquals(true, t1 instanceof GldapoSchemaRegistration)
	}
}

class RegistryTestSchema1 {}
class RegistryTestSchema2 {}


