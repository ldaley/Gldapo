package gldapo;
import org.springframework.ldap.LdapTemplate
import org.springframework.ldap.support.LdapContextSource
import javax.naming.directory.SearchControls

class GldapoTemplate extends LdapTemplate
{
	static final CONFIG_CONTEXT_SOURCE_KEY = 'contextSource'
	static final CONFIG_SEARCH_CONTROLS_KEY = 'searchControls'
	static final CONFIG_TEMPLATE_BASE_KEY = 'templateBase'
	
	static public final searchControlOptions = [
		"countLimit", "derefLinkFlag", "returningAttributes", 
		"returningObjFlag", "searchScope", "timeLimit"
	]
	
	SearchControls searchControls
	String templateBase
	
	SearchControls getSearchControls()
	{
		if (searchControls == null) searchControls = new SearchControls()
		return searchControls
	}
	
	static templateFromConfig(ConfigObject config)
	{
		template = new GldapoTemplate()
		
		def contextSourceConfig = config[CONFIG_CONTEXT_SOURCE_KEY]
		if (contextSourceConfig)
		{
			contextSource = new LdapContextSource()
			contextSourceConfig.each { def key, def value ->
				contextSource."${key}" = value
			}
			contextSource.afterPropertiesSet()
			template.contextSource = contextSource
		}
		
		def searchControlsConfig = config[CONFIG_SEARCH_CONTROLS_KEY]
		if (searchControlsConfig)
		{
			searchControls = new SearchControls()
			searchControlsConfig.each { def key, def value ->
				searchControls."${key}" = value
			}
			template.searchControls = searchControls
		}
		
		template.templateBase = config[CONFIG_TEMPLATE_BASE_KEY]
		template.afterPropertiesSet()
		
		return template
	}
	
	SearchControls mergeSearchControlsWithOptions(def options)
	{
		def controls = delegate.searchControls
		if (options != null)
		{
			this.class.searchControlOptions.each {
				if (options[it] != null)
				{
					classControls."${it}" = options[it]
				}
			}
		}
		
		return controls
	}
}