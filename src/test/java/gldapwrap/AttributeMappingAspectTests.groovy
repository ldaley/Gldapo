import gldapwrap.aspect.AttributeMappingAspect
import gldapwrap.mapping.GldapwrapAttributeMapping
import testinjectee.AttributeInjectee

class AttributeMappingAspectTests extends GroovyTestCase {
	void testInjection() 
	{
		AttributeMappingAspect.inject(AttributeInjectee)
		List attributeMappings = AttributeInjectee.getAttributeMappings()
		
		// Make sure we got what we wanted
		GldapwrapAttributeMapping objectclassAttribute = attributeMappings.find { it.name == "objectclass" }
		assertNotNull("A property should exist named 'objectclass'", objectclassAttribute)
		assertEquals("attribute type", List, objectclassAttribute.type)
		assertEquals("setter name", "setObjectclass", objectclassAttribute.setter)

		GldapwrapAttributeMapping samaccountnameAttribute = attributeMappings.find { it.name == "samaccountname" }
		assertNotNull("A property should exist named 'samaccountname'", samaccountnameAttribute)
		assertEquals("attribute type", String, samaccountnameAttribute.type)
		assertEquals("setter name", "setSamaccountname", samaccountnameAttribute.setter)

		GldapwrapAttributeMapping snAttribute = attributeMappings.find { it.name == "sn" }
		assertNotNull("A property should exist named 'sn'", snAttribute)
		assertEquals("attribute type", String, snAttribute.type)
		assertEquals("setter name", "setSn", snAttribute.setter)

		
		// Now make sure we didn't get what we didn't want
		assertNull("filter static methods", attributeMappings.find { it.name == "staticmethod" })
		assertNull("filter private methods", attributeMappings.find { it.name == "privatemethod" })
		assertNull("filter setters with too many args", attributeMappings.find { it.name == "toomanyargs" })
		
		// Final check to see if anything else untested for came through
		assertEquals("should have found 3 attributeMappings", 3, attributeMappings.size())
	}
}