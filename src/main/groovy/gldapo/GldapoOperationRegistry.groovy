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
package gldapo
import gldapo.operation.*

/**
 * The operation registry contains instances of {@link GldapoOperation} that perform LDAP operations.
 * 
 * For example, the searching operation is abstracted to the {@link GldapoSearch} operation.
 * 
 * Operations are referenced by a name. This allows different operations to be installed over the top of each other if
 * desired.
 * 
 * The operation registry exists to allow a way to completely change the way an operation works. This is handy
 * for testing, but might be useful for some edge case.
 * 
 * @see GldapoOperation
 * @see GldapoOptionSubjectableOption
 */
class GldapoOperationRegistry {
	
	/**
	 * Operation name for the search operation. 
	 */
	public static final SEARCH = "search"
	
	/**
	 * The store of operations. Is initialised with the default operations ...
	 * 
	 * <pre>
	 * [GldapoOperationRegistry.SEARCH: GldapoSearch]
	 * </pre>
	 */
	private operations = [
		(SEARCH): GldapoSearch
	]
	
	/**
	 * Creates a new instance of the operation that is registered under {@code name} and returns it.
	 * 
	 * If the target operation is a {@link GldapoOptionSubjectableOperation}, the {@code options} are given to the new
	 * operation instance.
	 * 
	 * @param opname The name of the operation to retrieve (e.g. 'search')
	 * @param options The operation options to use if the operation is a GldapoOptionSubjectableOperation
	 * @return A new instance of the operation Class, or null if there is no operation registered for that name
	 */
	GldapoOperation getAt(String opname, Map options)
	{
		
		def op = this.operations[opname]?.newInstance()
		if (op != null && op instanceof GldapoOptionSubjectableOperation) op.options = (options == null) ? [:] : options
		return op
	}
	
	/**
	 * Calls {@link #getAt(String,Map)} with null for the options map
	 */
	GldapoOperation getAt(String opname)
	{
		this.getAt(opname, null)
	}	
	
	/**
	 * Installs a new operation for {@code opname}
	 * 
	 * @param opname The name of the operation
	 * @param opclass The class to make the operation objects from
	 * @return the added operation class ({@code opclass})
	 */
	def putAt(String opname, Object opclass)
	{
		this.operations[opname] = opclass
		return opclass
	}
	
}