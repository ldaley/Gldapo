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
import javax.naming.directory.Attributes
import javax.naming.directory.BasicAttributes
import gldapo.search.SearchProvider
import gldapo.search.SearchControlProvider
import gldapo.schema.annotation.GldapoNamingAttribute

class GldapoSchemaClassInjectoTest extends GroovyTestCase {

    static gldapo = new Gldapo(schemas: [DummySchema])
    
    static REP = DirContext.REPLACE_ATTRIBUTE
    static REM = DirContext.REMOVE_ATTRIBUTE 
    static ADD = DirContext.ADD_ATTRIBUTE

    def directory = new DummyDirectory("dummy", [url: "ldap://example.com", base: "dc=example,dc=com"])
    
    void setUp() {
        gldapo.directories << directory
    }

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
        o.rdn = new DistinguishedName("name=test")
        assertEquals(new DistinguishedName("name=test"), o.rdn)
    }
    
    void testDn() {
        def o = new DummySchema()
        o.directory = [base: new DistinguishedName("dc=example, dc=com")]
        o.rdn = new DistinguishedName("name=people")
        assertEquals(new DistinguishedName("name=people, dc=example, dc=com"), o.dn)
    }
    
    void testGetWithResult() {
        def result = [1,2,3]
        directory.result = result
        assertEquals(result.first(), DummySchema.getByDn("cn=entry,dn=example,dc=com"))
    }
    

    void testUpdate() {
        
        def e = new DummySchema()

        e.exists = true
        e.a = "clean"
        e.b = ["1", "2"] as Set
        e.name = "example"
        e.parent = "dc=com"
        e.snapshotStateAsClean()
        assertTrue(e.modificationItems.empty)
        
        e.b.remove("2")
        
        def mods = e.modificationItems
        assertEquals(1, mods.size())
        assertEquals(mods[0].modificationOp, REM)
        assertEquals(mods[0].attribute.iD, "b")
        assertEquals(mods[0].attribute.get(), "2")
        

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
        e.name = "example"
        e.parent = "dc=com"
        e.directory = [createEntry: { DistinguishedName rdn, Attributes attributes ->
            assertEquals(3, attributes.size())
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
            called = false
        }
        e.save()
        assertTrue("update() should have been called", called)
        
        e.exists = false
        called = false
        e.metaClass.update << { -> 
            called = false
        }
        e.metaClass.create << { -> 
            called = true
        }
        e.save()
        assertTrue("create() should have been called", called)
    }
    
    void testMove() {
        def e = new DummySchema()
        def moveto = new DistinguishedName("name=example2,dc=com")
        e.exists = true
        e.name = "example"
        e.parent = "dc=com"
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
        e.name = "example"
        e.parent = "dc=com"
        def replaced = new DistinguishedName("name=example2,dc=com")
        e.directory = [replaceEntry: { target, attributes ->
            assertEquals(replaced, target)
        }]
        e.replace(replaced)
        assertEquals(replaced, e.rdn)
    }
    
    void installSearchResult(result)
    {
        Map
        gldapo.directories.values().each { gldapo.directories.remove(it) }
        gldapo.directories << [
            search: { Object registration, DistinguishedName base, String filter, SearchControlProvider controls -> result }
        ] as SearchProvider
    }
    
    void testFindAll() 
    {
        def result = [1,2,3]
        directory.result = result
        assertEquals(result, DummySchema.findAll())
    }
    
    void testFind()
    {
        def result = [1,2,3]
        directory.result = result
        assertEquals(result.first(), DummySchema.find())
        
        result = []
        directory.result = result
        assertEquals(null, DummySchema.find())
    }

    void testDelete() {
        def e = new DummySchema()
        def rdn = new DistinguishedName("name=example,dc=com")
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
        def rdn = new DistinguishedName("name=example,dc=com")
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
    
    void testAssumeDefaultDirectoryIfNoneSet() {
        def directoryRegistryMock = new GldapoDirectoryRegistry()
        def directory = new GldapoDirectory("d", [url: "ldap://example.com"])
        directoryRegistryMock << directory
        DummySchema.gldapo.directories = directoryRegistryMock
        def e = new DummySchema()
        assertNull(e.directory)
        e.assumeDefaultDirectoryIfNoneSet()
        assertEquals(directory, e.directory)
    }
    
    void testGetAttributesOnlyReturnsAttributesWhereAValueIsSet() {
        def e = new DummySchema()
        e.a = "test"
        assertEquals(1, e.attributes.size())
        
        e.b = ["test"]
        assertEquals(2, e.attributes.size())
        
        e.b = []
        assertEquals(1, e.attributes.size())
        
        e.a = null
        assertEquals(0, e.attributes.size())
    }
    
    void testNamingValue() {
        def e = new DummySchema()
        e.namingValue = "test"
        assertEquals("test", e.name)
        assertEquals("test", e.namingValue)
    }
    
    void testNamingFieldName() {
        def e = new DummySchema()
        assertEquals("name", e.namingAttribute)
    }
    
    void testSetParent() {
        def parentString = "ou=people"
        def parentDn = new DistinguishedName(parentString)

        def e = new DummySchema()
        e.parent = parentString
        assertTrue(e.parent instanceof DistinguishedName)
        assertEquals(parentDn, e.parent)
        
        e = new DummySchema()
        e.parent = parentDn
        assertEquals(parentDn, e.parent)
    }
}

class DummyDirectory extends GldapoDirectory {
    
    def validator 
    def result
    
    DummyDirectory(String name, Map options) {
        super(name, options)
    }
    
    List search(Object registration, DistinguishedName base, String filter, SearchControlProvider controls) {
        if (validator) validator()
        result
    }
}

class DummySchema {
    @GldapoNamingAttribute
    String name
    String a
    Set<String> b
}