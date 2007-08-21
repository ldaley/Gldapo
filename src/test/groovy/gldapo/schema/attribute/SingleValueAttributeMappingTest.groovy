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
package gldapo.schema.attribute;
import gldapo.schema.annotation.GldapoPseudoType
import gldapo.schema.annotation.GldapoSynonymFor

class SingleValueAttributeMappingTest extends AbstractAttributeMappingTest
{
	def mappingClass = SingleValueAttributeMapping
	def mappingSubjectClass = SingleValueAttributeMappingTestSubject
	
	def getFakeContext(val)
	{
		new Expando(getStringAttribute: { return val })
	}
	
	void testSimpleString() 
	{
		doMappingTest("simpleString", "simpleString", "String", "test", "test")
	}
	
	void testPseudoType() 
	{
		doMappingTest("pseudoType", "pseudoType", "Integer", "3", 3)
	}
	
	void testSynonym() 
	{
		doMappingTest("synonym", "other", "String", "o", "o")
	}
	
	void testNullValue() 
	{
		doMappingTest("simpleString", "simpleString", "String", null, null)
	}
	
	void testBogusType()
	{
		shouldFail() {
			mappingForField("bogusType")
		}
	}
}

class SingleValueAttributeMappingTestSubject
{
	String simpleString
	
	@GldapoPseudoType("Integer")
	String pseudoType
	
	@GldapoSynonymFor("other")
	String synonym
	
	@GldapoPseudoType("BogusType")
	String bogusType
}