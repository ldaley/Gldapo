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
package gldapo.directory
import gldapo.exception.GldapoException
import javax.naming.directory.SearchControls
 
class GldapoSearchControls implements GldapoSearchControlProvider, Cloneable
{
	public static final OBJECT_SEARCHSCOPE = "object"
	public static final ONELEVEL_SEARCHSCOPE = "onelevel"
	public static final SUBTREE_SEARCHSCOPE = "subtree"
	
	static Map SEARCHSCOPE_MAPPING = [
		(OBJECT_SEARCHSCOPE): SearchControls.OBJECT_SCOPE,
		(ONELEVEL_SEARCHSCOPE): SearchControls.ONELEVEL_SCOPE,
		(SUBTREE_SEARCHSCOPE): SearchControls.SUBTREE_SCOPE
	]
	
	Integer countLimit
	
	Integer getCountLimit() {
		this.countLimit
	}
	
	Boolean derefLinkFlag
	
	Boolean getDerefLinkFlag() {
		this.derefLinkFlag
	}
	
	String searchScope
	
	String getSearchScope() {
		this.searchScope
	}
	
	Integer timeLimit
	
	Integer getTimeLimit() {
		this.timeLimit
	}
	
	def clone()
	{
		def n = new GldapoSearchControls()
		if (this.countLimit != null) n.countLimit = this.countLimit
		if (this.derefLinkFlag != null) n.derefLinkFlag = this.derefLinkFlag
		if (this.searchScope != null) n.searchScope = this.searchScope
		if (this.timeLimit != null) n.timeLimit = this.timeLimit
		return n
	}
	
	/**
	 * @todo Use an inspector or something to do this dynamically
	 */
	GldapoSearchControls mergeWith(controls)
	{
		def m = this.clone()
		
		if (controls.countLimit != null) m.countLimit = controls.countLimit
		if (controls.derefLinkFlag != null) m.derefLinkFlag = controls.derefLinkFlag
		if (controls.searchScope != null) m.searchScope = controls.searchScope
		if (controls.timeLimit != null) m.timeLimit = controls.timeLimit
		
		return m
	}
	
	void setSearchScope(String sc)
	{
		if (!SEARCHSCOPE_MAPPING.containsKey(sc)) throw new GldapoException("'$sc' is not a valid search scope")
		this.searchScope = sc
	}

	Integer getSearchScopeAsInteger()
	{
		SEARCHSCOPE_MAPPING[this.searchScope]
	}
	
	def asType(Class c)
	{
		if (c.equals(SearchControls))
		{
			def controls = new SearchControls()
			if (this.countLimit != null) controls.countLimit = this.countLimit
			if (this.derefLinkFlag != null) controls.derefLinkFlag = this.derefLinkFlag
			if (this.searchScope != null) controls.searchScope = this.searchScopeAsInteger
			if (this.timeLimit != null) controls.timeLimit = this.timeLimit
			controls.returningObjFlag = true
			return controls
		}
		else
		{
			super(c)
		}
	}
	
	static newInstance(Map config)
	{
		def cs = new GldapoSearchControls()
		if (config) {
    		if (config.containsKey("countLimit")) cs.countLimit = config.countLimit
    		if (config.containsKey("derefLinkFlag")) cs.derefLinkFlag = config.derefLinkFlag
    		if (config.containsKey("searchScope")) cs.searchScope = config.searchScope
    		if (config.containsKey("timeLimit")) cs.timeLimit = config.timeLimit
		}
		return cs
	}
}