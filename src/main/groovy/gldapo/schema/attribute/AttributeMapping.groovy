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
package gldapo.schema.attribute
import gldapo.schema.attribute.validator.AttributeValidator
import java.lang.reflect.Field

/**
 * Represents the bridging between the data of the LDAP world and the Groovy world
 */
interface AttributeMapping
{
    /**
     * The class that this attribute belongs to
     */
    Class getSchema()
    
    /**
     * The underlying field representing the attribute
     */
    Field getField()
    
    /**
     * The name on the LDAP side
     */
    String getAttributeName()

    /**
     * The type name to use for type conversions
     */
    String getTypeMapping()
    
    /**
     * A closure that can convert an LDAP value into a Java/Groovy value
     */
    Closure getToGroovyTypeMapper()
    
    /**
     * A closure that can convert a Java/Groovy value into an LDAP value
     */
     Closure getToLdapTypeMapper()
     
     /**
      * The validators for this attribute
      */
     Collection<AttributeValidator> getValidators()
}