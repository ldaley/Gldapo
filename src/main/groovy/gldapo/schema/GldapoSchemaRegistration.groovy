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
package gldapo.schema;
import gldapo.schema.attribute.AttributeMapping
import gldapo.schema.attribute.AttributeMappingInspector

class GldapoSchemaRegistration 
{
	private schema
	private attributeMappings
	
	GldapoSchemaRegistration(Class schema)
	{
		this.schema = schema
		this.attributeMappings = AttributeMappingInspector.getAttributeMappings(schema)
	}
	
	Class getSchema()
	{
		return this.schema
	}
	
	List<AttributeMapping> getAttributeMappings()
	{
		return this.attributeMappings
	}
}
