package gldapo.schema.gynamo;
import gldapo.exception.GldapoException
import gldapo.schema.annotations.GldapoSchemaFilter
import gynamo.*

class SchemaFilterTest extends GroovyTestCase 
{
	SchemaFilterTest()
	{
		Gynamo.gynamize(HasFilterSchema, SchemaFilterGynamo)
		Gynamo.gynamize(NoFilterSchema, SchemaFilterGynamo)
	}
	void testHasFilter() 
	{		
		assertEquals("(objectclass=person)", HasFilterSchema.getSchemaFilter())
	}

	void testNoFilter()
	{
		assertEquals(SchemaFilterGynamo.NO_FILTER_FILTER, NoFilterSchema.getSchemaFilter())
	}
	
	void testFilterAnding()
	{
		assertEquals("(objectclass=person)", HasFilterSchema.andSchemaFilterWithFilter(null))
		assertEquals("(&(objectclass=person)(a=b))", HasFilterSchema.andSchemaFilterWithFilter("(a=b)"))
		
		assertEquals(SchemaFilterGynamo.NO_FILTER_FILTER, NoFilterSchema.andSchemaFilterWithFilter(null))
		assertEquals("(a=b)", NoFilterSchema.andSchemaFilterWithFilter("(a=b)"))
	}
}

@GldapoSchemaFilter("(objectclass=person)")
class HasFilterSchema
{
	
}

class NoFilterSchema
{
	
}