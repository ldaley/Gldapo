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

class CleanValuesInjectoTest extends GroovyTestCase {

    static gldapo = new Gldapo(schemas: [CleanValuesInjectoTestSchema])
    
    void testSnapshotAsClean() {
        def e = new CleanValuesInjectoTestSchema()

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
        def e = new CleanValuesInjectoTestSchema()
        e.a = "blah"
        e.b = ["a"] as Set
        e.snapshotStateAsClean()
        
        e.a = "notblah"
        e.b = ["b"] as Set
        e.revert()
        
        assertEquals("blah", e.a)
        assert ["a"] as Set == e.b
    }
}

class CleanValuesInjectoTestSchema {
    String a
    Set<String> b
}