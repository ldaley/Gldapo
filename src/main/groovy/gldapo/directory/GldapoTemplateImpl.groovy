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
package gldapo.template;

import gldapo.schema.attribute.GldapoContextMapper
import gldapo.exception.GldapoException
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

class GldapoTemplateImpl extends LdapTemplate implements GldapoTemplate, BeanNameAware
{
	static final CONFIG_CONTEXT_SOURCE_KEY = 'contextSource'
	static final CONFIG_SEARCH_CONTROLS_KEY = 'searchControls'
	
	/**
	 * 
	 */
	GldapoSearchControls searchControls
	
	/**
	 * 
	 */
	String beanName
	
	/**
	 * Simply retrieves the property from the context source
	 */
	String getBaseDN()
	{
		contextSource.baseDN
	}
	/**
	 * Here to make sure we are using GldapoContextSources
	 */
	void setContextSource(GldapoContextSource contextSource)
	{
		super(contextSource)
	}
	
	/**
	 * @todo Use a better exception here
	 */
	void setContextSource(LdapContextSource contextSource)
	{
		throw new IllegalArgumentException("Can't use LdapContextSource with GldapoTemplates")
	}
	
	List search(Class schema, String base, String filter, GldapoSearchControls controls, Integer pageSize)
	{
		ContextMapper mapper = new GldapoContextMapper(schema: delegate)
		ContextMapperCallbackHandler handler = new ContextMapperCallbackHandler(mapper)

		SearchControls jndiControls = controls as SearchControls
		jndiControls.returningAttributes = schema.attributeMappings*.attributeName
		
		try
		{
			PagedResultsRequestControl requestControl = new PagedResultsRequestControl(pageSize)
			template.search(base, filter, jndiControls, handler, requestControl)
		
			while (requestControl?.cookie?.cookie != null)
			{
				requestControl = new PagedResultsRequestControl(pageSize, requestControl.cookie)
				template.search(base, filter, jndiControls, handler, requestControl)
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
	
	/**
	 * @todo Implement tighter checking that the config parameters are valid, for spelling mistakes and such
	 */
	static newInstance(String name, ConfigObject config)
	{
		def template = this.newInstance()
		
		template.beanName = name
		
		if (config.containsKey(CONFIG_CONTEXT_SOURCE_KEY))
		{
			template.contextSource = GldapoContextSourceImpl.newInstance(config[CONFIG_CONTEXT_SOURCE_KEY])
		}
		else
		{
			throw new GldapoInvalidConfigException("Template '${template.beanName}' does not define a context source")
		}
		
		if (config.containsKey(CONFIG_SEARCH_CONTROLS_KEY))
		{
			template.searchControls = GldapoSearchControlsImpl.newInstance(config[CONFIG_SEARCH_CONTROLS_KEY])
		}
		else
		{
			template.searchControls = new GldapoSearchControlsImpl()
		}

		template.afterPropertiesSet()
		return template
	}
}