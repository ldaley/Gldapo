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

class GldapoContextMapperTest extends GroovyTestCase 
{
    def registration = new GldapoSchemaRegistration(GldapoContextMapperTestSubject)

    def contextMapper = new GldapoContextMapper(schemaRegistration: registration)
        
    def fakeContext = new Expando([
        getStringAttribute: { assertEquals("attr1", it); return "attr1Value" },
        getStringAttributes: { assertEquals("attr2", it); return ["attr2Value1", "attr2Value2"] as String[] }
    ])
    
    void testMapFromContext() 
    {
        def o = contextMapper.mapFromContext(fakeContext)
        assertEquals("attr1Value", o.attr1)
        assertEquals(["attr2Value1", "attr2Value2"], o.attr2)
    }
}

class GldapoContextMapperTestSubject
{
    String attr1
    List<String> attr2
}