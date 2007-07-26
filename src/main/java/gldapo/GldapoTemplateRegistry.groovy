package gldapo;
import gldapo.exception.GldapoNoDefaultTemplateException
import gldapo.exception.GldapoException
import gldapo.exception.GldapoInvalidConfigException

class GldapoTemplateRegistry 
{
	static final CONFIG_TEMPLATES_KEY = 'templates'
	static final CONFIG_DEFAULT_TEMPLATE_KEY = 'defaultTemplate'
	
	List templates = []
	String defaultTemplateName
	
	GldapoTemplate getDefaultTemplate()
	{
		if (defaultTemplateName == null) throw new GldapoNoDefaultTemplateException()
		def defaultTemplate = this[defaultTemplateName]
		if (defaultTemplate == null) throw new GldapoException("The default template name of '${defaultTemplateName} does not match any registered template")
		return defaultTemplate
	}
	
	static newFromConfig(ConfigObject config)
	{
		def registry = this.newInstance()
		
		config[CONFIG_TEMPLATES_KEY]?.each { def templateName, def templateConfig -> 
			registry << GldapoTemplate.newFromConfig(templateName, templateConfig)
		}
		
		if (config[CONFIG_DEFAULT_TEMPLATE_KEY] != null) registry.defaultTemplateName = config[CONFIG_DEFAULT_TEMPLATE_KEY]

		return registry
	}
	
	void leftShift(GldapoTemplate template)
	{
		templates << template
	}
	
	GldapoTemplate getAt(String name)
	{
		def template = templates.find { it.beanName.equals(name) }
		if (template == null) throw new GldapoException("There is no template registered by the name of ${name}")
		return template
	}
}