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
import gldapo.exception.GldapoInvalidSearchScopeException

class SearchScopeTranslator 
{
	public static final OBJECT = "object"
	public static final ONELEVEL = "onelevel"
	public static final SUBTREE = "subtree"
	
	static MAPPING = [
		OBJECT: SearchControls.OBJECT_SCOPE,
		ONELEVEL: SearchControls.ONELEVEL_SCOPE,
		SUBTREE: SearchControls.SUBTREE_SCOPE
	]
	
	static translate(String searchScope)
	{
		if (MAPPING.containsKey(searchScope) == false) throw new GldapoInvalidSearchScopeException(searchScope)
		MAPPING[searchScope]
	}
	
	static translate(Integer searchScope)
	{
		if (MAPPING.containsValue(searchScope) == false) throw new GldapoInvalidSearchScopeException(searchScope)
		return searchScope
	}
}