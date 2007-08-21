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
import java.lang.reflect.Field

class GldapoTypeMappingRegistryTest extends GroovyTestCase 
{
	void testGetFromDefaults() 
	{
		def r = new GldapoTypeMappingRegistry()
		
		def m = r.getToFieldMapperForType("String")
		assertNotNull(m)
		assertEquals("test", m.call("test"))
		
		def i = r.getToFieldMapperForType("Integer")
		assertNotNull(i)
		assertEquals(3, i.call("3"))
	}
	
	void testGetNonExistantReturnsNull()
	{
		def r = new GldapoTypeMappingRegistry()
		
		assertNull(r.getToFieldMapperForType("BollocksType"))
	}
	
	/**
	 * Test that the last added mapping provider is checked first
	 * i.e order is most recently added to least recently
	 */
	void testOrderingIsCorrect()
	{
		// GldapoTypeMappingRegistryTest1 should respond here
		def r1 = new GldapoTypeMappingRegistry()
		r1 << GldapoTypeMappingRegistryTest2
		r1 << GldapoTypeMappingRegistryTest1
		
		def m1 = r1.getToFieldMapperForType("Test")
		assertNotNull(m1)
		assertEquals(1, m1.call(""))
		
		// GldapoTypeMappingRegistryTest2 should respond here
		def r2 = new GldapoTypeMappingRegistry()
		r2 << GldapoTypeMappingRegistryTest1
		r2 << GldapoTypeMappingRegistryTest2

		def m2 = r2.getToFieldMapperForType("Test")
		assertNotNull(m2)
		assertEquals(2, m2.call(""))
	}
}

class GldapoTypeMappingRegistryTest1
{
	static mapToTestType(value)
	{
		return 1
	}
}

class GldapoTypeMappingRegistryTest2
{
	static mapToTestType(value)
	{
		return 2
	}
}