package gldapo;
import gldapo.exception.GldapoNoDefaultTemplateException
import gldapo.exception.GldapoException

class GldapoTemplateRegistry 
{
	static final CONFIG_TEMPLATES_KEY = 'templates'
	static final CONFIG_DEFAULT_TEMPLATE_KEY = 'defaultTemplate'
	
	Map templates = [:]
	String defaultTemplateName
	
	GldapoTemplate getDefaultTemplate()
	{
		if (defaultTemplateName == null) throw new GldapoNoDefaultTemplateException()
		if (!templates.containsKey(defaultTemplateName)) throw new GldapoException("The default template name of '${defaultTemplateName} does not match any registered template")
		return templates[defaultTemplateName]
	}
	
	static newFromConfig(ConfigObject config)
	{
		def registry = this.newInstance()
		
		config[CONFIG_TEMPLATES_KEY]?.each { def templateName, def templateConfig -> 
			registry.templates[templateName] = GldapoTemplate.templateFromConfig(templateConfig)
		}
		
		if (config[CONFIG_DEFAULT_TEMPLATE_KEY] != null) registry.defaultTemplateName = config[CONFIG_DEFAULT_TEMPLATE_KEY]

		return registry
	}
	
}