import gldapo.GldapwrapTemplate

class GldapwrapTemplateTests extends GroovyTestCase {
	void testNullConstructor() 
	{
		GldapwrapTemplate template = new GldapwrapTemplate()
		assertNull(template.ldapTemplate)
		assertNull(template.contextSource)
	}
	
	void testConstructor() 
	{
		GldapwrapTemplate template = new GldapwrapTemplate(
			userDn: "cn=blah",
			password: "aaa",
			url: "ldapwrap://somewhere.com",
			base: "dc=somewhere,dc=com"
		)
		assertNotNull(template.ldapTemplate)
		assertNotNull(template.contextSource)
		
		assertEquals(false, template.initialised)
		template.afterPropertiesSet()
		assertEquals(true, template.initialised)
		template.afterPropertiesSet() // Making sure this doesn't throw exceptions if called more than once
	}
}