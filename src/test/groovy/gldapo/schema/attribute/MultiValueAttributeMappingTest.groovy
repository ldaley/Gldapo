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
import javax.naming.directory.BasicAttributes
import javax.naming.directory.BasicAttribute

class MultiValueAttributeMappingTest extends AbstractAttributeMappingTest {
    
    def mappingClass = MultiValueAttributeMapping
    def mappingSubjectClass = MultiValueAttributeMappingTestSubject
    
    static REP = DirContext.REPLACE_ATTRIBUTE
    static REM = DirContext.REMOVE_ATTRIBUTE 
    static ADD = DirContext.ADD_ATTRIBUTE
    
    static {
        HashSet.metaClass.equals = { Collection rhs ->
            def lhs = delegate 
            lhs.every { target ->
                rhs.find { target == it }
            } && rhs.every { target ->
                lhs.find { target == it }
            }
        }
    }
    
    def getFakeContext(values) {
        new Expando(getAttributes: { name, String[] attribNames ->
            def attribs = new BasicAttributes()
            def attrib = new BasicAttribute(attribNames[0])
            attribs.put(attrib)
            values.each {
                attrib.add(it)
            }
            attribs
        })
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
    
    void testByteArrays() {
        def mapping = mappingForField("byteArrays")
        verifyProperties(mapping, "byteArrays", "ByteArray", Set)
        
        def bo1 = "1" as Byte[]
        def bo2 = "2" as Byte[]
        def bo3 = "3" as Byte[]
        def bp1 = "1" as byte[]
        def bp2 = "2" as byte[]
        def bp3 = "3" as byte[]
        
        verifyMapFromContext(mapping, [bp1,bp2,bp3], [bo1,bo2,bo3] as Set)
        verifyCalculateModificationItems(mapping, [bo1], [bo1], [])
        verifyCalculateModificationItems(mapping, [bo1], [bo1, bo2], [[ADD, [bp2]]])
        verifyCalculateModificationItems(mapping, [bo1, bo2], [bo1], [[REM, [bp2]]])
        verifyCalculateModificationItems(mapping, null, [bo1], [[ADD, [bp1]]])
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
    
    void testCustomToFieldTypeMapper() {
        def mapping = mappingForField("plusTwo")
        verifyProperties(mapping, "plusTwo", "Integer", Set)
        verifyMapFromContext(mapping, ["1", "2", "3"], [3, 4, 5] as Set)
    }

    void testCustomToTypeTypeMapper() {
        def mapping = mappingForField("lc")
        verifyProperties(mapping, "lc", "LowerCaseString", Set)
        verifyMapFromContext(mapping, ["A", "B", "C"], ["a", "b", "c"] as Set)
    }

    void verifyCalculateModificationItems(mapping, clean, dirty, mods) {
        super.verifyCalculateModificationItems(mapping, clean as Set, dirty as Set, mods)
    }
    
    void testBogusType() {
        shouldFail {
            mappingForField("badCollectionType")
        }
    }

    void testPlusTwo() {
        def mapping = mappingForField("plusTwo")
        verifyProperties(mapping, "plusTwo", "Integer")
        verifyTypeConversion(mapping, "2", 4)
        verifyMapFromContext(mapping, ["2", "4"], [4, 6] as Set)
        verifyCalculateModificationItems(mapping, [1, 2, 3], [1, 2, 3], []) // same
        
        verifyCalculateModificationItems(mapping, null, [2, 4], [[ADD, ["0", "2"]]]) // from null
        verifyCalculateModificationItems(mapping, [], [2, 4], [[ADD, ["0", "2"]]]) // from emptylist
        
        verifyCalculateModificationItems(mapping, [1, 2, 3], null, [[REM, null]]) // to null
        verifyCalculateModificationItems(mapping, [1, 2, 3], [], [[REM, null]]) // to emptylist
        
        verifyCalculateModificationItems(mapping, [1, 2, 3], [1, 3], [[REM, "0"]]) // remove 1
        verifyCalculateModificationItems(mapping, [1, 3], [1, 2, 3], [[ADD, "0"]]) // add 1

        verifyCalculateModificationItems(mapping, [1, 2, 3], [1], [[REM, ["0", "1"]]]) // remove 2
        verifyCalculateModificationItems(mapping, [1], [1, 2, 3], [[ADD, ["0", "1"]]]) // add 2
        
        verifyCalculateModificationItems(mapping, [], [1], [[ADD, ["-1"]]])
        
        verifyCalculateModificationItems(mapping, [1, 3], [1, 2], [[REM, "1"], [ADD, "0"]])
        
        verifyCalculateModificationItems(mapping, [1], [], [[REM, null]])
    }

}

class MultiValueAttributeMappingTestSubject {
    Set stringSet
    
    SortedSet<Integer> sortedIntSet
    
    @GldapoPseudoType("Integer")
    Set pseudoTypeSet
    
    LinkedList badCollectionType
    
    Set<Integer> plusTwo
    
    static mapToPlusTwoField(value) {
        new Integer(value) + 2
    }

    static mapFromPlusTwoField(value) {
        value - 2 as String
    }
    
    @GldapoPseudoType("LowerCaseString")
    Set<String> lc
    
    static mapToLowerCaseStringType(value) {
        value.toString().toLowerCase()
    }
    
    Set<Byte[]> byteArrays
}