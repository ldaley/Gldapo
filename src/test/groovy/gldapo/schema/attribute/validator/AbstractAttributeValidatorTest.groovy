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

abstract class AbstractAttributeValidatorTest extends GroovyTestCase {
    
    abstract getValidator()
    
    def createValidator(config, attributeMapping) {
        def v = this.validator.newInstance(
            config: config,
            attributeMapping: createAttributeMapping(attributeMapping)
        )
        v.init()
        v
    }
    
    def createAttributeMapping(values) {
        def attributeMapping = [:]
        values.each { k,v ->
            attributeMapping[k] = v 
        }
        attributeMapping as AttributeMapping
    }
}