package gldapo;
import gldapo.exception.GldapoInitializationException

class GldapoTest extends GroovyTestCase 
{
	void testInitialiseDefaultConf() 
	{
		Gldapo.initialize("dev")
		assertEquals(2, Gldapo.templateRegistry.templates.size())
		assertEquals("development", Gldapo.templateRegistry["t2"].base) // Tests env collapse
	}
	
	void testNullUrlExplodes()
	{
		shouldFail {
			Gldapo.initialize(new File("2853kgmpv0").toURL())
		}
	}
	
}