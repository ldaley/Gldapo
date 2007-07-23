import gldapo.aspect.*
import testinjectee.FindableInjectee
import org.springframework.ldap.core.LdapTemplate
import groovy.mock.interceptor.*
import javax.naming.directory.Attributes
import javax.naming.directory.Attribute
import org.springframework.ldap.control.PagedResultsRequestControl

class FindAspectTests extends GroovyTestCase {

	FindAspectTests()
	{
		AttributeMappingAspect.inject(FindableInjectee)
		SearchControlsAspect.inject(FindableInjectee)
		TemplateAspect.inject(FindableInjectee)
		FilterAspect.inject(FindableInjectee)
		TypeCoercionAspect.inject(FindableInjectee)
		FindAspect.inject(FindableInjectee)	
	}
	
	void testFind() 
	{
		def templateMock = new MockFor(LdapTemplate)	
		templateMock.demand.search {
			base, filter, controls, handler, requestControl ->
			
			handler = [getLists: {return []}] as Object
			requestControl = [getCookie: {return null}] as Object
		}
		
		def matches = null
		templateMock.use {
			LdapTemplate template = new LdapTemplate()
			FindableInjectee.setLdapTemplate(template)
			matches = FindableInjectee.find()
		}
		
	}
}