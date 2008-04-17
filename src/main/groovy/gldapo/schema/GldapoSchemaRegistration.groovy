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
import gldapo.Gldapo
import gldapo.exception.GldapoSchemaInitializationException
import gldapo.schema.attribute.AttributeMappingInspector
import injecto.Injecto

class GldapoSchemaRegistration 
{
    Class schema
    Map attributeMappings
    Gldapo gldapo
    
    GldapoSchemaRegistration(Class schema, Gldapo gldapo)
    {
        this.gldapo = gldapo

        prepareSchemaClass(schema)
        
        schema.gldapo = gldapo
        schema.schemaRegistration = this
        
        this.schema = schema
        
        this.attributeMappings = AttributeMappingInspector.getAttributeMappings(schema, gldapo.typemappings)
    }
        
    static prepareSchemaClass(schema) {
        Injecto.inject(schema, GldapoSchemaClassInjecto)
    }
    
    boolean equals(Class clazz) {
       this.schema.equals(clazz)
    }
}
