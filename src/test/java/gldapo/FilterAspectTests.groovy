import testinjectee.GoodFilterInjectee
import testinjectee.NoFilterInjectee
import gldapo.aspect.FilterAspect
import gldapo.exception.GldapwrapInjectionException

class FilterAspectTests extends GroovyTestCase {
	void testGoodFilter() 
	{
		FilterAspect.inject(GoodFilterInjectee)
		assertNotNull(GoodFilterInjectee.getFilter())
		assertEquals(GoodFilterInjectee.filter, GoodFilterInjectee.getFilter())
	}

	void testNoFilter()
	{
		FilterAspect.inject(NoFilterInjectee)
		assertNull(NoFilterInjectee.getFilter())
	}
	
	void testFilterAnding()
	{
		assertEquals(GoodFilterInjectee.filter, GoodFilterInjectee.andClassFilterWithFilter(null))
		assertEquals("(&" + GoodFilterInjectee.filter + "(a=b))", GoodFilterInjectee.andClassFilterWithFilter("(a=b)"))
		
		assertEquals(FilterAspect.NO_FILTER_FILTER, NoFilterInjectee.andClassFilterWithFilter(null))
		assertEquals("(a=b)", NoFilterInjectee.andClassFilterWithFilter("(a=b)"))
	}
}