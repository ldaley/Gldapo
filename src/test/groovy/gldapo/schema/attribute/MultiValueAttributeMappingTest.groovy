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

class MultiValueAttributeMappingTest extends AbstractAttributeMappingTest
{
    def mappingClass = MultiValueAttributeMapping
    def mappingSubjectClass = MultiValueAttributeMappingTestSubject
    
    def getFakeContext(val)
    {
        new Expando(getStringAttributes: { return val as String[] })
    }

    void doMappingTest(fieldName, attributeName, typeMapping, collectionType, contextValue, mappedValue) 
    {
        def m = mappingForField(fieldName)
        verifyMapping(m, fieldName, attributeName, typeMapping, collectionType, contextValue, mappedValue)
    }
    
    void verifyMapping(mapping, fieldName, attributeName, typeMapping, collectionType, contextValue, mappedValue)
    {
        verifyMapping(mapping, fieldName, attributeName, typeMapping, contextValue, mappedValue)
        assertTrue("Collection type test", collectionType.isAssignableFrom(mapping.collectionType))
    }
    
    void testStringList() 
    {
        doMappingTest("stringList", "stringList", "String", List, ["1", "2", "3"], ["1", "2", "3"])
    }
    
    void testSortedIntSet() 
    {
        doMappingTest("sortedIntSet", "sortedIntSet", "Integer", Set, ["3", "1", "2"], [1, 2, 3] as Set)
    }
    
    void testPseudoTypeSet() 
    {
        doMappingTest("pseudoTypeSet", "pseudoTypeSet", "Integer", Set, ["1", "2", "3"], [1, 2, 3] as Set)
    }
    
    void testBogusType()
    {
        shouldFail {
            mappingForField("badCollectionType")
        }
    }
}

class MultiValueAttributeMappingTestSubject
{
    List stringList
    
    SortedSet<Integer> sortedIntSet
    
    @GldapoPseudoType("Integer")
    Set pseudoTypeSet
    
    LinkedList badCollectionType
}