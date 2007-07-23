import gldapo.aspect.SearchControlsAspect
import testinjectee.SearchControlsInjectee
import javax.naming.directory.SearchControls
import gldapo.exception.GldapwrapInjectionException

class SearchControlsAspectTests extends GroovyTestCase {
	SearchControlsAspectTests()
	{
		SearchControlsAspect.inject(SearchControlsInjectee)
	}
	
	void testGetter() 
	{
		def controls = SearchControlsInjectee.getSearchControls()
		assertNotNull(controls)
		assertEquals(SearchControls, controls.class)
	}
	
	void testSetter()
	{
		def controls = new SearchControls()
		SearchControlsInjectee.setSearchControls(controls)
		assertSame(controls, SearchControlsInjectee.getSearchControls())
	}
}