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

class LiveTest extends GroovyTestCase 
{
	
	LiveTest()
	{
		Gldapo.initialize(this.class.getClassLoader().findResource("washington-edu-conf.groovy"))
	}
	
	void testFind() 
	{
		def people = WashingtonEduPerson.findAll(
			filter: "(&(telephonenumber=*)(uid=*))",
			base: "ou=Faculty and Staff, ou=People", 
			searchScope: "subtree",
			countLimit: 2 // Only get two so we don't hit their server hard
		)
		
		people.each {
			println it.uid
			println it.telephoneNumber	
		}
		
		assertEquals(2, people.size())
	}
}