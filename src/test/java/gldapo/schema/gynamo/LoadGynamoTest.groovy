package gldapo.schema.gynamo;
import gldapo.GldapoTemplate
import gldapo.exception.GldapoException
import gldapo.schema.annotations.GldapoIdentifyingAttribute
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
	
	void testLoad()
	{
		Gynamo.gynamize(CanLoadSchema, LoadGynamo)
		def injectee = new CanLoadSchema()
		def results = [injectee]
		CanLoadSchema.metaClass."static".find << { Map options ->
			assertEquals("(uid=something)", options?.filter)
			return results
		}

		def template = new GldapoTemplate()
		assertSame(injectee, CanLoadSchema.load("something", template))
		results = []
		assertEquals(null, CanLoadSchema.load("something", template))
	}
}

@GldapoIdentifyingAttribute("uid")
class CanLoadSchema 
{
	
}

class CantLoadSchema 
{
	
}