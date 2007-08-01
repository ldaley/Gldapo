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
package gldapo.schema.attribute;
import javax.naming.directory.Attributes;
import org.springframework.ldap.core.AttributesMapper

class GldapoLdapToGroovyAttributeMapper implements AttributesMapper
{
	Class schema
	List attributeMappings
	
	void setSchema(Class schema)
	{
		this.schema = schema
		this.attributeMappings = schema.getAttributeMappings()
	}
	
	Object mapFromAttributes(Attributes attributes)
	{
		def object = schema.newInstance()
		attributeMappings.each { GldapoAttributeMapping attributeMapping ->
			def coercedValue = schema.convertLdapAttributeToGroovy(
				attributeMapping.type, 
				attributeMapping.name, 
				attributes.get(attributeMapping.name)
			)
			object."${attributeMapping.name}" = coercedValue
		}
		return object
	}
}