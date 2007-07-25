package gldapo;
import gldapo.exception.GldapoNoDefaultTemplateException
import gldapo.exception.GldapoException

class GldapoTemplateRegistryTest extends GroovyTestCase 
{
	void testGetDefaultWhenIsNone() 
	{
		def registry = new GldapoTemplateRegistry()
		shouldFail (GldapoNoDefaultTemplateException) {
			def defaultTemplate = registry.defaultTemplate
		}
	}
	
	void testGetDefaultWhenDoesntExist() 
	{
		def registry = new GldapoTemplateRegistry()
		registry.defaultTemplateName = "abc"
		shouldFail (GldapoException) {
			def defaultTemplate = registry.defaultTemplate
		}
	}
	
	void testGetDefault()
	{
		def registry = new GldapoTemplateRegistry()
		def template = new GldapoTemplate()
		registry.templates["test"] = template
		registry.defaultTemplateName = "test"
		assertSame(template, registry.defaultTemplate)
	}
}