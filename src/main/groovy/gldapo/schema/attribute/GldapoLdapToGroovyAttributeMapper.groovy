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
import org.springframework.ldap.core.ContextMapper

class GldapoContextMapper implements ContextMapper
{
	Class schemaClass
	List attributeMappings
	String base
	
	void setSchema(Class schema)
	{
		this.schemaClass = schemaClass
		this.attributeMappings = schemaClass.getAttributeMappings()
	}
	
	Object mapFromAttributes(Attributes attributes)
	{
		def entry = schemaClass.newInstance()
		attributeMappings.each { AttributeMapping attributeMapping ->
			def attribute = context.getStringAttributes(attributeMapping.attributeName)
			object."${attributeMapping.propertyName}" = attributeMapping.convertAttributeToProperty()
		}
		return object
	}
	
	public Object mapFromContext(Object ctx) {
        DirContextAdapter context = (DirContextAdapter)ctx;
        Person p = new Person();
        p.setFullName();
        p.setLastName(context.getStringAttribute("sn"));
        p.setDescription(context.getStringAttribute("description"));
        return p;
     }
}