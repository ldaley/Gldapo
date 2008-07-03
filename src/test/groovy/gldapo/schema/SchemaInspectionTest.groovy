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
import gldapo.schema.attribute.*
import gldapo.schema.annotation.GldapoNamingAttribute
import gldapo.exception.GldapoNoNamingAttributeException
import gldapo.Gldapo

class SchemaInspectionTest extends GroovyTestCase
{    
    def inspection = new SchemaInspection(SchemaInspectionTestSubject, new Gldapo())
    def mappings = inspection.attributeMappings

    void testCorrectNumberOfMappings()
    {
        assertEquals(3, mappings.size())
    }
    
    void testSimpleSingleValue() 
    {
        def m = mappings.simpleSingleValue
        assertNotNull(m)
        assertEquals(SingleValueAttributeMapping, m.class)
    }
    
    void testSimpleMultiValue() 
    {
        def m = mappings.simpleMultiValue
        assertNotNull(m)
        assertEquals(MultiValueAttributeMapping, m.class)
    }

    void testPrivateProperty()
    {
        assertNull(mappings.privateProperty)
    }
    
    void testPrivateReadOnly()
    {
        assertNull(mappings.privateReadOnly)
    }
    
    void testPrivateWriteOnly()
    {
        assertNull(mappings.privateWriteOnly)
    }
    
    void testPrivateReadWrite()
    {
        assertNotNull(mappings.privateReadWrite)
    }
    
    void testNamingAttribute() {
        assertEquals("name", inspection.namingAttributeFieldName)
    }
    
    void testNoNamingAttributeThrowsException() {
        shouldFail(GldapoNoNamingAttributeException) {
            new SchemaInspection(SchemaInspectionTestNoNamingAttributeSubject, new Gldapo())
        }
    }
}

class SchemaInspectionTestNoNamingAttributeSubject{}

class SchemaInspectionTestSubject
{
    @GldapoNamingAttribute
    String name

    String simpleSingleValue
    
    Set<String> simpleMultiValue
    
    private privateProperty
    
    private privateReadOnly
    def getPrivateReadOnly() { }
    
    private privateWriteOnly
    void setPrivateWriteOnly(Object it) { }
        
    String privateReadWrite
    def getPrivateReadWrite() { null }
    void setPrivateReadWrite(String it) { }
    
    static staticAttribute
}