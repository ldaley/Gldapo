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
package gldapo;
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource
import org.apache.commons.lang.WordUtils
import org.springframework.beans.factory.BeanNameAware

class GldapoTemplate extends LdapTemplate implements BeanNameAware, IGldapoTemplate
{
	static final CONFIG_CONTEXT_SOURCE_KEY = 'contextSource'
	static final CONFIG_SEARCH_CONTROLS_KEY = 'searchControls'
		
	Map searchControls
	String base
	String beanName
	
	/**
	 * @todo Implement tighter checking that the config parameters are valid, for spelling mistakes and such
	 */
	static newFromConfig(String name, ConfigObject config)
	{
		def template = this.newInstance()
		
		def contextSourceConfig = config[CONFIG_CONTEXT_SOURCE_KEY]
		if (contextSourceConfig)
		{
			def contextSource = new GldapoContextSource()
			contextSourceConfig.each { def key, def value ->
				contextSource."set${WordUtils.capitalize(key)}"(value)
			}
			contextSource.afterPropertiesSet()
			template.contextSource = contextSource
		}
		
		def searchControlsConfig = config[CONFIG_SEARCH_CONTROLS_KEY]
		if (searchControlsConfig)
		{
			def searchControls = [:]
			searchControlsConfig.each { def key, def value ->
				searchControls[key] = value
			}
			template.searchControls = searchControls
		}
		
		template.base = config[CONFIG_TEMPLATE_BASE_KEY]
		template.beanName = name
		template.afterPropertiesSet()
		
		return template
	}
	
	/**
	 * Here to make sure we are using GldapoContextSources
	 */
	void setContextSource(GldapoContextSource contextSource)
	{
		super(contextSource)
	}
	
	/**
	 * Simply retrieves the property from the context source
	 */
	String getBaseDN()
	{
		contextSource.baseDN
	}
}