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
package gldapo.schema.injecto;
import gldapo.Gldapo
import gldapo.GldapoOperationRegistry
import injecto.annotation.InjectAs

class SearchingInjecto 
{	
	static findAll = { Map options ->
		def searchOptions = options.clone()
		searchOptions.schema = delegate
		
		Gldapo.instance.operations[GldapoOperationRegistry.SEARCH, searchOptions].execute()
	}
	
	@InjectAs("findAll")
	static findAllNoArgs = { -> 
		delegate.findAll([:])
	}
	
	static find = { Map options ->
		options.countLimit = 1
		def r = delegate.findAll(options)
		(r.size() > 0) ? r[0] : null
	}
	
	@InjectAs("find")
	static findNoArgs = { -> 
		delegate.find([:])
	}
}