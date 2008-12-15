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
import gldapo.schema.attribute.AttributeMapping
import java.lang.annotation.Annotation

/**
 * The base for validators.
 * 
 * If this validator was instanciated as the result of a constraint annotation, the 
 * properties of that annotation instance will be contained in the config map.
 * 
 * @see #getConfig()
 * @see #getAttributeMapping()
 * @see #validate(Object)
 */
abstract class AbstractAttributeValidator implements AttributeValidator {
    
    Map config
    AttributeMapping attributeMapping 
    
    /**
     * Whether or not to treat null values as valid.
     * 
     * @return {@code true}
     */
    def getSkipNull() { true }
    
    /**
     * Whether or not to treat empty collections (multivalue attributes) as valid.
     * 
     * @return {@code true}
     */
    def getSkipEmpty() { true }
    
    /**
     * The implementation in this class does nothing.
     */
    void init() throws Exception {}
    
    /**
     * Validate an attribute.
     * 
     * If the value is valid, {@code null} MUST be returned. If the value is invalid, either return a 
     * single string error code or a list of string error codes.
     * 
     * This implementation calls {@link #validateMultiValue(Collection)} if {@code obj} is a {@link Collection},
     * otherwise {@link #validateSingleValue(Object)}, unless {@link getSkipNull()} returns {@code true} and {@code obj}
     * is {@code null} in which case {@code null} is returned immediately.
     * 
     * @return {@code null} if {@code obj} is valid, otherwise a single or list of error codes.
     */
    def validate(obj) {
        if (this.skipNull && obj == null) return null
        (obj instanceof Collection) ? validateMultiValue(obj) : validateSingleValue(obj)
    }
    
    /**
     * Validate a multi value attribute.
     * 
     * This implementation calls {@link validateValue(Object)} for each value in the collection
     * and returns all the error codes returned, unless {@link getSkipEmpty()} returns {@code true} and {@code obj}
     * is empty in which case {@code null} is returned immediately.
     * 
     * @return {@code null} if {@code obj} is valid, otherwise a single or list of error codes.
     */
    protected validateMultiValue(Collection obj) {
        if (this.skipEmpty && obj.empty) return null
        def codes = []
        obj.each {
            def c = validateValue(it)
            (c instanceof Collection) ? c.each { codes << it } : c << it
        }
        (codes) ?: null
    }
    
    /**
     * Validate a single value attribute.
     * 
     * Passes through to {@link validateValue(Object)}.
     * 
     * @return {@code null} if {@code obj} is valid, otherwise a single or list of error codes.
     */
    protected validateSingleValue(obj) {
        validateValue(obj)
    }
    
    /**
     * Used to actually validate a single value of an attribute.
     * 
     * Most subclasses should only need to override this method. This implementation just returns {@code null}.
     * 
     * @return {@code null} if {@code obj} is valid, otherwise a single or list of error codes.
     */
    protected validateValue(obj) {
        null
    }
}
