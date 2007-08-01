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

class SearchControlsMerger 
{
	static public final searchControlOptions = [
		"countLimit", "derefLinkFlag", "searchScope", "timeLimit"
	]
	
	static SearchControls merge(c1, c2)
	{
		def controls = new SearchControls()
		if (c1 == null && c2 == null) return controls 
		if (c1 == null) c1 = [:]
		if (c2 == null) c2 = [:]
		
		searchControlOptions.each {
			if (c2.containsKey(it))
			{
				controls."${it}" = c2[it]
			}
			else if (c1.containsKey(it))
			{
				controls."${it}" = c1[it]
			}
		}
		
		return controls
	}
}