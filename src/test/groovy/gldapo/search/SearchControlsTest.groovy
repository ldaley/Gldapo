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

class SearchControlsTest extends GroovyTestCase
{
    void testSetSearchScopeAndAsInteger()
    {
        def sc = new SearchControls()

        sc.searchScope = "subtree"
        assertEquals(javax.naming.directory.SearchControls.SUBTREE_SCOPE,sc.searchScopeAsInteger)
        sc.searchScope = "object"
        assertEquals(javax.naming.directory.SearchControls.OBJECT_SCOPE, sc.searchScopeAsInteger)
        sc.searchScope = "onelevel"
        assertEquals(javax.naming.directory.SearchControls.ONELEVEL_SCOPE, sc.searchScopeAsInteger)

        shouldFail() {
            sc.searchScope = "xxxxxxxxxx"
        }
    }

    void testFromConfig()
    {
        def c = new ConfigObject()
        c.countLimit = 100
        c.derefLinkFlag = false
        c.searchScope = "object"
        c.timeLimit = 200
        def sc = SearchControls.newInstance(c)

        assertEquals(100, sc.countLimit)
        assertFalse(sc.derefLinkFlag)
        assertEquals("object", sc.searchScope)
        assertEquals(200, sc.timeLimit)
    }

    void testAsJavaxSearchControls()
    {
        def sc = new SearchControls()
        sc.countLimit = 100
        sc.derefLinkFlag = false
        sc.searchScope = "object"
        sc.timeLimit = 200

        def jsc = sc as javax.naming.directory.SearchControls
        assertTrue(jsc instanceof javax.naming.directory.SearchControls)

        assertEquals(100, jsc.countLimit)
        assertFalse(jsc.derefLinkFlag)
        assertEquals(javax.naming.directory.SearchControls.OBJECT_SCOPE, jsc.searchScope)
        assertEquals(200, jsc.timeLimit)
    }

    void testMerging()
    {
        def sc = new SearchControls()
        sc.countLimit = 100
        sc.derefLinkFlag = false
        sc.searchScope = "object"
        sc.timeLimit = 200

        def sc2 = new SearchControls()
        sc2.searchScope = "onelevel"
        sc2.timeLimit = 300

        def s = sc.mergeWith(sc2)
        assertEquals(100, s.countLimit)
        assertFalse(s.derefLinkFlag)
        assertEquals("onelevel", s.searchScope)
        assertEquals(300, s.timeLimit)
    }
}