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
     * Installs {@link DefaultTypeMappings} into the registry.
     */
    GldapoTypeMappingRegistry()    {
        super()
        installDefaults()
    }
    
    void installDefaults() {
       this << DefaultTypeMappings
    }
    
    /**
     * Returns a closure that can be used to convert a LDAP value to a particular type.
     * <p>
     * Uses {@link AbstractAttributeMapping#toGroovyByTypeMapperName(String)} to calculate the name of the
     * suitable mapping method
     */
    def getToGroovyMapperForType(String type) {
        findMapper(AbstractAttributeMapping.toGroovyByTypeMapperName(type), Object)
    }
    
    /**
     * Returns a closure that can be used to convert a groovy value to an LDAP value (string)
     * <p>
     * Uses {@link AbstractAttributeMapping#toLdapByTypeMapperName(String)} to calculate the name of the
     * suitable mapping method
     */
    def getToLdapMapperForType(String type) {
        findMapper(AbstractAttributeMapping.toLdapByTypeMapperName(type), Object)
    }
    
    def findMapper(String mapperName, Class[] argTypes) {
        def mapping
        def provider = this.reverse().find {
            mapping = it.metaClass.getMetaMethod(mapperName, argTypes)
            MetaMethod
            return mapping?.isStatic()
        }
        
        (provider && mapping) ? { mapping.invoke(provider, [it] as Object[]) } : null
    }
    
    void clear() {
       super.clear()
       installDefaults()
    }
}