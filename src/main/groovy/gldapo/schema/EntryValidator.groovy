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
import org.springframework.validation.Validator
import org.springframework.validation.Errors
import org.springframework.validation.FieldError

class EntryValidator implements Validator {
    
    boolean supports(Class clazz) {
        try {
            clazz.schemaRegistration
            return true
        }
        catch(MissingPropertyException e) {
            return false
        }
    }
    
    void validate(Object obj, Errors e) {
        
        obj.attributeMappings.each { fieldName, attributeMapping ->
            def value = obj."${fieldName}"
            attributeMapping.validators.each {
                def codes = it.validate(value)
                if (codes) {
                    def args = [obj.class.simpleName, fieldName, obj] as String[]
                    if (!(codes instanceof List)) 
                        codes = [codes].collect {
                            [it, "${obj.class.simpleName}.$it", "${obj.class.simpleName}.$fieldName.$it"]
                        }.flatten()

                    e.addError(
                        new FieldError(e.objectName, fieldName, obj, false, codes as String[], args, "error")
                    ) 
                }
            }
        }
    }

}