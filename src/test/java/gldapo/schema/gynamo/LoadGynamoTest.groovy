package gldapo.schema.gynamo;
import gldapo.GldapoTemplate
import gldapo.exception.GldapoException
import gldapo.schema.annotations.GldapoIdentifyingAttribute
import groovy.mock.interceptor.*
import javax.naming.directory.SearchControls
import org.springframework.ldap.core.AttributesMapperCallbackHandler
import gynamo.*

class LoadGynamoTest extends GroovyTestCase 
{
	void testGetFailsWhenNoIdentifyingAttribute() 
	{
		Gynamo.gynamize(CantLoadSchema, LoadGynamo)

		shouldFail (GldapoException) {
			CantLoadSchema.load("whatever", null)
		}
	}

/*	
	Test disabled because of http://jira.codehaus.org/browse/GROOVY-2000
	void testLoad()
	{
		Gynamo.gynamize(CanLoadSchema, LoadGynamo)
		def injectee = new CanLoadSchema()
		def findResults

		CanLoadSchema.metaClass.'static'.find = { Map options ->
			return findResults
		}
		
		findResults = [injectee]
		assertSame(injectee, CanLoadSchema.load("something", [:]))
		findResults = []
		assertEquals(null, CanLoadSchema.load("something", [:]))
	}
*/}

@GldapoIdentifyingAttribute("uid")
class CanLoadSchema 
{
	
}

class CantLoadSchema 
{
	
}