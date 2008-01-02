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
import gldapo.exception.GldapoException
import gldapo.GldapoTypeMappingRegistry
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType

class MultiValueAttributeMapping extends AbstractAttributeMapping
{
    static DEFAULT_COLLECTION_ELEMENT_TYPE = String
    
    static SUPPORTED_COLLECTION_TYPE_MAP = [
        (List): LinkedList,
        (Set): LinkedHashSet,
        (SortedSet): TreeSet
    ]
    
    /**
     * Multivalue attributes are declared by List<T>, Set<T> or SortedSet<T>
     */
    Class collectionType
    
    /**
     * @todo Check that this is actually a collection type, maybe
     */
    MultiValueAttributeMapping (Class schema, Field field, GldapoTypeMappingRegistry typemappings) {
        super(schema, field, typemappings)
        this.collectionType = this.calculateCollectionType()
    }
    
    /**
     * @todo Need to use a better exception
     */
    def calculateCollectionType()
    {
        def t = this.field.type
        if (!SUPPORTED_COLLECTION_TYPE_MAP.containsKey(t)) throw new GldapoException("$t is not a supported collection type, supported values are ${SUPPORTED_COLLECTION_TYPE_MAP.keys}")
        return SUPPORTED_COLLECTION_TYPE_MAP[t]
    }
        
    def calculateTypeMappingFromFieldType()
    {
        def t = this.field.genericType
        
        if (t instanceof ParameterizedType) 
        {
            return t.actualTypeArguments[0].simpleName
        }
        else
        {
            return DEFAULT_COLLECTION_ELEMENT_TYPE.simpleName
        }
    }
    
    def getFieldValue(Object context)
    {        
        def rawValues = context.getStringAttributes(this.attributeName)
        if (rawValues == null || rawValues.size() < 1)
        {
            return null
        }
        else
        {
            def mappedValue = this.collectionType.newInstance()
            0.upto(rawValues.size() - 1) {
                mappedValue << this.toFieldTypeMapper.call(rawValues[it])
            }
            return mappedValue
        }
    }
}