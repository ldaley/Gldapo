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

class CleanValuesInjecto {

    @InjectoProperty(write = false)
    Map cleanValues = [:]
    
    def setCleanValue = { String name, Object value ->
        delegate.cleanValues[name] = value
    }

    def getCleanValue = { String name ->
        delegate.cleanValues[name]
    }

    def snapshotStateAsClean = {
        delegate.class.schemaRegistration.attributeMappings.each { attribute, mapping ->
            def value = delegate."$attribute"
            delegate.setCleanValue(attribute, (value instanceof Cloneable) ? value.clone() : value)
        }
    }
    
    def revert = { ->
        delegate.cleanValues.each { key, value ->
            delegate."$key" = (value instanceof Cloneable) ? value.clone() : value
        }
    }
}