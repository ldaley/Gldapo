import gldapo.GldapwrapTypeCoercions
import javax.naming.directory.BasicAttribute

class TypeCoercionsTests extends GroovyTestCase {
	void testStringCoercion() 
	{
		def a = new BasicAttribute("", 2)
		def coerced = GldapwrapTypeCoercions.coerceToStringType(a)
		assertEquals(String, coerced.class)
		assertEquals("2", coerced)
	}
	
	void testListCoercion()
	{
		def a = new BasicAttribute("", 1)
		a.add(2)
		a.add(3)
		def coerced = GldapwrapTypeCoercions.coerceToListType(a)
		assert(coerced instanceof List)
		assertEquals([1, 2, 3], coerced)
	}
}