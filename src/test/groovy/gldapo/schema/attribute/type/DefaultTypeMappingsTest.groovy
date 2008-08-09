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
package gldapo.schema.attribute.type

import java.math.BigInteger
import java.math.BigDecimal

class DefaultTypeMappingsTest extends GroovyTestCase 
{
    void testStringMapping() 
    {
        def m = DefaultTypeMappings.mapToStringType("s")
        assertEquals(String, m.class)
        assertEquals("s", m)
    }
    
    void testIntegerMapping()
    {
        def m = DefaultTypeMappings.mapToIntegerType("3")
        assertEquals(Integer, m.class)
        assertEquals(3, m)
    }
    
    void testBigIntegerMapping()
    {
        def m = DefaultTypeMappings.mapToBigIntegerType("3")
        assertEquals(BigInteger, m.class)
        assertEquals(new BigInteger("3"), m)
    }
    
    void testBigDecimalMapping() {
        def m = DefaultTypeMappings.mapToBigDecimalType("2.3")
        assertEquals(BigDecimal, m.class)
        assertEquals(new BigDecimal("2.3"), m)
    }
    
    void testDoubleMapping() {
        def m = DefaultTypeMappings.mapToDoubleType("2.3")
        assertEquals(Double, m.class)
        assertEquals(new Double("2.3"), m)
    }
    
    void testFloatMapping() {
        def m = DefaultTypeMappings.mapToFloatType("2.3")
        assertEquals(Float, m.class)
        assertEquals(new Float("2.3"), m)
    }
    
    void testLongMapping() {
        def m = DefaultTypeMappings.mapToLongType("123")
        assertEquals(Long, m.class)
        assertEquals(new Long("123"), m)
    }
    
    void testShortMapping() {
        def m = DefaultTypeMappings.mapToShortType("123")
        assertEquals(Short, m.class)
        assertEquals(new Short("123"), m)
    }
    
    void testBooleanMapping() {
        def m1 = DefaultTypeMappings.mapToBooleanType(true)
        assertEquals(Boolean, m1.class)
        assertEquals(new Boolean("TRUE"), m1)
        assertEquals(new Boolean("true"), m1)
        
        def m2 = DefaultTypeMappings.mapToBooleanType(false)
        assertEquals(Boolean, m2.class)
        assertEquals(new Boolean("FALSE"), m2)
        assertEquals(new Boolean("false"), m2)
    }
}