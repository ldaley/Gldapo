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
import gldapo.GldapoOperationRegistry
import gldapo.GldapoDirectory
import gldapo.exception.GldapoException
import injecto.annotation.InjectoProperty
import injecto.annotation.InjectAs
import org.springframework.ldap.core.DistinguishedName
import org.apache.commons.lang.WordUtils
import javax.naming.directory.DirContext
import javax.naming.directory.Attributes
import javax.naming.directory.BasicAttributes
import org.springframework.ldap.AuthenticationException

class GldapoSchemaClassInjecto {

    @InjectoProperty
    static GldapoSchemaRegistration schemaRegistration = null
    
    @InjectoProperty
    static Gldapo gldapo = null

    @InjectoProperty
    GldapoDirectory directory = null

    @InjectoProperty
    DistinguishedName rdn = null
    
    @InjectoProperty(write = false)
    DistinguishedName dn = null
    
    @InjectoProperty(write = false)
    Map cleanValues = [:]
    
    @InjectoProperty
    Boolean exists = false
    
    def setCleanValue = { String name, Object value ->
        delegate.cleanValues[name] = value
    }

    def getCleanValue = { String name ->
        delegate.cleanValues[name]
    }

    def snapshotStateAsClean = {
        delegate.class.schemaRegistration.attributeMappings.each { attribute, mapping ->
            def value = delegate."$attribute"
            delegate.setCleanValue(attribute, (value instanceof Cloneable) ? value.clone() : value)
        }
    }
    
    def revert = { ->
        delegate.cleanValues.each { key, value ->
            delegate."$key" = (value instanceof Cloneable) ? value.clone() : value
        }
    }
    
    def setDirectory = { directory ->
        if (delegate.directory != null)
            throw new GldapoException("Cannot change directory on schema objects after it has been set")
        
        delegate.setInjectoProperty("directory", directory)
    }
    
    def setRdn = { DistinguishedName rdn ->
        if (delegate.rdn != null)
            throw new GldapoException("Cannot change rdn/dn on object once set")
            
        delegate.setInjectoProperty('rdn', rdn)
        delegate.setInjectoProperty('dn', null)
    }
    
    @InjectAs("setRdn")
    def setRdnAsString = { String rdn ->
        delegate.setRdn(new DistinguishedName(rdn))
    }
    
    def getDn = { ->
        def dn = delegate.getInjectoProperty('dn')
        if (dn == null) {
            dn = new DistinguishedName()
            dn.append(delegate.directory.base)
            dn.append(delegate.rdn)
            delegate.setInjectoProperty('dn', dn)
        }
        return dn
    }
    
    static getByDn = { DistinguishedName dn, GldapoDirectory directory ->
        delegate.find(absoluteBase: dn, searchScope: "object", directory: directory)
    }

    @InjectAs("getByDn")
    static getByStringDn = { String dn, GldapoDirectory directory ->
        delegate.find(absoluteBase: dn, searchScope: "object", directory: directory)
    }
    
    @InjectAs("getByDn")
    static getUsingDirectoryName = { DistinguishedName dn, String directoryName ->
        delegate.find(absoluteBase: dn, searchScope: "object", directory: directoryName)
    }
    
    @InjectAs("getByDn")
    static getUsingDirectoryNameAndStringDn = { String dn, String directoryName ->
        delegate.find(absoluteBase: dn, searchScope: "object", directory: directoryName)
    }

    @InjectAs("getByDn")
    static getUsingDefaultDirectory = { DistinguishedName dn -> 
        delegate.getByDn(dn, (String)null)
    }
    
    @InjectAs("getByDn")
    static getUsingDefaultDirectoryAndStringDn = { String dn -> 
        delegate.getByDn(dn, (String)null)
    }
    

    
    /**
     * 
     */
    def getAttributes = { ->
        def schemaRegistration = delegate.class.schemaRegistration
        def attributes = new BasicAttributes()
        schemaRegistration.attributeMappings.each { attributeName, mapping ->
            def value = delegate."$attributeName"
            if (!(value == null || (value instanceof Collection && value.size() == 0)))
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
     * If the object doesn't have a directory set, it will have the default directory
     * set from it's class's gldapo instance after this. If a directory is set, this does nothing.
     */
    def assumeDefaultDirectoryIfNoneSet = { ->
        if (delegate.directory == null) {
            def directoryRegistry = delegate.class.gldapo.directories
            if (directoryRegistry.isHasDefault()) 
                delegate.directory = directoryRegistry.defaultDirectory
        }
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
        assumeDefaultDirectoryIfNoneSet()
        delegate.assertHasRdnAndDirectoryForOperation('create')
        delegate.directory.createEntry(delegate.rdn, delegate.attributes)
        delegate.exists = true
        delegate.snapshotStateAsClean()
    }

    /**
     * Writes an existing object back to the directory.
     * <p>
     * Uses the {@link GldapoDirectory#updateEntry(DistinguishedName,List<ModificationItem>)} method of this
     * object's directory if this object has any modification items.
     */    
    def update = { ->
        assumeDefaultDirectoryIfNoneSet()
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
        assumeDefaultDirectoryIfNoneSet()
        if (delegate.exists) {
            delegate.update()
        } else {
            throw new GldapoException("'move' attempted on an object that does not exist")
        }
        delegate.directory.moveEntry(delegate.rdn, newrdn)
        delegate.setInjectoProperty('rdn', newrdn)
        delegate.setInjectoProperty('dn', null)
    }

    @InjectAs("move")
    def moveString = { String newrdn ->
        delegate.move(new DistinguishedName(newrdn))
    }
    
    /**
     * 
     */
    def replace = { DistinguishedName target ->
        assumeDefaultDirectoryIfNoneSet()
        assertHasDirectoryForOperation('replace')
        delegate.directory.replaceEntry(target, delegate.attributes)
        delegate.setInjectoProperty('rdn', target)
        delegate.setInjectoProperty('dn', null)
        delegate.snapshotStateAsClean()
    }
    
    def delete = {
        assumeDefaultDirectoryIfNoneSet()
        delegate.assertHasRdnAndDirectoryForOperation('delete')
        delegate.directory.deleteEntry(delegate.rdn)
        delegate.exists = false
    }

    def deleteRecursively = {
        assumeDefaultDirectoryIfNoneSet()
        delegate.assertHasRdnAndDirectoryForOperation('delete')
        delegate.directory.deleteEntryRecursively(delegate.rdn)
        delegate.exists = false
    }
    
    
    static findAll = { Map options ->
        def searchOptions = options.clone()
        searchOptions.schema = delegate
        
        delegate.gldapo.operations[GldapoOperationRegistry.SEARCH, searchOptions].execute()
    }
    
    @InjectAs("findAll")
    static findAllNoArgs = { -> 
        delegate.findAll([:])
    }
    
    static find = { Map options ->
        options.countLimit = 1
        def r = delegate.findAll(options)
        (r.size() > 0) ? r[0] : null
    }
    
    @InjectAs("find")
    static findNoArgs = { -> 
        delegate.find([:])
    }
    
    def authenticate = { password ->
        delegate.assertHasRdnAndDirectoryForOperation('authenticate')
        def contextSource = delegate.directory.getSubContextSource(delegate.rdn)
        contextSource.password = password
        
        def success = false
        def context
        
        try {
            contextSource.afterPropertiesSet()
            context = contextSource.getReadOnlyContext()
            success = true
        }
        catch(Exception e) {
            if (e instanceof AuthenticationException) {
                success = false
            } else {
                throw e
            }
        } finally {
            if (context != null) context.close()
        }

        return success
    }
}