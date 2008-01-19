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
import gldapo.Gldapo
import javax.naming.directory.DirContext
import javax.naming.directory.Attributes
import org.springframework.ldap.core.DistinguishedName

class SaveInjectoTest extends GroovyTestCase {

    static gldapo = new Gldapo(schemas: [SaveInjectoTestSchema])
    
    static REP = DirContext.REPLACE_ATTRIBUTE
    static REM = DirContext.REMOVE_ATTRIBUTE 
    static ADD = DirContext.ADD_ATTRIBUTE
    
    /**
     * We don't need to put the calculation of correct modification items here, the *AttributeMappingTest tests
     * do that. We just need to check that we are calling things correctly.
     */
    void testSaveExisting() {
        
        def e = new SaveInjectoTestSchema()

        e.existingEntry = true
        e.a = "clean"
        e.b = ["1", "2"] as Set
        e.snapshotStateAsClean()
        assertTrue(e.modificationItems.empty)
        
        e.b.remove("2")
        
        def mods = e.modificationItems
        assertEquals(1, mods.size())
        assertEquals(mods[0].modificationOp, REM)
        assertEquals(mods[0].attribute.iD, "b")
        assertEquals(mods[0].attribute.get(), "2")
        
        e.rdn = "dc=example,dc=com"
        e.directory = [save: { DistinguishedName rdn, List modificationItems ->
            assertEquals(1, modificationItems.size())
            assertEquals(modificationItems[0].modificationOp, REM)
            assertEquals(modificationItems[0].attribute.iD, "b")
            assertEquals(modificationItems[0].attribute.get(), "2")
        }]
        
        e.save()
    }
    
    /**
     * 
     */
    void testSaveNew() {
        def e = new SaveInjectoTestSchema()
        e.a = "clean"
        e.b = ["1", "2"] as Set
        e.rdn = "dc=example,dc=com"
        e.directory = [save: { DistinguishedName rdn, Attributes attributes ->
            assertEquals(2, attributes.size())
            assertEquals("clean", attributes.get("a").get())
            
            def b = attributes.get("b")
            assertTrue(b.contains("1"))
            assertTrue(b.contains("2"))
        }]
        
        e.save()
        
    }
}

class SaveInjectoTestSchema {
    String a
    Set<String> b
}