package gldapo;
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource
import javax.naming.directory.SearchControls

class GldapoTemplate extends LdapTemplate
{
	static final CONFIG_CONTEXT_SOURCE_KEY = 'contextSource'
	static final CONFIG_SEARCH_CONTROLS_KEY = 'searchControls'
	static final CONFIG_TEMPLATE_BASE_KEY = 'base'
		
	SearchControls searchControls
	String base
	
	static templateFromConfig(ConfigObject config)
	{
		def template = this.newInstance()
		
		def contextSourceConfig = config[CONFIG_CONTEXT_SOURCE_KEY]
		if (contextSourceConfig)
		{
			def contextSource = new LdapContextSource()
			contextSourceConfig.each { def key, def value ->
				contextSource."${key}" = value
			}
			contextSource.afterPropertiesSet()
			template.contextSource = contextSource
		}
		
		def searchControlsConfig = config[CONFIG_SEARCH_CONTROLS_KEY]
		if (searchControlsConfig)
		{
			def searchControls = new SearchControls()
			searchControlsConfig.each { def key, def value ->
				searchControls."${key}" = value
			}
			template.searchControls = searchControls
		}
		
		template.base = config[CONFIG_TEMPLATE_BASE_KEY]
		template.afterPropertiesSet()
		
		return template
	}
}