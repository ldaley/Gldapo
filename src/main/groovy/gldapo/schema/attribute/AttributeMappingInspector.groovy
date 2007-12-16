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

class AttributeMappingInspector 
{
	static excludedFields = ["metaClass"]
	
	static Map getAttributeMappings(Class schema)
	{
		def mappings = [:]
		schema.declaredFields.each {
			def mapping = getMappingForField(schema, it)
			if (mapping) mappings[it.name] = mapping
		}
		return mappings
	}
	
	static getMappingForField(Class schema, Field field)
	{
		if (excludedFields.contains(field.name) || !fieldIsReadableAndWritable(schema, field)) return null
		
		if (Collection.isAssignableFrom(field.type))
		{
			return new MultiValueAttributeMapping(schema, field)
		}
		else
		{
			return new SingleValueAttributeMapping(schema, field)
		}
	}
	
	static fieldIsReadableAndWritable(Class schema, Field field)
	{
		def capitalisedFieldName = (field.name.size() > 1) ? field.name[0].toUpperCase() + field.name[1..field.name.size() - 1] : field.name[0].toUpperCase()
		return (schema.metaClass.hasMetaMethod("get${capitalisedFieldName}") && schema.metaClass.hasMetaMethod("set${capitalisedFieldName}", field.type))
	}
}