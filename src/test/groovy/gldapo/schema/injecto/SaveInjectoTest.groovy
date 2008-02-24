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
    void testUpdate() {
        
        def e = new SaveInjectoTestSchema()

        e.exists = true
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
        e.directory = [updateEntry: { DistinguishedName rdn, List modificationItems ->
            assertEquals(1, modificationItems.size())
            assertEquals(modificationItems[0].modificationOp, REM)
            assertEquals(modificationItems[0].attribute.iD, "b")
            assertEquals(modificationItems[0].attribute.get(), "2")
        }]
        
        e.update()
    }
    
    /**
     * 
     */
    void testCreate() {
        def e = new SaveInjectoTestSchema()
        e.a = "clean"
        e.b = ["1", "2"] as Set
        e.rdn = "dc=example,dc=com"
        e.directory = [createEntry: { DistinguishedName rdn, Attributes attributes ->
            assertEquals(2, attributes.size())
            assertEquals("clean", attributes.get("a").get())
            
            def b = attributes.get("b")
            assertTrue(b.contains("1"))
            assertTrue(b.contains("2"))
        }]
        
        e.create()
        
    }
    
    void testSave() {
        def e = new SaveInjectoTestSchema()
        
        e.exists = true
        
        def called = false
        e.metaClass.update << { -> 
            called = true
        }
        e.metaClass.create << { -> 
            throw new Error()
        }
        e.save()
        assertTrue("update() should have been called", called)
        
        e.exists = false
        called = false
        e.metaClass.update << { -> 
            throw new Error()
        }
        e.metaClass.create << { -> 
            called = true
        }
        e.save()
        assertTrue("create() should have been called", called)
    }
    
    void testMove() {
        def e = new SaveInjectoTestSchema()
        def moveto = new DistinguishedName("dc=example2,dc=com")
        e.exists = true
        e.rdn = "dc=example,dc=com"
        e.directory = [moveEntry: { from, to -> 
            assertEquals(from, e.rdn)
            assertEquals(to, moveto)
        }]
        
        def called = false
        e.metaClass.update << { ->
            called = true
        }
        e.move(moveto)
        assertTrue(called)
        assertEquals(e.rdn, moveto)
        
        def e2 = new SaveInjectoTestSchema()
        e2.exists = false
        shouldFail(Exception) {
            e2.move(moveto)
        }
    }
    
    void testReplace() {
        def e = new SaveInjectoTestSchema()
        e.rdn = "dc=example,dc=com"
        def replaced = new DistinguishedName("dc=example2,dc=com")
        e.directory = [replaceEntry: { target, attributes ->
            assertEquals(replaced, target)
        }]
        e.replace(replaced)
        assertEquals(replaced, e.rdn)
    }
}

class SaveInjectoTestSchema {
    String a
    Set<String> b
}