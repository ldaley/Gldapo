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
import gldapo.GldapoTypeMappingRegistry
import gldapo.Gldapo

abstract class AbstractAttributeMappingTest extends GroovyTestCase 
{
    abstract def getMappingClass()
    abstract def getMappingSubjectClass()
    
    AbstractAttributeMappingTest() {
        gldapo.Gldapo.initialize([:])
    }
    
    def mappingForField(String fieldName)
    {
        this.mappingClass.getConstructor(Class, Field, GldapoTypeMappingRegistry).newInstance(
            this.mappingSubjectClass, 
            this.mappingSubjectClass.getDeclaredField(fieldName), 
            Gldapo.instance.typemappings
        )
    }
    
    def getFakeContext(val)
    {
        new Expando(getStringAttribute: { return val })
    }
    
    void doMappingTest(fieldName, attributeName, typeMapping, contextValue, mappedValue) 
    {
        def m = mappingForField(fieldName)
        verifyMapping(m, fieldName, attributeName, typeMapping, contextValue, mappedValue)
    }
    
    void verifyMapping(mapping, fieldName, attributeName, typeMapping, contextValue, mappedValue)
    {
        def e = new Expando()
        
        assertEquals(attributeName, mapping.attributeName)
        assertEquals(typeMapping, mapping.typeMapping)
        
        mapping.mapFromContext(getFakeContext(contextValue), e)
        assertEquals(mappedValue, e."$fieldName")
    }
}