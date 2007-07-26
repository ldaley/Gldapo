package gldapo;
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource
import org.apache.commons.lang.WordUtils
import org.springframework.beans.factory.BeanNameAware

class GldapoTemplate extends LdapTemplate implements BeanNameAware
{
	static final CONFIG_CONTEXT_SOURCE_KEY = 'contextSource'
	static final CONFIG_SEARCH_CONTROLS_KEY = 'searchControls'
	static final CONFIG_TEMPLATE_BASE_KEY = 'base'
		
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
			def contextSource = new LdapContextSource()
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
}