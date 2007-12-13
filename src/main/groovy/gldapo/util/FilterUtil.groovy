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
package gldapo.util
import gldapo.schema.annotation.GldapoSchemaFilter

class FilterUtil 
{
	static final public NO_FILTER_FILTER = "(objectclass=*)"
		
	static getSchemaFilter(Class schemaClass)
	{
		def filterAnnotation = schemaClass.getAnnotation(GldapoSchemaFilter)
		if (filterAnnotation)
		{
			return filterAnnotation.value()
		}
		else
		{
			return NO_FILTER_FILTER
		}
	}
	
	static andSchemaFilterWithFilter(Class schemaClass, String filter)
	{
		String schemaFilter = schemaClass.schemaFilter
		if (filter == null)
		{
			if (schemaFilter == null || schemaFilter.equals(NO_FILTER_FILTER))
			{
				filter = NO_FILTER_FILTER
			}
			else
			{
				filter = schemaFilter
			}
		}
		else
		{
			if (schemaFilter != null && schemaFilter.equals(NO_FILTER_FILTER) == false)
			{
				filter = "(&${schemaFilter}${filter})"
			}	
		}
		
		return filter
	}
}