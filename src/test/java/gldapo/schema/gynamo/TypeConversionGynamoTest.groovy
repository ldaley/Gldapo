package gldapo.schema.gynamo;
import gldapo.exception.GldapoException
import gldapo.schema.annotations.GldapoSchemaFilter
import gynamo.*
import gldapo.exception.GldapoNoTypeConversionAvailableException
import gldapo.exception.GldapoTypeConversionException
import javax.naming.directory.Attribute
import java.math.BigInteger
import javax.naming.directory.BasicAttribute

class TypeConversionGynamoTest extends GroovyTestCase 
{
	
	TypeConversionGynamoTest()
	{
		Gynamo.gynamize(TypeConversionSchema, TypeConversionGynamo)
	}
	
	void testFactoryConversion() 
	{
		def converted = TypeConversionSchema.coerceLdapAttributeToGroovy(Integer, "anInt", new BasicAttribute("a", "4"))
		assertEquals(Integer, converted.class)
	}
	
	void testNoAvailableGenericConversion() 
	{
		try
		{
			TypeConversionSchema.coerceLdapAttributeToGroovy(Exception, "blah", new BasicAttribute("a", "4"))
			fail("A GldapoNoTypeConversionAvailableException should have been raised")
		}
		catch (GldapoTypeConversionException e)
		{
			assertEquals(GldapoNoTypeConversionAvailableException, e.cause.class)
		}
	}
	
	void testLocalOverridesGenericGlobal()
	{
		def bi50 = new BigInteger("50");
		def bi100 = new BigInteger("100");
		
		assertEquals(bi50, TypeConversionSchema.coerceLdapAttributeToGroovy(BigInteger, "garbage", new BasicAttribute("a", "50"))) // Use global
		
		TypeConversionSchema.metaClass."static".coerceToBigIntegerType << { Attribute value ->
			return new BigInteger("100");
		}
		
		assertEquals(bi100, TypeConversionSchema.coerceLdapAttributeToGroovy(BigInteger, "garbage", new BasicAttribute("a", "50"))) // Use override
	}
	
	void testAttributeConversion()
	{
		try
		{
			TypeConversionSchema.coerceLdapAttributeToGroovy(Exception, "garbage", "50")
			fail("A GldapoNoTypeConversionAvailableException should have been raised")
		}
		catch (GldapoNoTypeConversionAvailableException)
		{
			// Nothing here, this is expected
		}
		
		TypeConversionSchema.metaClass."static".coerceToGarbageAttribute << { Attribute value ->
			return 100
		}
		
		assertEquals(100, TypeConversionSchema.coerceLdapAttributeToGroovy(Exception, "garbage", new BasicAttribute("a", "50")))
	}
	
	void testAttributeOverridesGeneric()
	{
		assertEquals(50, TypeConversionSchema.coerceLdapAttributeToGroovy(Integer, "override", new BasicAttribute("a", "50")))
		
		TypeConversionSchema.metaClass."static".coerceToOverrideAttribute << { Attribute value ->
			return 100
		}
		
		assertEquals(100, TypeConversionSchema.coerceLdapAttributeToGroovy(Exception, "override", new BasicAttribute("a", "100")))
	}
}

class TypeConversionSchema
{

}