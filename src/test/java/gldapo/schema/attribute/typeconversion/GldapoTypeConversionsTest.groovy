package gldapo.schema.attribute.typeconversion;
import javax.naming.directory.BasicAttribute

class GldapoTypeConversionsTest extends GroovyTestCase 
{
	void testStringConversion() 
	{
		def a = new BasicAttribute("", 2)
		def convertd = GldapoTypeConversions.convertToStringType(a)
		assertEquals(String, convertd.class)
		assertEquals("2", convertd)
	}
	
	void testListConversion()
	{
		def a = new BasicAttribute("", 1)
		a.add(2)
		a.add(3)
		def convertd = GldapoTypeConversions.convertToListType(a)
		assert(convertd instanceof List)
		assertEquals([1, 2, 3], convertd)
	}
}