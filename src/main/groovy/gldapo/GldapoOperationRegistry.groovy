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
package gldapo;
import gldapo.operation.*

class GldapoOperationRegistry extends LinkedHashMap<String,GldapoOperation>
{
	public static final SEARCH = "search"
	
	private static DEFAULTS = [
		(SEARCH): GldapoSearch
	]
	
	def getOperationInstance(String opname, Map options)
	{
		def op = this[opname].newInstance()
		if (op instanceof GldapoOptionSubjectableOperation) op.options = options
		return op
	}
	
	def getOperationInstance(String opname)
	{
		this.getOperationInstance(opname, null)
	}	
	
	static newDefaultOperationRegistry()
	{
		return new GldapoOperationRegistry(DEFAULTS)
	}
	
	
}