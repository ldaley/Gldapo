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
package gldapo.schema.gynamo;
import gynamo.GynamoPropertyStorage
import gldapo.schema.attribute.GldapoAttributeMapping
import java.beans.Introspector

class AttributeMappingGynamo 
{
	static getAttributeMappings = { ->
		if (GynamoPropertyStorage[delegate].attributeMappings == null)
		{
			def mappings = []
			Introspector.getBeanInfo(delegate, Object).propertyDescriptors.each {
				if (it.name.equals("metaClass") == false)
				{
					mappings << new GldapoAttributeMapping(
						name: it.name,
						type: it.propertyType,
					)
				}
			}
			GynamoPropertyStorage[delegate].attributeMappings = mappings
		}
		return GynamoPropertyStorage[delegate].attributeMappings
	}	
}