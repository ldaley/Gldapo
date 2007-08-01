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
import gldapo.schema.provided.Person
import javax.naming.directory.SearchControls

class LiveTest extends GroovyTestCase 
{
	
	LiveTest()
	{
		Gldapo.initialize(this.class.getClassLoader().findResource("washington-edu-conf.groovy"))
	}
	
	void testFind() 
	{
		def people = Person.find(
			base: "ou=Faculty and Staff,ou=People", 
			searchScope: SearchControls.SUBTREE_SCOPE,
			countLimit: 2 // Only get two so we don't hit their server hard
		)
		
		println people[0].objectclass
		assertEquals(2, people.size())
	}
}