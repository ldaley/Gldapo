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
import gldapo.Gldapo
import gldapo.directory.GldapoSearchProvider
import gldapo.directory.GldapoSearchControls
import gldapo.util.FilterUtil

class GldapoSearch extends AbstractGldapoOptionSubjectableOperation
{
	private schema
	
	private directory
	private filter
	private pageSize
	private searchControls
	private base
	private returningAttributes
	
	def getDirectory() { directory }
	def getFilter() { filter }
	def getPageSize() { pageSize }
	def getSearchControls() { searchControls }
	def getBase() { base }
	def getReturningAttributes() { returningAttributes }
	
	void inspectOptions() 
	{
		this.directory = this.calculateDirectory()
		this.filter = this.calculateFilter()
		this.pageSize = this.calculatePageSize()
		this.searchControls = this.calculateSearchControls()
		this.base = this.calculateBase()
	}
	
	def calculateDirectory()
	{
		println options
		if (options.directory != null)
		{
			def directoryValue = options.directory
			if (directoryValue instanceof String) return Gldapo.instance.directories[directoryValue]
			
			if (directoryValue instanceof GldapoSearchProvider) return directoryValue

			// TODO more suitable exception needed
			throw new IllegalArgumentException()
		}
		else
		{
			return Gldapo.instance.directories.defaultDirectory
		}		
	}
	
	def calculateFilter()
	{
		FilterUtil.andSchemaFilterWithFilter(this.schema, options?.filter)
	}
	
	def calculatePageSize()
	{
		println options
		if (options.pageSize != null) return options.pageSize
		else return Gldapo.instance.settings.pageSize
	}
	
	def calculateSearchControls()
	{
		this.directory.searchControls.mergeWith(new GldapoSearchControls(this.options))
	}
	
	def calculateBase()
	{
		if (options.containsKey("absoluteBase")) return options.absoluteBase - this.directory.baseDN
		if (options.containsKey("base")) return options.base
		return ""
	}
	
	def execute()
	{
		this.directory.search(this.schema, this.base, this.filter, this.controls, this.pageSize)
	}
}
