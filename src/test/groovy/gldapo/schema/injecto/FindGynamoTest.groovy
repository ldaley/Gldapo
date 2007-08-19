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
import gldapo.GldapoSchemaRegistry
import gldapo.schema.annotation.*

/**
 * @todo These tests need to be more comprehensive
 */
class FindGynamoTest extends GroovyTestCase 
{

	FindGynamoTest()
	{
		GldapoSchemaRegistry.newInstance() << FindSchema
	}
	
	void testFind() 
	{
		def matches = [1,2,3]
		def template = new Expando()
		template.search = {
			base, filter, controls, handler, requestControl ->
			
			assertEquals("ou=people", base)
			assertEquals("(&(objectclass=find)(a=b))", filter)
			handler = [list: matches]
			requestControl = [cookie: null]
		}
		
		def results = FindSchema.find(
			template: template,
			filter: "(a=b)",
			base: "ou=people"
		)
	}
}

@GldapoSchemaFilter("(objectclass=find)")
class FindSchema 
{
	String uid
	String sn
}