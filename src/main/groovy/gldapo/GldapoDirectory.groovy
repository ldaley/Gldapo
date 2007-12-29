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
package gldapo

import gldapo.schema.GldapoContextMapper
import gldapo.exception.GldapoException
import gldapo.exception.GldapoInvalidConfigException
import gldapo.exception.GldapoInvalidConfigException
import gldapo.util.FilterUtil
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource
import org.springframework.ldap.filter.Filter
import org.springframework.ldap.core.LdapOperations
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler
import org.springframework.ldap.core.ContextMapperCallbackHandler
import org.springframework.ldap.control.PagedResultsRequestControl
import org.springframework.ldap.core.AttributesMapper
import org.springframework.ldap.LimitExceededException
import org.springframework.ldap.core.ContextMapper
import org.springframework.beans.factory.BeanNameAware
import javax.naming.directory.SearchControls

class GldapoDirectory implements BeanNameAware, GldapoSearchProvider
{
	static final CONFIG_SEARCH_CONTROLS_KEY = 'searchControls'
	
	static final CONTEXT_SOURCE_PROPS = ["url", "urls", "base", "userDn", "password"]
	
	/**
	 * 
	 */
	GldapoSearchControlProvider searchControls
	
	/**
	 * 
	 */
	String name
	
	/**
	 * 
	 */
	def template
	
	/**
	 * 
	 */
	GldapoDirectory(String name, Map config) {
	    if (config == null) throw new GldapoInvalidConfigException("Config for directory '$name' is null" as String)
	    this.name = name
		
		def contextSource = new LdapContextSource()
		CONTEXT_SOURCE_PROPS.each {
		    if (config.containsKey(it)) { 
		        contextSource."$it" = config."$it"
		    }
		}
		contextSource.afterPropertiesSet()
		
		this.template = new LdapTemplate(contextSource: contextSource)
		this.template.afterPropertiesSet()
		
		this.searchControls = new GldapoSearchControls(config[CONFIG_SEARCH_CONTROLS_KEY])
	}
	
	/**
	 * 
	 */
	void setBeanName(String beanName) {
		this.name = beanName
	}
	
	/**
	 * Simply retrieves the property from the context source
	 */
	String getBase()
	{
		template?.contextSource?.base as String
	}
	
	List search(Class schema, String base, String filter, GldapoSearchControlProvider controls)
	{
		def schemaRegistration = Gldapo.instance.schemas[schema]
		
		ContextMapper mapper = new GldapoContextMapper(schemaRegistration: schemaRegistration, directory: this)
		ContextMapperCallbackHandler handler = new ContextMapperCallbackHandler(mapper)

		SearchControls jndiControls = controls as SearchControls
		jndiControls.returningAttributes = schemaRegistration.attributeMappings*.attributeName
		
        if (controls.pageSize == null || pageSize < 1) {
            return nonPagedSearch(base, filter, jndiControls, handler)
        } else { 
            return pagedSearch(base, filter, jndiControls, handler, controls. pageSize)
        }
	}
	
	private List nonPagedSearch(base, filter, jndiControls, handler) {
		try {
			this.template.search(base, filter, jndiControls, handler)
		}
		catch (LimitExceededException e) {
			// If the number have entries has hit the specified count limit OR
			// The server is unwilling to send more entries we will get here.
			// It's not really an error condition hence we just return what we found.
		}
		return handler.list	   
	}
	
	private List pagedSearch(base, filter, jndiControls, handler, pageSize) {
	   	try
		{
			PagedResultsRequestControl requestControl = new PagedResultsRequestControl(pageSize)
			this.template.search(base, filter, jndiControls, handler, requestControl)
		
			while (requestControl?.cookie?.cookie != null)
			{
				requestControl = new PagedResultsRequestControl(pageSize, requestControl.cookie)
				this.template.search(base, filter, jndiControls, handler, requestControl)
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