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
import org.springframework.ldap.core.support.LdapContextSource

/**
 * @todo Tests needed
 */
class GldapoTemplateTest extends GroovyTestCase 
{
	void testNewFromConfig() 
	{
		def c = new ConfigObject()
		
		c.contextSource.url = "ldap://example.com"
		c.contextSource.base = "ou=example,ou=com"
		c.countextSource.userDn = "cn=user"
		c.contextSource.password = "password"
		
		c.searchControls.countLimit = 50
		c.base = "ou=people"
		
		def t = GldapoTemplate.newFromConfig("testTemplate", c)
		assertNotNull(t)
		assertEquals(true, t instanceof GldapoTemplate)
		assertEquals(50, t.searchControls.countLimit)
		assertEquals("ou=people", t.base)
		assertEquals("testTemplate", t.beanName)
	}
}