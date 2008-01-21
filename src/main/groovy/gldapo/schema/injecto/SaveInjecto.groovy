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
    Boolean exists = false
    
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
     * Ensures that the object has an rdn and a directory.
     * 
     * @throws GldapoException
     */
    def assertHasRdnAndDirectoryForOperation = { operation ->
        assertHasDirectoryForOperation(operation)
        assertHasRdnForOperation(operation)
    }
    
    /**
     * 
     */
    def assertHasDirectoryForOperation = { operation ->
        if (delegate.directory == null) 
            throw new GldapoException("'$operation' attempted on object with no directory")
    }
    
    /**
     * 
     */
    def assertHasRdnForOperation = { operation ->
        if (delegate.rdn == null) 
            throw new GldapoException("'$operation' attempted on object with no rdn")
    }
    
    /**
     * 
     */
    def create = { ->
        delegate.assertHasRdnAndDirectoryForOperation('create')
        delegate.directory.createEntry(delegate.rdn, delegate.attributes)
        delegate.snapshotStateAsClean()
    }

    /**
     * Writes an existing object back to the directory.
     * <p>
     * Uses the {@link GldapoDirectory#updateEntry(DistinguishedName,List<ModificationItem>)} method of this
     * object's directory if this object has any modification items.
     */    
    def update = { ->
        delegate.assertHasRdnAndDirectoryForOperation('update')
        def modificationItems = delegate.modificationItems
        if (!modificationItems.empty) delegate.directory.updateEntry(delegate.rdn, modificationItems)
        delegate.snapshotStateAsClean()
    }
    
    /**
     * Convenience method to create the entry if it doesn't exist, or update it if it does.
     * 
     * @see getCreate()
     * @see getSave()
     */
    def save = { ->                
        (delegate.exists) ? delegate.update() : delegate.create()
    }

    /**
     * 
     */
    def move = { DistinguishedName newrdn ->
        if (delegate.exists) {
            delegate.update()
        } else {
            throw new GldapoException("'move' attempted on an object that does not exist")
        }
        delegate.directory.moveEntry(delegate.rdn, newrdn)
        delegate.rdn = newrdn
    }
    
    /**
     * 
     */
    def replace = { DistinguishedName target ->
        assertHasDirectoryForOperation('replace')
        delegate.directory.replaceEntry(target, delegate.attributes)
        delegate.rdn = target
        delegate.snapshotStateAsClean()
    }

}