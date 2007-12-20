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
import gldapo.schema.attribute.type.DefaultTypeMappings
import gldapo.schema.attribute.AbstractAttributeMapping

/**
 * Holds the global type mappings to convert values to and from LDAP and Java
 * 
 * @see Gldapo#getTypemappings()
 */
class GldapoTypeMappingRegistry extends LinkedList<Class> {
    
	/**
	 * The key under which the array of type mapping classes is excepted to be in the {@link newInstance(Map) config}.
	 * Is '{@value}'
	 */
	static String CONFIG_KEY = "typemappings"
	
	/**
	 * Installs {@link DefaultTypeMappings} into the registry.
	 */
	GldapoTypeMappingRegistry()	{
		super()
		this << DefaultTypeMappings
	}
	
	/**
	 * Returns a closure that can be used to convert a LDAP value to a particular type.
	 * <p>
	 * Uses {@link AbstractAttributeMapping#toFieldByTypeMapperName(Object)} to calculate the name of the
	 * suitable mapping method
	 */
	def getToFieldMapperForType(String type) {
		findMapper(AbstractAttributeMapping.toFieldByTypeMapperName(type), Object)
	}
	
	def findMapper(String mapperName, Class[] argTypes)
	{
		def mapping
		def provider = this.reverse().find {
			mapping = it.metaClass.getMetaMethod(mapperName, argTypes)
			return mapping?.isStatic()
		}
		
		(provider && mapping) ? { mapping.invoke(provider, it) } : null
	}
	
	/**
	 * 
	 */
	static newInstance(Map config) {
		def tmr = new GldapoTypeMappingRegistry()
		if (config != null && config[CONFIG_KEY] != null && config[CONFIG_KEY] instanceof Collection) {
			config[CONFIG_KEY].each {
				tmr << it
			}
		}
		return tmr
	}
}