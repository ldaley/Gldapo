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
package gldapo.schema.attribute
import java.lang.reflect.Field
import gldapo.schema.annotation.GldapoPseudoType
import gldapo.schema.annotation.GldapoSynonymFor
import javax.naming.directory.DirContext

class MultiValueAttributeMappingTest extends AbstractAttributeMappingTest {
    
    def mappingClass = MultiValueAttributeMapping
    def mappingSubjectClass = MultiValueAttributeMappingTestSubject
    
    static REP = DirContext.REPLACE_ATTRIBUTE
    static REM = DirContext.REMOVE_ATTRIBUTE 
    static ADD = DirContext.ADD_ATTRIBUTE
    
    def getFakeContext(val) {
        new Expando(getStringAttributes: { return val as String[] })
    }
    
    void verifyProperties(mapping, attributeName, typeMapping, collectionType) {

        verifyProperties(mapping, attributeName, typeMapping)
        assertTrue("[fieldName: ${mapping.field.name}] - collectionType", collectionType.isAssignableFrom(mapping.collectionType))
    }
    
    void testStringSet() {
        def mapping = mappingForField("stringSet")
        verifyProperties(mapping, "stringSet", "String", Set)
        verifyMapFromContext(mapping, ["1", "2", "3"], ["1", "2", "3"] as Set)        
    }
    
    void testSortedIntSet() {
        def mapping = mappingForField("sortedIntSet")
        verifyProperties(mapping, "sortedIntSet", "Integer", SortedSet)
        verifyMapFromContext(mapping, ["3", "1", "2"], [1, 2, 3] as Set)
    }
    
    void testPseudoTypeSet() {
        def mapping = mappingForField("pseudoTypeSet")
        verifyProperties(mapping, "pseudoTypeSet", "Integer", Set)
        verifyMapFromContext(mapping, ["1", "2", "3"], [1, 2, 3] as Set)
        
        verifyCalculateModificationItems(mapping, [1, 2, 3], [1, 2, 3], []) // same
        
        verifyCalculateModificationItems(mapping, null, [1, 2], [[ADD, ["1", "2"]]]) // from null
        verifyCalculateModificationItems(mapping, [], [1, 2], [[ADD, ["1", "2"]]]) // from emptylist
        
        verifyCalculateModificationItems(mapping, [1, 2, 3], null, [[REM, null]]) // to null
        verifyCalculateModificationItems(mapping, [1, 2, 3], [], [[REM, null]]) // to emptylist
        
        verifyCalculateModificationItems(mapping, [1, 2, 3], [1, 3], [[REM, "2"]]) // remove 1
        verifyCalculateModificationItems(mapping, [1, 3], [1, 2, 3], [[ADD, "2"]]) // add 1

        verifyCalculateModificationItems(mapping, [1, 2, 3], [1], [[REM, ["2", "3"]]]) // remove 2
        verifyCalculateModificationItems(mapping, [1], [1, 2, 3], [[ADD, ["2", "3"]]]) // add 2
        
        verifyCalculateModificationItems(mapping, [], [1], [[ADD, "1"]])
        
        verifyCalculateModificationItems(mapping, [1, 3], [1, 2], [[REM, "3"], [ADD, "2"]])
        
        verifyCalculateModificationItems(mapping, [1], [], [[REM, null]])        
    }
    
    void verifyCalculateModificationItems(mapping, clean, dirty, mods) {
        super.verifyCalculateModificationItems(mapping, clean as Set, dirty as Set, mods)
    }
    
    void testBogusType() {
        shouldFail {
            mappingForField("badCollectionType")
        }
    }
}

class MultiValueAttributeMappingTestSubject {
    Set stringSet
    
    SortedSet<Integer> sortedIntSet
    
    @GldapoPseudoType("Integer")
    Set pseudoTypeSet
    
    LinkedList badCollectionType
}