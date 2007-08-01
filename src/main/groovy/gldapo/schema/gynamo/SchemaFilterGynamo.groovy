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
import gldapo.schema.annotations.GldapoSchemaFilter
import gynamo.GynamoPropertyStorage

class SchemaFilterGynamo 
{
	static final public NO_FILTER_FILTER = "(objectclass=*)"
		
	static getSchemaFilter = { ->
		def filterAnnotation = delegate.getAnnotation(GldapoSchemaFilter)
		if (filterAnnotation)
		{
			return filterAnnotation.value()
		}
		else
		{
			return SchemaFilterGynamo.NO_FILTER_FILTER
		}
	}
	
	static andSchemaFilterWithFilter = { String filter ->
		String schemaFilter = delegate.schemaFilter
		if (filter == null)
		{
			if (schemaFilter == null || schemaFilter.equals(SchemaFilterGynamo.NO_FILTER_FILTER))
			{
				filter = SchemaFilterGynamo.NO_FILTER_FILTER
			}
			else
			{
				filter = schemaFilter
			}
		}
		else
		{
			if (schemaFilter != null && schemaFilter.equals(SchemaFilterGynamo.NO_FILTER_FILTER) == false)
			{
				filter = "(&${schemaFilter}${filter})"
			}	
		}
		
		return filter
	}
}