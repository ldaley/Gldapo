package gldapo.schema.gynamo;
import gldapo.GldapoSchemaRegistry
import gldapo.schema.annotations.*

/**
 * @todo These tests need to be more comprehensive
 */
class FindGynamoTest extends GroovyTestCase 
{

	FindGynamoTest()
	{
		GldapoSchemaRegistry.newInstance() << FindSchema
	}
	
	void testFind() 
	{
		def matches = [1,2,3]
		def template = new Expando()
		template.search = {
			base, filter, controls, handler, requestControl ->
			
			assertEquals("ou=people", base)
			assertEquals("(&(objectclass=find)(a=b))", filter)
			handler = [list: matches]
			requestControl = [cookie: null]
		}
		
		def results = FindSchema.find(
			template: template,
			filter: "(a=b)",
			base: "ou=people"
		)
	}
}

@GldapoSchemaFilter("(objectclass=find)")
class FindSchema 
{
	String uid
	String sn
}