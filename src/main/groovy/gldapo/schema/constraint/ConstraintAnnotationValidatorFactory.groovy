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
import gldapo.schema.constraint.annotation.Validator

class ConstraintAnnotationValidatorFactory implements ConstraintValidatorFactory {

    def types = [:]
    
    boolean registers(Object constraintType) {
        constraintType instanceof Class && (constraintType.interfaces.find { Annotation.isAssignableFrom(it) } != null)
    }
    
    boolean registered(Object constraintType) {
        types.containsKey(constraintType)
    }
    
    void register(Object constraintType) {
        types[constraintType] = constraintType.getAnnotation(Validator).value()
    }
    
    Collection<AttributeValidator> createAttributeValidators(AttributeMapping attributeMapping) {
        def validators = []
        attributeMapping.field.annotations.each { 
            def type = types[it.annotationType()]
            if (type) {
                def validator = type.newInstance()
                validator.config = ConstraintAnnotationPropertyInspector.inspect(it)
                validator.attributeMapping = attributeMapping
                validator.init()
                validators << validator
            }
        }
        validators
    }

}