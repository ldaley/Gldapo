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
import gldapo.exception.GldapoInvalidConfigException

/**
 * @todo Tests needed
 */
class GldapoDirectoryTest extends GroovyTestCase 
{
    void testNewFromConfig() 
    {
        def c = new ConfigObject()
        
        c.url = "ldap://example.com"
        c.base = "ou=example,ou=com"
        c.userDn = "cn=user"
        c.password = "password"
        
        c.searchControls.countLimit = 50
        
        def d = new GldapoDirectory("testTemplate", c)
        assertNotNull(d)
        assertEquals(50, d.searchControls.countLimit)
        assertEquals("ou=example, ou=com", d.template.contextSource.base as String)
        assertEquals("testTemplate", d.name)
    }
    
    void testNewFromNullConfig() {
        shouldFail(GldapoInvalidConfigException) { new GldapoDirectory("test", null) }
    }
}