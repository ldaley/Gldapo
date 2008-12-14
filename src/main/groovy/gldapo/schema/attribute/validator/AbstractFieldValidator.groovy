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
package gldapo.schema.attribute.validator
import org.springframework.beans.factory.InitializingBean
import java.lang.reflect.Field
import java.lang.annotation.Annotation
import gldapo.schema.constraint.InvalidConstraintException

/**
 * Base class for field validators. Each constraint annotation creates a new instance of it's
 * associated validator which is bound specifically to the field under constraint.
 * 
 * Subclasses must at least implement {@link #validate(Object)}. The instance of the constraint
 * that instanciated this validator is available via the {@link #getConstraint() constraint} property.
 * The {@link Field field} that this validator is going to be validating is available as the {@link #getField() field} property. 
 * 
 * After instanciation, the {@link #afterPropertiesSet()} will be called. If the constraint values are invalid, or this
 * validator is not valid for the type of field it is applied to or if any other kind of problem exists, a 
 * {@link InvalidConstraintException} should be thrown.
 */
abstract class AbstractFieldValidator implements InitializingBean {
    
    Annotation constraint
    Field field 
    
    /**
     * The instance of the constraint annotation that created this validator.
     */
    Annotation getConstraint() {
        this.constraint
    }
    
    /**
     * The underlying field that we will be validating values of.
     */
    Field getField() {
        this.field
    }
    
    /**
     * If the constraint values are invalid or the type of field is invalid, a 
     * {@link InvalidConstraintException} should be thrown.
     * 
     * The implementation in this class does nothing.
     */
    void afterPropertiesSet() throws InvalidConstraintException {}
    
    /**
     * Will be called with a field value to validate.
     * 
     * If the value is valid, {@code null} MUST be returned. If the value is invalid, either return a 
     * single string error code or a list of string error codes.
     * 
     * @return {@code null} if {@code obj} is valid, otherwise 1 or a list of error codes.
     */
    abstract validate(obj)
}
