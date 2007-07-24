package gldapo.schema.gynamo;
import gldapo.schema.annotations.GldapoIdentifyingAttribute;
import gynamo.*

class IdentifyingAttributeGynamoTest extends GroovyTestCase 
{
	void testHasIdentifier() 
	{
		Gynamo.gynamize(HasIdentifyingAttributeSchema, IdentifyingAttributeGynamo)
		assertEquals("uid", HasIdentifyingAttributeSchema.getIdentifyingAttribute())
	}

	void testNoIdentifier()
	{
		Gynamo.gynamize(NoIdentifyingAttributeSchema, IdentifyingAttributeGynamo)
		assertNull(NoIdentifyingAttributeSchema.getIdentifyingAttribute())
	}
}

@GldapoIdentifyingAttribute("uid")
class HasIdentifyingAttributeSchema
{
	
}

class NoIdentifyingAttributeSchema
{
	
}