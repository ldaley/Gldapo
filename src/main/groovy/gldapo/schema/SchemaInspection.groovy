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
import gldapo.schema.attribute.*
import gldapo.schema.annotation.GldapoNamingAttribute
import gldapo.exception.GldapoNoNamingAttributeException
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import gldapo.Gldapo
import org.apache.commons.lang.StringUtils

class SchemaInspection 
{
    static excludedFields = ["metaClass"]
    
    def schema
    def gldapo
    def attributeMappings = [:]
    def namingAttributeFieldName
    
    SchemaInspection(Class schema, Gldapo gldapo) {
        this.schema = schema
        this.gldapo = gldapo
        
        this.schema.declaredFields.each {
            def mapping = getMappingForField(it)
            if (mapping) 
                attributeMappings[it.name] = mapping
            
            if (it.getAnnotation(GldapoNamingAttribute))
                this.namingAttributeFieldName = it.name
        }
        
        if (namingAttributeFieldName == null)
            throw new GldapoNoNamingAttributeException(this.schema)
    }
    
    private getMappingForField(Field field)
    {
        if (Modifier.isStatic(field.modifiers) || 
            excludedFields.contains(field.name) || 
            !fieldIsReadableAndWritable(field)
        ) { 
            return null
        }
        
        getAttributeMappingClassForField(field).newInstance(schema, field, gldapo.typemappings)
    }
    
    private getAttributeMappingClassForField(field) {
        (Collection.isAssignableFrom(field.type)) ? MultiValueAttributeMapping : SingleValueAttributeMapping
    }

    private fieldIsReadableAndWritable(Field field)
    {
        def capitalisedFieldName = StringUtils.capitalize(field.name)
        def mc = this.schema.metaClass
        mc.hasMetaMethod("get${capitalisedFieldName}") && mc.hasMetaMethod("set${capitalisedFieldName}", field.type)
    }
}