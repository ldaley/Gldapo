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
import gldapo.Gldapo
import gldapo.support.GldapoTemplate
import injecto.annotation.InjectoDepenedencies
import org.springframework.ldap.filter.Filter
import gldapo.schema.attribute.GldapoContextMapper
import org.springframework.ldap.core.LdapOperations
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler
import org.springframework.ldap.core.AttributesMapperCallbackHandler
import org.springframework.ldap.control.PagedResultsRequestControl
import org.springframework.ldap.core.AttributesMapper
import org.springframework.ldap.LimitExceededException
import gldapo.exception.GldapoException
import gldapo.util.SearchControlsMerger

@InjectoDependencies([PropertyAttributeMappingInjecto, SchemaFilterInjecto, TypeConversionInjecto])
class FindAllInjecto 
{
	static DEFAULT_PAGE_SIZE = 500
	
	static findAll = { Map options ->
		
		def template 
		if (options?.template instanceof String)
		{
			template = Gldapo.templateRegistry[options.template]
		}
		else if (options?.template != null)
		{
			template = options.template
		}
		else
		{
			template = Gldapo.templateRegistry.defaultTemplate
		}

		def filter = delegate.andSchemaFilterWithFilter(options?.filter)
		def controls = SearchControlsMerger.merge(template.searchControls, options)
		int pageSize = options?.pageSize == null ? FindGynamo.DEFAULT_PAGE_SIZE : options.pageSize
		def base = options?.base
		
		// Restrict the returning attributes to just those specified in the schema
		controls.returningAttributes = delegate.attributeMappings*.name
		
		ContextMapper mapper = new GldapoContextMapper(schema: delegate)
		CollectingNameClassPairCallbackHandler handler = new AttributesMapperCallbackHandler(mapper)

		try
		{
			PagedResultsRequestControl requestControl = new PagedResultsRequestControl(pageSize)
			template.search(base, filter, controls, handler, requestControl)
			
			while (requestControl?.cookie?.cookie != null)
			{
				requestControl = new PagedResultsRequestControl(pageSize, requestControl.cookie)
				template.search(base, filter, controls, handler, requestControl)
			} 
	
			return handler.list
		}
		catch (LimitExceededException e)
		{
			// If the number have entries has hit the specified count limit OR
			// The server is unwilling to send more entries we will get here.
			// It's not really an error condition hence we just return what we found.
			
			return handler.list
		}
	}
}