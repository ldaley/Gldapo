import testinjectee.TemplateInjectee
import gldapo.aspect.TemplateAspect
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource
import gldapo.exception.GldapwrapNoTemplateException
import gldapo.GldapwrapTemplate
import groovy.mock.interceptor.MockFor

class TemplateAspectTests extends GroovyTestCase {
	TemplateAspectTests()
	{
		TemplateAspect.inject(TemplateInjectee)
	}
	
	void testGetter() {
		assertNull(TemplateInjectee.getLdapTemplate())
		shouldFail(GldapwrapNoTemplateException) {
			TemplateInjectee.getLdapTemplateAssert()
		}
	}
	
	void testSetter()
	{
		def template = new LdapTemplate()
		TemplateInjectee.setLdapTemplate(template)
		assertSame(template, TemplateInjectee.getLdapTemplate())
		TemplateInjectee.getLdapTemplateAssert() // shouldn't raise exception
	}
	
	void testGldapwrapTemplateSetGet()
	{
		def ldapTemplateMock = new MockFor(LdapTemplate)
		ldapTemplateMock.demand.afterPropertiesSet() {}
		
		def contextSourceMock = new MockFor(LdapContextSource)
		contextSourceMock.demand.afterPropertiesSet() {}
		
		def returnedTemplate
		
		contextSourceMock.use {
			ldapTemplateMock.use {
				def gldapoTemplate = new GldapwrapTemplate(
					url: "ldapwrap://somewhere.com",
					base: "dc=somewhere,dc=com",
					userDn: "uid=me",
					password: "xxx"
				)
			
				TemplateInjectee.setLdapTemplate(gldapoTemplate)
				returnedTemplate = TemplateInjectee.getLdapTemplate()
			}
		}

		assertEquals(LdapTemplate, returnedTemplate.class)
	}
}