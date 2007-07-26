package gldapo.util;
import javax.naming.directory.SearchControls

class SearchControlsMergerTest extends GroovyTestCase 
{
	void testBothNulls() 
	{
		assertEquals(true, SearchControlsMerger.merge(null, null) instanceof SearchControls)
	}
	
	void testFirstNull()
	{
		def m = SearchControlsMerger.merge(null, [countLimit: 50])
		assertEquals(true, m instanceof SearchControls)
		assertEquals(50, m.countLimit)
	}
	
	void testSecondNull()
	{
		def m = SearchControlsMerger.merge([countLimit: 50], null)
		assertEquals(true, m instanceof SearchControls)
		assertEquals(50, m.countLimit)
	}
	
	void testMerge()
	{		
		def m = SearchControlsMerger.merge([countLimit: 50, timeLimit: 20], [countLimit: 100, searchScope: 2])
		assertEquals(true, m instanceof SearchControls)
		assertEquals(100, m.countLimit)
		assertEquals(20, m.timeLimit)
		assertEquals(2, m.searchScope)
	}
	
}