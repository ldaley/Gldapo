import gldapwrap.exception.GldapwrapException
import gldapwrap.aspect.IdentifierAspect
import gldapwrap.aspect.LoadAspect
import testinjectee.GoodIdentifierInjectee
import testinjectee.NoIdentifierInjectee

class LoadAspectTests extends GroovyTestCase {
	void testGetFailsWhenNoIdentifier() 
	{
		IdentifierAspect.inject(NoIdentifierInjectee)
		LoadAspect.inject(NoIdentifierInjectee)

		shouldFail (GldapwrapException) {
			NoIdentifierInjectee.load("whatever")
		}
	}
	
	void testGet()
	{
		IdentifierAspect.inject(GoodIdentifierInjectee)
		LoadAspect.inject(GoodIdentifierInjectee)
		def injectee = new GoodIdentifierInjectee()
		def results = [injectee]
		GoodIdentifierInjectee.metaClass."static".find << { Map options ->
			assertEquals("(uid=something)", options?.filter)
			return results
		}
		
		assertSame(injectee, GoodIdentifierInjectee.load("something"))
		results = []
		assertEquals(null, GoodIdentifierInjectee.load("something"))
	}
}