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

class DelegatingConstraintValidatorFactory implements ConstraintValidatorFactory {

    final delegates = [new ConstraintAnnotationValidatorFactory()]
    
    boolean registers(Object constraintType) {
        delegates.find { it.registers(constraintType) } != null
    }
    
    boolean registered(Object constraintType) {
        delegates.find { it.registered(constraintType) } != null
    }
    
    void register(Object constraintType) {
        def delegate = delegates.find { it.registers(constraintType) }
        if (delegate == null) throw new IllegalStateException("Could not find a ConstraintValidatorFactory to register ${constraintType} with")
        delegate.register(constraintType)
    }
    
    Collection<AttributeValidator> createAttributeValidators(AttributeMapping attributeMapping) {
        def validators = []
        delegates.each {
            it.createAttributeValidators(attributeMapping)?.each {
                validators << it
            }
        }
        validators
    }

}