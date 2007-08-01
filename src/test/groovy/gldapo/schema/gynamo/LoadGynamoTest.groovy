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
import gldapo.GldapoTemplate
import gldapo.exception.GldapoException
import gldapo.schema.annotations.GldapoIdentifyingAttribute
import groovy.mock.interceptor.*
import javax.naming.directory.SearchControls
import org.springframework.ldap.core.AttributesMapperCallbackHandler
import gynamo.*

class LoadGynamoTest extends GroovyTestCase 
{
	void testGetFailsWhenNoIdentifyingAttribute() 
	{
		Gynamo.gynamize(CantLoadSchema, LoadGynamo)

		shouldFail (GldapoException) {
			CantLoadSchema.load("whatever", null)
		}
	}

	void testLoad()
	{
		Gynamo.gynamize(CanLoadSchema, LoadGynamo)
		def injectee = new CanLoadSchema()
		def findResults

		CanLoadSchema.metaClass.'static'.find = { Map options ->
			return findResults
		}
		
		findResults = [injectee]
		assertSame(injectee, CanLoadSchema.load("something", [:]))
		findResults = []
		assertEquals(null, CanLoadSchema.load("something", [:]))
	}
}

@GldapoIdentifyingAttribute("uid")
class CanLoadSchema 
{
	
}

class CantLoadSchema 
{
	
}