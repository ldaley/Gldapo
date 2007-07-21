import testinjectee.GoodIdentifierInjectee
import testinjectee.NoIdentifierInjectee
import gldapwrap.aspect.IdentifierAspect
import gldapwrap.exception.GldapwrapInjectionException

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