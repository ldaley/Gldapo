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
package gldapo.exception

class GldapoTypeMappingException extends GldapoException
{
    static Boolean MAPPING_TO_FIELD = Boolean.TRUE
    static Boolean MAPPING_FROM_FIELD = Boolean.FALSE
    
    GldapoTypeMappingException(Class schema, String fieldName, String typeName, Boolean direction, Exception cause) 
    {
        super(GldapoTypeMappingException.getPrefix(schema, fieldName, typeName, direction), cause)
    }
    
    GldapoTypeMappingException(Class schema, String fieldName, String typeName, Boolean direction, String message) 
    {
        super(GldapoTypeMappingException.getPrefix(schema, fieldName, typeName, direction) + message)
    }
    
    static String getPrefix(Class schema, String fieldName, String typeName, Boolean direction)
    {
        def directionName = (direction == MAPPING_TO_FIELD) ? "TO" : "FROM"
        "Mapping ${directionName} ${fieldName} ({$typeName}) of ${schema.name}: ".toString()
    }
}
