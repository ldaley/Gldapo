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
		template.beanName = "test"
		registry.defaultTemplateName = "test"
		registry << template
		assertSame(template, registry.defaultTemplate)
	}
	
	void testNewFromOkConfig()
	{
		def c = new ConfigObject()
		
		c.templates.t1.contextSource.url = "ldap://example.com"
		c.templates.t2.contextSource.url = "ldap://example2.com"
		c.defaultTemplate = "t1"
		
		def r = GldapoTemplateRegistry.newFromConfig(c)
		
		assert(r instanceof GldapoTemplateRegistry)
		assertEquals(2, r.templates.size())
		assertNotNull(r["t1"])
		assertNotNull(r["t2"])
		assert(r["t1"] instanceof GldapoTemplate)
		assertSame(r.defaultTemplate, r["t1"])
	}
	
}