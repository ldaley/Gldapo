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
package gldapo.schema.gynamo;
import gynamo.*

class AttributeMappingGynamoTest extends GroovyTestCase 
{
	void testInjection() 
	{
		
		Gynamo.gynamize(SchemaTestA, AttributeMappingGynamo)
		List attributeMappings = SchemaTestA.getAttributeMappings()
		
		// Make sure we got what we wanted
		def objectclassAttribute = attributeMappings.find { it.name == "objectclass" }
		assertNotNull("A property should exist named 'objectclass'", objectclassAttribute)
		assertEquals("attribute type", List, objectclassAttribute.type)

		def samaccountnameAttribute = attributeMappings.find { it.name == "samaccountname" }
		assertNotNull("A property should exist named 'samaccountname'", samaccountnameAttribute)
		assertEquals("attribute type", String, samaccountnameAttribute.type)

		def snAttribute = attributeMappings.find { it.name == "sn" }
		assertNotNull("A property should exist named 'sn'", snAttribute)
		assertEquals("attribute type", String, snAttribute.type)
		
		// Final check to see if anything else untested for came through
		assertEquals("should have found 3 attributeMappings", 3, attributeMappings.size())
	}
}

class SchemaTestA
{
	String samaccountname
	String sn
	List objectclass
}