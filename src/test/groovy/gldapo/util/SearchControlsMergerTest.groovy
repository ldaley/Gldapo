/* 
 * Copyright 2007 Luke Daley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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