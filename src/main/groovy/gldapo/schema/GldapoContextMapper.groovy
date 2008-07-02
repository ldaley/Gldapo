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
package gldapo.schema
import gldapo.GldapoDirectory
import org.springframework.ldap.core.ContextMapper
import org.springframework.ldap.core.DirContextAdapter

class GldapoContextMapper implements ContextMapper
{
    GldapoSchemaRegistration schemaRegistration
    GldapoDirectory directory

    Object mapFromContext(context) 
    {
        def entry = schemaRegistration.schema.newInstance()
        
        entry.directory = directory
        entry.rdn = context.dn
        entry.exists = true
        
        schemaRegistration.attributeMappings.each { name, mapping ->
            mapping.mapFromContext(context, entry)
        }
        
        entry.snapshotStateAsClean()
        
        return entry
     }
}