package gldapo.schema.gynamo;
import gynamo.*
import groovy.mock.interceptor.*
import gldapo.GldapoTemplate
import javax.naming.directory.Attributes
import javax.naming.directory.Attribute
import org.springframework.ldap.control.PagedResultsRequestControl
import javax.naming.directory.SearchControls

class FindGynamoTest extends GroovyTestCase 
{

	FindGynamoTest()
	{
		Gynamo.gynamize(FindSchema, FindGynamo)
	}
	
	void testFind() 
	{
		def templateMock = new MockFor(GldapoTemplate)
		templateMock.demand.mergeSearchControlsWithOptions {
			return new SearchControls()
		}
		templateMock.demand.search {
			base, filter, controls, handler, requestControl ->
			
			handler = [getLists: {return []}] as Object
			requestControl = [getCookie: {return null}] as Object
		}
		
		def matches = null
		templateMock.use {
			def template = new GldapoTemplate()
			matches = FindSchema.find([template: template])
		}
	}
}

class FindSchema 
{
	String attr1
	String attr2
}