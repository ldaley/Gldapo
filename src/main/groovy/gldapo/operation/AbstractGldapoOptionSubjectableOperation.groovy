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
package gldapo.operation;
import gldapo.exception.GldapoOperationException

abstract class AbstractGldapoOptionSubjectableOperation implements GldapoOptionSubjectableOperation
{
	List required
	List optionals
	Map options
		
	abstract Object execute()
	abstract void inspectOptions()
	
	/**
	 * @todo add test for null
	 */
	void setOptions(Map options)
	{
		this.options = options
		this.validateOptions()
		this.inspectOptions()
	}
	
	void validateOptions()
	{
		def requiredNotSeen = required.clone()
		
		options?.keySet()?.each {
			if (required?.contains(it)) requiredNotSeen.remove(it)
			else if (optionals?.contains(it) == false) throw new GldapoOperationException("Option '${it}' of ${this.class.simpleName} is not supported")
			
			
		}
		
		if (requiredNotSeen.size() != 0) throw new GldapoOperationException("Required options '${requiredNotSeen}' of ${this.class.simpleName} is not present in options")
	}
}