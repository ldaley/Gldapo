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
package gldapo.schema.injecto
import injecto.annotation.InjectoProperty
import gldapo.exception.GldapoException
import gldapo.schema.attribute.MultiValueAttributeMapping

class CleanValuesInjecto {

    Map _cleanValues = [:]
    
    def _setCleanValue = { String name, Object value ->
        delegate._cleanValues[name] = value
    }

    def _getCleanValue = { String name ->
        delegate._cleanValues[name]
    }
    
    def _refreshCleanValues = {
        delegate.class.schemaRegistration.attributeMappings.each { attribute, mapping ->
            def value = delegate."$attribute"
            if (value == null) {
                delegate._setCleanValue(attribute, null)
            } else {
                if (mapping instanceof MultiValueAttributeMapping) {
                    delegate._setCleanValue(attribute, value.clone())
                } else {
                    delegate._setCleanValue(attribute, value)
                }
            }            
        }
    }
}