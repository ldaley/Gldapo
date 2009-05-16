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
import gldapo.exception.GldapoSchemaInitializationException
import gldapo.GldapoTypeMappingRegistry
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import javax.naming.directory.ModificationItem
import javax.naming.directory.BasicAttribute
import javax.naming.directory.DirContext

class MultiValueAttributeMapping extends AbstractAttributeMapping
{
    static DEFAULT_COLLECTION_ELEMENT_TYPE = String
    
    static SUPPORTED_COLLECTION_TYPE_MAP = [
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
        if (!SUPPORTED_COLLECTION_TYPE_MAP.containsKey(t)) 
            throw new GldapoSchemaInitializationException("${this.schema.name}#${this.field.name}: $t is not a supported collection type, supported types are ${SUPPORTED_COLLECTION_TYPE_MAP.keySet()}")
        return SUPPORTED_COLLECTION_TYPE_MAP[t]
    }
        
    def calculateTypeMappingFromFieldType()
    {
        def t = this.field.genericType
        typeNameFromClass((t instanceof ParameterizedType) ? t.actualTypeArguments[0] : DEFAULT_COLLECTION_ELEMENT_TYPE)
    }
    
    def getGroovyValueFromContext(Object context)
    {        
        def rawValues = context.getAttributes("", [this.attributeName] as String[]).get(this.attributeName)
        def mappedValue = this.collectionType.newInstance()
        
        if (rawValues == null || rawValues.size() < 1) {
            return []
        } else {
            def rawValuesEnum = rawValues.getAll()
            while (rawValuesEnum.hasMore())
                mappedValue << this.toGroovyTypeMapper.call(rawValuesEnum.next())
            return mappedValue
        }
    }
    
    protected calculateModificationItems(clean, dirty) {
        def modificationItems = []
        if ((clean != null && clean.empty == false) && (dirty == null || dirty.empty == true)) {
            def attribute = new BasicAttribute(this.attributeName)
            modificationItems << new ModificationItem(DirContext.REMOVE_ATTRIBUTE, attribute)
        } else {
            
            def removeMod = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute(this.attributeName))
            clean.each { obj ->
                if ((obj.class.array) ? ((dirty.find { Arrays.deepEquals(it, obj) }) == null) : (dirty.contains(obj) == false))  {
                    removeMod.attribute.add(this.mapToLdapType(obj))
                }
            }
            if (removeMod.attribute.size() > 0) {
                modificationItems << removeMod
            }
            
            def addMod = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute(this.attributeName))
            dirty.each { obj ->
                if (clean == null || (obj.class.array) ? ((clean.find { Arrays.deepEquals(it, obj) }) == null) : (clean.contains(obj) == false)) {
                    addMod.attribute.add(this.mapToLdapType(obj))
                }
            }
            if (addMod.attribute.size() > 0) {
                modificationItems << addMod
            }
            
        }
        
        return modificationItems
    }
    
    protected getAttribute(value) {
        def attribute = new BasicAttribute(this.attributeName)
        value.each {
            attribute.add(this.mapToLdapType(it))
        }
        return attribute
    }
    
}