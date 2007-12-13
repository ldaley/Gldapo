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
package gldapo.operation
import gldapo.Gldapo
import gldapo.directory.GldapoSearchProvider
import gldapo.directory.GldapoSearchControls
import gldapo.schema.annotation.GldapoSchemaFilter

class GldapoSearch extends AbstractGldapoOptionSubjectableOperation
{	
	GldapoSearch()
	{
		super()
		required = ["schema"]
		optionals = ["directory", "filter", "pageSize", "base", "absoluteBase", "countLimit", "derefLinkFlag", "searchScope", "timeLimit"]
	}
	
	void inspectOptions() 
	{
		this.options.directory = this.calculateDirectory()
		this.options.filter = this.calculateFilter()
		this.options.pageSize = this.calculatePageSize()
		this.options.searchControls = this.calculateSearchControls()
		this.options.base = this.calculateBase()
	}
	
	def calculateDirectory()
	{
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
		def schemaFilter = this.options.schema.getAnnotation(GldapoSchemaFilter)?.value()
		
		if (this.options.filter) return (schemaFilter) ? "(&${schemaFilter}${this.options.filter})" : this.options.filter
		else return (schemaFilter) ? schemaFilter : "(objectclass=*)"
	}
	
	def calculatePageSize()
	{
		if (options.pageSize != null) return options.pageSize
		else return Gldapo.instance.settings.pageSize
	}
	
	def calculateSearchControls()
	{
		def specificControls = GldapoSearchControls.newInstance(this.options)
		(this.options.directory.searchControls) ? this.options.directory.searchControls.mergeWith(specificControls) : specificControls
	}
	
	def calculateBase()
	{
		if (options.containsKey("absoluteBase")) return options.absoluteBase - ",${this.options.directory.base}"
		else if (options.containsKey("base")) return options.base
		else return ""
	}
	
	def execute()
	{
		this.options.directory.search(this.options.schema, this.options.base, this.options.filter, this.options.searchControls, this.options.pageSize)
	}
}
