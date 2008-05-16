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
package gldapo.search
import gldapo.Gldapo
import gldapo.GldapoDirectory
import gldapo.schema.annotation.GldapoSchemaFilter
import org.springframework.ldap.core.DistinguishedName
import org.springframework.ldap.filter.*

class SearchOptionParserTest extends GroovyTestCase {

    static gldapo = new Gldapo(schemas: [SearchOptionParserTestSchema, SearchOptionParserTestSchemaWithFilter])

    def mockDirectory = [getSearchControls: { new SearchControls() }, getBase: {-> new DistinguishedName("dc=example,dc=com") }] as SearchProvider

    def getParser(Map options) {
        getParser(options, null)
    }

    def getParser(Map options, Class schema) {
        if (!options.containsKey("directory")) options.directory = mockDirectory
        if (schema == null) schema = SearchOptionParserTestSchema
        new SearchOptionParser(schema, options)
    }

    void testDirectory() {
        def td = new GldapoDirectory("t", [url: "ldap://example.com"])
        gldapo.directories << td

        def withDirectoryName = getParser(directory: "t")
        assertSame(td, withDirectoryName.directory)

        gldapo.directories.defaultDirectoryName = "t"
        def withoutDirectory = getParser(directory: null)
        assertSame(td, withoutDirectory.directory)

        def md = [getSearchControls: { new SearchControls() }, getBase: {-> ""}] as SearchProvider
        def withDirectory = getParser(directory: md)
        assertSame(md, withDirectory.directory)
    }

    void testBase() {
        def noBase = getParser([:])
        assertEquals(new DistinguishedName(""), noBase.base)

        def withBase = getParser([base: "ou=people"])
        assertEquals(new DistinguishedName("ou=people"), withBase.base)
        def withAbsoluteBase = getParser([absoluteBase: "ou=people,dc=example,dc=com"])
        assertEquals(new DistinguishedName("ou=people"), withAbsoluteBase.base)
    }

    void testSearchControls() {
        def sc = new SearchControls(countLimit: 50)
        def d = [getSearchControls: { sc }, getBase: {->"dc=example,dc=com"}] as SearchProvider

        def withCustomCountLimit = getParser(countLimit: 20, directory: d)
        assertEquals(20, withCustomCountLimit.controls.countLimit)

        def withDirectoryCountLimit = getParser(directory:d)
        assertEquals(50, withDirectoryCountLimit.controls.countLimit)

        shouldFail {
            getParser(searchScope: "bogus")
        }
    }

    void testFilter() {
        def withFilter = getParser(filter: "(a=b)")
        assertEquals("(a=b)", withFilter.filter)

        def noFilter = getParser([:])
        assertEquals("(objectclass=*)", noFilter.filter)

        def withFilterAndSchemaFilter = getParser(filter: "(a=b)", SearchOptionParserTestSchemaWithFilter)
        assertEquals("(&(objectclass=person)(a=b))", withFilterAndSchemaFilter.filter)

        def noFilterAndSchemaFilter = getParser([:], SearchOptionParserTestSchemaWithFilter)
        assertEquals("(objectclass=person)", noFilterAndSchemaFilter.filter)
    }
    
    void testFilterWithFilterObject() {
        
        def withFilter = getParser(filter: new EqualsFilter("a", "b"))
        assertEquals("(a=b)", withFilter.filter)

        def withFilterAndSchemaFilter = getParser(filter: new EqualsFilter("a", "b"), SearchOptionParserTestSchemaWithFilter)
        assertEquals("(&(objectclass=person)(a=b))", withFilterAndSchemaFilter.filter)
    }
}

class SearchOptionParserTestSchema{}

@GldapoSchemaFilter("(objectclass=person)")
class SearchOptionParserTestSchemaWithFilter{}