import testinjectee.TypeCoercionInjectee
import gldapo.aspect.TypeCoercionAspect
import gldapo.exception.GldapwrapNoAvailableTypeCoercionAvailableException
import gldapo.exception.GldapwrapTypeCoercionException
import javax.naming.directory.Attribute
import java.math.BigInteger
import javax.naming.directory.BasicAttribute

class TypeCoercionAspectTests extends GroovyTestCase {
	
	TypeCoercionAspectTests()
	{
		TypeCoercionAspect.inject(TypeCoercionInjectee)
	}
	
	void testFactoryCoercion() {
		def coerced = TypeCoercionInjectee.coerceLdapToNative(Integer, "anInt", new BasicAttribute("a", "4"))
		assertEquals(Integer, coerced.class)
	}
	
	void testNoAvailableGenericCoercion() {
		try
		{
			TypeCoercionInjectee.coerceLdapToNative(Exception, "blah", new BasicAttribute("a", "4")) // Should never be coercing to Exception
			fail("A GldapwrapTypeCoercionException should have been raised")
		}
		catch (GldapwrapTypeCoercionException e)
		{
			assertEquals(GldapwrapNoAvailableTypeCoercionAvailableException, e.cause.class)
		}
	}
	
	void testLocalOverridesGenericGlobal()
	{
		def bi50 = new BigInteger("50");
		def bi100 = new BigInteger("100");
		
		assertEquals(bi50, TypeCoercionInjectee.coerceLdapToNative(BigInteger, "garbage", new BasicAttribute("a", "50"))) // Use global
		
		TypeCoercionInjectee.metaClass."static".coerceToBigIntegerType << { Attribute value ->
			return new BigInteger("100");
		}
		
		assertEquals(bi100, TypeCoercionInjectee.coerceLdapToNative(BigInteger, "garbage", new BasicAttribute("a", "50"))) // Use override
	}
	
	void testAttributeCoercion()
	{
		try
		{
			TypeCoercionInjectee.coerceLdapToNative(Exception, "garbage", "50")
			fail("A GldapwrapTypeCoercionException should have been raised")
		}
		catch (GldapwrapNoAvailableTypeCoercionAvailableException)
		{
			// Nothing here, this is expected
		}
		
		TypeCoercionInjectee.metaClass."static".coerceToGarbageAttribute << { Attribute value ->
			return 100
		}
		
		assertEquals(100, TypeCoercionInjectee.coerceLdapToNative(Exception, "garbage", new BasicAttribute("a", "50")))
	}
	
	void testAttributeOverridesGeneric()
	{
		assertEquals(50, TypeCoercionInjectee.coerceLdapToNative(Integer, "override", new BasicAttribute("a", "50")))
		
		TypeCoercionInjectee.metaClass."static".coerceToOverrideAttribute << { Attribute value ->
			return 100
		}
		
		assertEquals(100, TypeCoercionInjectee.coerceLdapToNative(Exception, "override", new BasicAttribute("a", "100")))
	}
}