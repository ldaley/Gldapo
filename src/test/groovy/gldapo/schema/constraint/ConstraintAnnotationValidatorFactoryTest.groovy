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
package gldapo.schema.constraint

import gldapo.schema.attribute.AttributeMapping
import gldapo.schema.attribute.validator.AttributeValidator
import java.lang.annotation.Annotation
import gldapo.schema.annotation.NullOnly
import gldapo.schema.constraint.annotation.Validator

class ConstraintAnnotationValidatorFactoryTest extends GroovyTestCase {

    def factory
    
    void setUp() {
        factory = new ConstraintAnnotationValidatorFactory()
    }
    
    void testRegisters() {
        assertTrue(factory.registers(NullOnly))
        assertFalse(factory.registers(String))
    }
    
    void testRegisterAndRegistered() {
        factory.register(NullOnly)
        assertTrue(factory.registered(NullOnly))
        assertFalse(factory.registered(String))
    }
    
    void testCreateAttributeValidators() {
        factory.register(NullOnly)
        def f = ConstraintAnnotationValidatorFactoryTestSubject.getDeclaredField('a')
        def v = factory.createAttributeValidators(
            [getField: {f}] as AttributeMapping
        )
        assertEquals(1, v.size())
        assertEquals(NullOnly.getAnnotation(Validator).value(), v[0].class)
        assertNotNull(v[0].config)
    }
}

class ConstraintAnnotationValidatorFactoryTestSubject {

    @NullOnly
    def a

}