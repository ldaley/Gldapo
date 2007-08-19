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
package gldapo.template;
import gldapo.exception.GldapoException

import javax.naming.directory.SearchControls


class GldapoSearchControlsImpl implements GldapoSearchControls
{
	public static final OBJECT_SEARCHSCOPE = "object"
	public static final ONELEVEL_SEARCHSCOPE = "onelevel"
	public static final SUBTREE_SEARCHSCOPE = "subtree"
	
	static SEARCHSCOPE_MAPPING = [
		(OBJECT_SEARCHSCOPE): SearchControls.OBJECT_SCOPE,
		(ONELEVEL_SEARCHSCOPE): SearchControls.ONELEVEL_SCOPE,
		(SUBTREE_SEARCHSCOPE): SearchControls.SUBTREE_SCOPE
	]
	
	Integer countLimit
	Boolean derefLinkFlag
	String searchScope
	Integer timeLimit
	
	/**
	 * @todo Use an inspector or something to do this dynamically
	 */
	def mergeWith(GldapoSearchControls controls)
	{
		if (controls.countLimit != null) this.countLimit = controls.countLimit
		if (controls.derefLinkFlag != null) this.derefLinkFlag = controls.derefLinkFlag
		if (controls.searchScope != null) this.searchScope = controls.searchScope
		if (controls.timeLimit != null) this.timeLimit = controls.timeLimit
	}
	
	def setSearchScope(String sc)
	{
		if (!SEARCHSCOPE_MAPPING.containsKey(sc)) throw new GldapoException("'$sc' is not a valid search scope")
		this.searchScope = sc
	}

	def getSearchScopeAsInteger()
	{
		SEARCHSCOPE_MAPPING[this.searchScope]
	}
	
	def asType(Class c)
	{
		if (c.equals(SearchControls))
		{
			def controls = new SearchControls()
			if (this.countLimit != null) controls.countLimit = controls.countLimit
			if (this.derefLinkFlag != null) controls.derefLinkFlag = controls.derefLinkFlag
			if (this.searchScope != null) controls.searchScope = controls.searchScopeAsInteger
			if (this.timeLimit != null) controls.timeLimit = controls.timeLimit
			return controls
		}
		else
		{
			super(c)
		}
	}
	
	static newInstance(ConfigObject config)
	{
		
	}
}