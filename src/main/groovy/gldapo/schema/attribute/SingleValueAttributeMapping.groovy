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
package gldapo.schema.attribute
import java.lang.reflect.Field
import gldapo.GldapoTypeMappingRegistry
import javax.naming.directory.ModificationItem
import javax.naming.directory.BasicAttribute
import javax.naming.directory.DirContext


class SingleValueAttributeMapping extends AbstractAttributeMapping
{
    
    SingleValueAttributeMapping(Class schema, Field field, GldapoTypeMappingRegistry typemappings) {
        super(schema, field, typemappings)
    }
    
    protected calculateTypeMappingFromFieldType() {
         return this.field.type.simpleName
    }
    
    protected getGroovyValueFromContext(context) {
        def rawValue = context.getStringAttribute(this.attributeName)
        if (rawValue == null) {
            return null
        } else {
            return this.toGroovyTypeMapper.call(rawValue)
        }
    }
    
    protected calculateModificationItems(clean, dirty) {
        if (dirty != clean) {
            def dirtyAttribute = new BasicAttribute(this.attributeName, this.mapToLdapType(dirty))
            if (clean == null && dirty != null) {
                return [new ModificationItem(DirContext.ADD_ATTRIBUTE, dirtyAttribute)]
            } else if (clean != null && dirty == null) {
                return [new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(this.attributeName))]
            } else {
                return [new ModificationItem(DirContext.REPLACE_ATTRIBUTE, dirtyAttribute)]
            }
        }
        else {
            return null
        }
    }
    
    protected getAttribute(value) {
        new BasicAttribute(this.attributeName, this.mapToLdapType(value))
    }
}