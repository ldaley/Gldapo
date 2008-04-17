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
package gldapo.schema

import gldapo.*
import javax.naming.directory.DirContext
import org.springframework.ldap.core.DistinguishedName
import gldapo.test.GldapoMockOperationInstaller
import javax.naming.directory.Attributes
import javax.naming.directory.BasicAttributes

class GldapoSchemaClassInjectoTest extends GroovyTestCase {

    static gldapo = new Gldapo(schemas: [DummySchema])
    
    static REP = DirContext.REPLACE_ATTRIBUTE
    static REM = DirContext.REMOVE_ATTRIBUTE 
    static ADD = DirContext.ADD_ATTRIBUTE
    
    void testSnapshotAsClean() {
        def e = new DummySchema()

        e.snapshotStateAsClean()
        assertTrue(e.cleanValues.containsKey("a"))
        assertNull(e.cleanValues.a)
        assertTrue(e.cleanValues.containsKey("b"))
        assertNull(e.cleanValues.b)

        e.a = "blah"
        e.b = ["1", "2"] as Set
        e.snapshotStateAsClean()
        assertEquals("blah", e.cleanValues.a)
        assertEquals(["1", "2"] as Set, e.cleanValues.b)
    }
    
    void testRevert() {
        def e = new DummySchema()
        e.a = "blah"
        e.b = ["a"] as Set
        e.snapshotStateAsClean()
        
        e.a = "notblah"
        e.b = ["b"] as Set
        e.revert()
        
        assertEquals("blah", e.a)
        assert ["a"] as Set == e.b
    }
    
    void testDirectory() {
        def o = new DummySchema()
        def d = new GldapoDirectory("test", [url: "ldap://example.com"])
        o.directory = d
        assertSame(d, o.directory)
    }
    
    void testRdn() {
        def o = new DummySchema()
        o.rdn = new DistinguishedName("dc=test")
        assertEquals(new DistinguishedName("dc=test"), o.rdn)
    }
    
    void testDn() {
        def o = new DummySchema()
        o.directory = [base: new DistinguishedName("dc=example, dc=com")]
        o.rdn = new DistinguishedName("ou=people")
        assertEquals(new DistinguishedName("ou=people, dc=example, dc=com"), o.dn)
    }
    
    void testGetWithResult() {
        GldapoMockOperationInstaller.installSearchWithResult([1,2,3], gldapo)
        assertEquals(1, DummySchema.getByDn("abc"))
    }
    

    void testUpdate() {
        
        def e = new DummySchema()

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

    void testCreate() {
        def e = new DummySchema()
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
        def e = new DummySchema()
        
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
        def e = new DummySchema()
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
        
        def e2 = new DummySchema()
        e2.exists = false
        shouldFail(Exception) {
            e2.move(moveto)
        }
    }
    
    void testReplace() {
        def e = new DummySchema()
        e.rdn = "dc=example,dc=com"
        def replaced = new DistinguishedName("dc=example2,dc=com")
        e.directory = [replaceEntry: { target, attributes ->
            assertEquals(replaced, target)
        }]
        e.replace(replaced)
        assertEquals(replaced, e.rdn)
    }
    
    void testFindAll() 
    {
        GldapoMockOperationInstaller.installSearchWithResult([1,2,3], gldapo)
        assertEquals([1,2,3], DummySchema.findAll())
    }
    
    void testFind()
    {
        GldapoMockOperationInstaller.installSearchWithResult([1,2,3], gldapo)
        assertEquals(1, DummySchema.find())
        
        GldapoMockOperationInstaller.installSearchWithResult([], gldapo)
        assertEquals(null, DummySchema.find())
    }

    void testDelete() {
        def e = new DummySchema()
        def rdn = new DistinguishedName("dc=example,dc=com")
        e.rdn = rdn
        def deleted = false
        e.directory = [deleteEntry: {
            assertEquals(rdn, it)
            deleted = true
        }]
        e.delete()
        assertTrue(deleted)
        assertFalse(e.exists)
    }
    
    void testDeleteRecursively() {
        def e = new DummySchema()
        def rdn = new DistinguishedName("dc=example,dc=com")
        e.rdn = rdn
        def deleted = false
        e.directory = [deleteEntryRecursively: {
            assertEquals(rdn, it)
            deleted = true
        }]
        e.deleteRecursively()
        assertTrue(deleted)         
        assertFalse(e.exists)
    }
    
}

class DummySchema {
    String a
    Set<String> b
}