import testinjectee.GoodIdentifierInjectee
import testinjectee.NoIdentifierInjectee
import gldapo.aspect.IdentifierAspect
import gldapo.exception.GldapwrapInjectionException

class IdentifierAspectTests extends GroovyTestCase {
	void testGoodIdentifier() 
	{
		IdentifierAspect.inject(GoodIdentifierInjectee)
		assertNotNull(GoodIdentifierInjectee.getIdentifier())
		assertEquals(String, GoodIdentifierInjectee.getIdentifier().class)
		assertEquals(GoodIdentifierInjectee.identifier, GoodIdentifierInjectee.getIdentifier())
	}

	void testNoIdentifier()
	{
		IdentifierAspect.inject(NoIdentifierInjectee)
		assertNull(NoIdentifierInjectee.getIdentifier())
	}
}