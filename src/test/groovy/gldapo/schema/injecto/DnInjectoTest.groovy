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
package gldapo.schema.injecto
import injecto.*

class DnInjectoTest extends GroovyTestCase {

    DnInjectoTest() {
        use (Injecto) { DnInjectoTestSchema.inject(DnInjecto) }
    }
    
    void testRdn() {
        def o = new DnInjectoTestSchema()
        o.rdn = "test"
        assertEquals("test", o.rdn)
    }
    
    void testDn() {
        def o = new DnInjectoTestSchema()
        o.directory = [base: "dc=example, dc=com"]
        o.rdn = "ou=people"
        assertEquals("ou=people, dc=example, dc=com", o.dn)
    }
}

class DnInjectoTestSchema {}