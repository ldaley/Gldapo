package gldapo;

class GldapoTest extends GroovyTestCase 
{
	void testRegisterSchema() 
	{
		def gldapo = Gldapo.instance
		
		gldapo.registerSchema(Schema1)

		
	}
}

class Schema1 {}
class Schema2 {}
class Schema3 {}