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
import gldapo.schema.annotation.GldapoPseudoType
import gldapo.schema.annotation.GldapoSynonymFor
import javax.naming.directory.DirContext

class SingleValueAttributeMappingTest extends AbstractAttributeMappingTest
{
    def mappingClass = SingleValueAttributeMapping
    def mappingSubjectClass = SingleValueAttributeMappingTestSubject
    
    static REP = DirContext.REPLACE_ATTRIBUTE
    static REM = DirContext.REMOVE_ATTRIBUTE 
    static ADD = DirContext.ADD_ATTRIBUTE
    
    def getFakeContext(val) {
        new Expando(getObjectAttribute: { return val })
    }
    
    void testSimpleString() {
        def mapping = mappingForField("simpleString")
        verifyProperties(mapping, "simpleString", "String")
        verifyTypeConversion(mapping, "test", "test")
        verifyMapFromContext(mapping, "test", "test")
        verifyCalculateModificationItems(mapping, "a", "b", [[REP, "b"]])
        verifyCalculateModificationItems(mapping, "a", "a", null)
        verifyCalculateModificationItems(mapping, "a", null, [[REM, null]])
        verifyCalculateModificationItems(mapping, null, "a", [[ADD, "a"]])
    }

    void testPlusTwo() {
        def mapping = mappingForField("plusTwo")
        verifyProperties(mapping, "plusTwo", "Integer")
        verifyTypeConversion(mapping, "2", 4)
        verifyMapFromContext(mapping, "2", 4)
        verifyCalculateModificationItems(mapping, 2, 4, [[REP, "2"]])
        verifyCalculateModificationItems(mapping, 2, 2, null)
        verifyCalculateModificationItems(mapping, 2, null, [[REM, null]])
        verifyCalculateModificationItems(mapping, null, 4, [[ADD, "2"]])
    }

    void testPseudoType() {
        def mapping = mappingForField("pseudoType")
        verifyProperties(mapping, "pseudoType", "Integer")
        verifyTypeConversion(mapping, "3", 3)
    }
    
    void testSynonym() {
        def mapping = mappingForField("synonym")
        verifyProperties(mapping, "other", "String")
    }
    
    void testCustomToFieldTypeMapper() {
        def mapping = mappingForField("plusTwo")
        verifyProperties(mapping, "plusTwo", "Integer")
        verifyMapFromContext(mapping, "1", 3)
    }
    
    void testCustomToTypeTypeMapper() {
        def mapping = mappingForField("lc")
        verifyProperties(mapping, "lc", "LowerCaseString")
        verifyMapFromContext(mapping, "A", "a")
    }
    
    void testBogusType() {
        shouldFail() {
            mappingForField("bogusType")
        }
    }
}

class SingleValueAttributeMappingTestSubject
{
    String simpleString
    
    @GldapoPseudoType("Integer")
    String pseudoType
    
    @GldapoSynonymFor("other")
    String synonym
    
    @GldapoPseudoType("BogusType")
    String bogusType
    
    Integer plusTwo
    
    static mapToPlusTwoField(value) {
        new Integer(value) + 2
    }

    static mapFromPlusTwoField(value) {
        (value == null) ? null : value - 2 as String
    }
    
    @GldapoPseudoType("LowerCaseString")
    String lc
    
    static mapToLowerCaseStringType(value) {
        value.toString().toLowerCase()
    }
}