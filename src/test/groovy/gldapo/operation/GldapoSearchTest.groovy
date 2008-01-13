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
package gldapo.operation
import gldapo.Gldapo
import gldapo.GldapoSearchProvider
import gldapo.GldapoDirectory
import gldapo.GldapoSearchControls
import gldapo.schema.annotation.GldapoSchemaFilter
import org.springframework.ldap.core.DistinguishedName

class GldapoSearchTest extends GroovyTestCase {
    
    static gldapo = new Gldapo()
    
    def mockDirectory = [getSearchControls: { new GldapoSearchControls() }, getBase: {-> new DistinguishedName("dc=example,dc=com") }] as GldapoSearchProvider
    
    def getSearch(Map options) {
        if (!options.containsKey("directory")) options.directory = mockDirectory
        if (!options.containsKey("schema")) options.schema = GldapoSearchTestDummySchema
        def s = new GldapoSearch()
        s.gldapo = gldapo
        s.options = options
        return s
    }
    
    void testDirectory() {
        def td = new GldapoDirectory("t", [url: "ldap://example.com"])
        gldapo.directories << td
        
        def withDirectoryName = getSearch(directory: "t")
        assertSame(td, withDirectoryName.options.directory)
        
        gldapo.directories.defaultDirectoryName = "t"
        def withoutDirectory = getSearch(directory: null)
        assertSame(td, withoutDirectory.options.directory)
        
        def md = [getSearchControls: { new GldapoSearchControls() }, getBase: {-> ""}] as GldapoSearchProvider
        def withDirectory = getSearch(directory: md)
        assertSame(md, withDirectory.options.directory)
    }
    
    void testBase() {
        def noBase = getSearch([:])
        assertEquals(new DistinguishedName(""), noBase.options.base)

        def withBase = getSearch([base: "ou=people"])
        assertEquals(new DistinguishedName("ou=people"), withBase.options.base)
        def withAbsoluteBase = getSearch([absoluteBase: "ou=people,dc=example,dc=com"])
        assertEquals(new DistinguishedName("ou=people"), withAbsoluteBase.options.base)
        
    }
    
    void testSearchControls() {
        def sc = new GldapoSearchControls(countLimit: 50)
        def d = [getSearchControls: { sc }, getBase: {->"dc=example,dc=com"}] as GldapoSearchProvider
        
        def withCustomCountLimit = getSearch(countLimit: 20, directory: d)
        assertEquals(20, withCustomCountLimit.options.searchControls.countLimit)
        
        def withDirectoryCountLimit = getSearch(directory:d)
        assertEquals(50, withDirectoryCountLimit.options.searchControls.countLimit)
        
        shouldFail {
            getSearch(searchScope: "bogus")
        }
    }
    
    void testFilter() {
        def withFilter = getSearch(filter: "(a=b)")
        assertEquals("(a=b)", withFilter.options.filter)
        
        def noFilter = getSearch([:])
        assertEquals("(objectclass=*)", noFilter.options.filter)
        
        def withFilterAndSchemaFilter = getSearch(filter: "(a=b)", schema: GldapoSearchTestDummySchemaWithFilter)
        assertEquals("(&(objectclass=person)(a=b))", withFilterAndSchemaFilter.options.filter)
        
        def noFilterAndSchemaFilter = getSearch(schema: GldapoSearchTestDummySchemaWithFilter)
        assertEquals("(objectclass=person)", noFilterAndSchemaFilter.options.filter)    
    }
}

class GldapoSearchTestDummySchema{}

@GldapoSchemaFilter("(objectclass=person)")
class GldapoSearchTestDummySchemaWithFilter{}