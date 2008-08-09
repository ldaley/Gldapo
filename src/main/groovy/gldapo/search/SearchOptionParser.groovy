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
package gldapo.search
import gldapo.Gldapo
import gldapo.filter.FilterUtil
import gldapo.schema.annotation.GldapoSchemaFilter
import org.springframework.ldap.core.DistinguishedName
import org.springframework.ldap.filter.*

/**
 * Given a map of search options, calculates what the actual search parameters should be.
 */
class SearchOptionParser
{    
    def keys = [
        "schema", "directory", "filter", "base", "absoluteBase", 
        "countLimit", "derefLinkFlag", "searchScope", "timeLimit", "pageSize"
    ]
    
    def schema
    def gldapo
    def options
    def directory
    def filter
    def controls
    def base
    
    SearchOptionParser(schema, options) {
        this.schema = schema
        this.gldapo = schema.gldapo
        this.options = options
        
        validateOptions()
        
        this.directory = calculateDirectory()
        this.filter = calculateFilter()
        this.controls = calculateSearchControls()
        this.base = calculateBase()
    }
    
    void validateOptions() {
        options.keySet.each { key ->
            if (keys.find { it == key} == false)
                throw new SearchOptionException("$key is not a valid search option")
        }
    }
    
    def calculateDirectory()
    {
        if (options.directory != null)
        {
            def directoryOption = options.directory
            
            if (directoryOption instanceof String) 
            {
                return this.gldapo.directories[directoryOption]
            } 
            else if (directoryOption instanceof SearchProvider) 
            {
                return directoryOption
            } 
            else 
            {
                throw new SearchOptionException("'directory' option must be a string, or an implementer of SearchProvider")
            }
        }
        else
        {
            return this.gldapo.directories.defaultDirectory
        }        
    }
    
    def calculateFilter()
    {
        def schemaFilter = this.schema.getAnnotation(GldapoSchemaFilter)?.value()
        def optionFilter = this.options.filter
            
        if (optionFilter) 
        {
            if (optionFilter instanceof Closure)
                optionFilter = FilterUtil.build(optionFilter)
            
            optionFilter = optionFilter as String
            
            return (schemaFilter) ? "(&${schemaFilter}${optionFilter})" : optionFilter
        } 
        else 
        {
            return (schemaFilter) ? schemaFilter : "(objectclass=*)"
        }
    }
    
    def calculateSearchControls()
    {
        def specificControls = new SearchControls(this.options)
        def directoryControls = this.directory.searchControls
        
        if (directoryControls != null)
        {
            return directoryControls.mergeWith(specificControls)
        }
        else 
        {
            return specificControls
        }
    }
    
    def calculateBase()
    {
        def absoluteBase = options.absoluteBase
        def relativeBase = options.base
        def base
        
        def coerceToDn = { optionName, value ->
            if (value instanceof String) 
            {
                return new DistinguishedName(value)
            } 
            else if (value instanceof DistinguishedName) 
            {
                return value.clone()
            } 
            else 
            {
                throw new SearchOptionException("'$optionName' must be a String or DistinguishedName")
            }            
        }
        
        if (absoluteBase != null) 
        {
            base = coerceToDn('absoluteBase', absoluteBase)
            base.removeFirst(directory.base)
        } 
        else if (relativeBase != null) 
        {
            base = coerceToDn('base', relativeBase)
        } 
        else 
        {
            base = new DistinguishedName("")
        }
        
        return base
    }
}
