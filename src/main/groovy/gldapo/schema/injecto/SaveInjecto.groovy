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
package gldapo.schema.injecto
import injecto.annotation.InjectoDependencies
import injecto.annotation.InjectoProperty
import injecto.annotation.InjectAs
import org.apache.commons.lang.WordUtils
import gldapo.exception.GldapoException
import org.springframework.ldap.core.DistinguishedName
import javax.naming.directory.BasicAttributes

@InjectoDependencies([CleanValuesInjecto, SchemaRegistrationInjecto, DirectoryInjecto, DnInjecto])
class SaveInjecto {

    @InjectoProperty
    Boolean existingEntry = false
    
    /**
     * 
     */
    def getAttributes = { ->
        def schemaRegistration = delegate.class.schemaRegistration
        def attributes = new BasicAttributes()
        schemaRegistration.attributeMappings.each { attributeName, mapping ->
            attributes.put(mapping.getAttribute(delegate."$attributeName"))
        }
        
        return attributes
    }
    
    /**
     * Inspects the state of the object and calculates the {@link ModificationItem}'s that describe
     * the changes to this object since it was loaded.
     */
    def getModificationItems = { ->
        def schemaRegistration = delegate.class.schemaRegistration
        def modificationItems = []
        
        schemaRegistration.attributeMappings.each { attributeName, mapping ->
            def clean = delegate.getCleanValue(attributeName)
            def dirty = delegate."$attributeName"
            def modificationItemsForAttribute = mapping.calculateModificationItems(clean, dirty)
            if (modificationItemsForAttribute) {
                modificationItems.addAll(modificationItemsForAttribute)
            }
        }
        
        return modificationItems
    }

    /**
     * Writes an existing object back to the directory.
     * <p>
     * Uses the {@link GldapoDirectory#save(DistinguishedName,List<ModificationItem>)} method of this
     * object's directory if this object has any modification items.
     */
    def save = { ->        
        if (delegate.directory == null) 
            throw new GldapoException("save() called on object with no directory")
            
        if (delegate.rdn == null) 
            throw new GldapoException("save() called on object with no dn")
        
        if (delegate.existingEntry) {
            def modificationItems = delegate.modificationItems
            if (modificationItems.empty == false) delegate.directory.save(delegate.rdn, modificationItems)
        } else {
            delegate.directory.save(delegate.rdn, delegate.attributes)
        } 
        
        delegate.snapshotStateAsClean()
    }
    
    /**
     * Writes a new object to the directory
     */
    @InjectAs("save")
    def saveWithDnAndDirectory = { DistinguishedName rdn, directory ->
        delegate.rdn = rdn
        delegate.directory = directory
        directory.save()
    }
    
    /**
     * 
     */
    @InjectAs("save")
    def saveNewStringRdn = { String rdn, directory ->
        delegate.save(new DistinguishedName(rdn), directory) 
    }

    

}

