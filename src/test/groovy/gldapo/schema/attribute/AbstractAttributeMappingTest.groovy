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
import java.lang.reflect.Field
import gldapo.GldapoTypeMappingRegistry
import gldapo.Gldapo
import javax.naming.directory.ModificationItem
import javax.naming.directory.BasicAttribute

abstract class AbstractAttributeMappingTest extends GroovyTestCase 
{
    abstract def getMappingClass()
    abstract def getMappingSubjectClass()
    abstract def getFakeContext(val)
        
    static gldapo = new Gldapo()
    
    static {
        ModificationItem.metaClass.equals = { ModificationItem it ->
            (delegate.attribute == it.attribute && delegate.modificationOp == it.modificationOp)
        }
    }
    
    def mappingForField(String fieldName) {
        this.mappingClass.getConstructor(Class, Field, GldapoTypeMappingRegistry).newInstance(
            this.mappingSubjectClass, 
            this.mappingSubjectClass.getDeclaredField(fieldName), 
            gldapo.instance.typemappings
        )
    }
    
    void verifyProperties(mapping, attributeName, typeMapping) {
        assertEquals("[fieldName: ${mapping.field.name}] - attributeName", attributeName, mapping.attributeName)
        assertEquals("[fieldName: ${mapping.field.name}] - typeMapping", typeMapping, mapping.typeMapping)
    }
    
    void verifyTypeConversion(mapping, ldapValue, groovyValue) {
        assertEquals("[fieldName: ${mapping.field.name}] - map to groovy", groovyValue, mapping.mapToGroovyType(ldapValue))
        assertEquals("[fieldName: ${mapping.field.name}] - map to ldap", ldapValue, mapping.mapToLdapType(groovyValue))
    }
    
    void verifyMapFromContext(mapping, contextValue, mappedValue) {
        def context = getFakeContext(contextValue)
        def subject = new Expando()
        mapping.mapFromContext(context, subject)

        assertEquals("[fieldName: ${mapping.field.name}] - context map test", mappedValue, subject."${mapping.field.name}")        
    }
    
    void verifyCalculateModificationItems(mapping, clean, dirty, mods) {
        
        def comparator = { lh, rh ->
            def idComparison = lh.attribute.iD <=> rh.attribute.iD 
            if (idComparison != 0) return idComparison
            
            def modificationOpComparison = lh.modificationOp <=> rh.modificationOp
            if (modificationOpComparison != 0) return modificationOpComparison
            
            def lhValues = new TreeSet(lh.attribute.getAll().toList())
            def rhValues = new TreeSet(rh.attribute.getAll().toList())
            def lhIterator = lhValues.iterator()
            def rhIterator = rhValues.iterator()
            
            while (true) {
                if (lhIterator.hasNext() == false && rhIterator.hasNext() == false) {
                    return 0
                } else if (lhIterator.hasNext() == true && rhIterator.hasNext() == false) {
                    return -1
                } else if (lhIterator.hasNext() == false && rhIterator.hasNext() == true) {
                    return 1
                } else {
                    def lhValue = lhIterator.next()
                    def rhValue = rhIterator.next()
                    def c =  lhValue <=> rhValue
                    if (c != 0) {
                        return c
                    }
                }
            }
            
            return 0
        }
        
        def expectedMods = mods?.collect { 
            def attribute = new BasicAttribute(mapping.attributeName)
            def mod = new ModificationItem(it[0], attribute)
            it[1].each { mod.attribute.add(it) }
            return mod
        }?.sort(comparator)
        if (expectedMods == null) expectedMods = []
        
        def actualMods = mapping.calculateModificationItems(clean, dirty)?.sort(comparator)
        if (actualMods == null) actualMods = []
        
        assert expectedMods == actualMods : "[fieldName: ${mapping.field.name}] - mod item test"
    }
}