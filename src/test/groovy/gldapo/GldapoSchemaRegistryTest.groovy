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
import gldapo.schema.gynamo.GldapoSchemaMetaGynamo

class GldapoSchemaRegistryTest extends GroovyTestCase 
{
	void testLeftShift() 
	{
		def registry = new GldapoSchemaRegistry()
		registry << RegistryTestSchema1
		assertEquals([RegistryTestSchema1], registry.schemas)
		assertEquals(true, Gynamo.isGynamized(RegistryTestSchema1, GldapoSchemaMetaGynamo))
	}
	
	void testSetSchemas()
	{
		def registry = new GldapoSchemaRegistry()
		registry.schemas = [RegistryTestSchema2, RegistryTestSchema3]

		assertEquals([RegistryTestSchema2, RegistryTestSchema3], registry.schemas)
		assertEquals(true, Gynamo.isGynamized(RegistryTestSchema2, GldapoSchemaMetaGynamo))
		assertEquals(true, Gynamo.isGynamized(RegistryTestSchema3, GldapoSchemaMetaGynamo))
	}
	
	void testNewFromConfig()
	{
		def c = new ConfigObject()
		c.schemas = [RegistryTestSchema4, RegistryTestSchema5]
		def r = GldapoSchemaRegistry.newFromConfig(c)
		assert(r instanceof GldapoSchemaRegistry)
		assert(Gynamo.isGynamized(RegistryTestSchema4, GldapoSchemaMetaGynamo))
		assert(Gynamo.isGynamized(RegistryTestSchema5, GldapoSchemaMetaGynamo))
		assertEquals([RegistryTestSchema4, RegistryTestSchema5], r.schemas)
	}
}

class RegistryTestSchema1 {}
class RegistryTestSchema2 {}
class RegistryTestSchema3 {}
class RegistryTestSchema4 {}
class RegistryTestSchema5 {}


