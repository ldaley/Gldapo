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
import gldapo.search.SearchOptionParser
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

    @InjectoProperty(write = false) // TODO Why is Injecto installing a setter unless write = false here?
    DistinguishedName parent = null

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
    
    def setBrdn = { brdn ->
        if (delegate.namingValue != null)
            throw new GldapoException("Cannot change brdn/dn on object once set")
        
        def brdnCopy = (brdn instanceof DistinguishedName) ? brdn.clone() : new DistinguishedName(brdn as String)
        
        def namingRdn = brdnCopy.removeLast()
        if (namingRdn.key != delegate.namingAttribute)
            throw new GldapoException("Attempt to set brdn of object with a naming attribute of '${delegate.namingAttribute}' to '${brdn}'")

        delegate.namingValue = namingRdn.value
        delegate.setInjectoProperty('parent', brdnCopy)
    }

    def setParent = { parent ->
        if (parent != null) 
            parent = (parent instanceof DistinguishedName) ? parent.clone() : new DistinguishedName(parent as String)
            
        delegate.setInjectoProperty('parent', parent)
    }

    def getParent = { ->
        def parent = delegate.getInjectoProperty('parent')
        if (parent == null) {
            parent = new DistinguishedName()
            delegate.parent = parent
        }
        return parent
    }

    def getBrdn = { -> 
        def brdn = new DistinguishedName()

        def namingValue = delegate.namingValue
        if (namingValue == null)
            throw new GldapoException("getRdn() called on entry object with no naming value (naming attribute is '${delegate.namingAttribute}')")
        
        def parent = delegate.parent
        if (parent != null) brdn.append(delegate.parent)
        
        brdn.append(delegate.namingAttribute, delegate.namingValue)
        
        brdn
    }
    
    def getNamingAttribute = { ->
        delegate.class.schemaRegistration.namingAttributeFieldName
    }
    
    def setNamingValue = { value ->
        
        if (value != null && delegate.namingValue != null && delegate.namingValue != value)
            throw new GldapoException("Cannot set value for naming attribute ('${delegate.namingAttribute}') once set, see move() or replace()")

        def property = delegate.class.metaClass.getMetaProperty(delegate.namingAttribute)
        property.setProperty(delegate, value)
    }
    
    def getNamingValue = { ->
        delegate."${delegate.namingAttribute}"
    }

    def getDn = { ->
        def directory = delegate.directory
        if (directory == null)
            throw new GldapoException("getDn() called on entry with no directory")
            
        def namingValue = delegate.namingValue
        if (namingValue == null)
            throw new GldapoException("getDn() called on entry with no naming value")
            
        def dn = new DistinguishedName()
        dn.append(directory.base)
        dn.append(delegate.parent)
        dn.append(delegate.namingAttribute, namingValue)
        dn
    }
    
    static getByDn = { Object dn, Object directory ->
        delegate.find(absoluteBase: dn, searchScope: "object", directory: directory)
    }

    @InjectAs("getByDn")
    static getByDnUsingDefaultDirectory = { Object dn -> 
        delegate.getByDn(dn, null)
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
     * Ensures that the object has an brdn and a directory.
     * 
     * @throws GldapoException
     */
    def assertHasNamingValueAndDirectoryForOperation = { operation ->
        assertHasDirectoryForOperation(operation)
        assertHasNamingValueForOperation(operation)
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
    def assertHasNamingValueForOperation = { operation ->
        if (delegate.namingValue == null) 
            throw new GldapoException("'$operation' attempted on object with no naming value (${delegate.namingAttribute}) set")
    }
    
    /**
     * 
     */
    def create = { ->
        assumeDefaultDirectoryIfNoneSet()
        delegate.assertHasNamingValueAndDirectoryForOperation('create')
        delegate.directory.createEntry(delegate.brdn, delegate.attributes)
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
        delegate.assertHasNamingValueAndDirectoryForOperation('update')
        def modificationItems = delegate.modificationItems
        if (!modificationItems.empty) delegate.directory.updateEntry(delegate.brdn, modificationItems)
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
    def move = { newbrdn ->
        newbrdn = (newbrdn instanceof DistinguishedName) ? newbrdn : new DistinguishedName(newbrdn.toString())
        assumeDefaultDirectoryIfNoneSet()
        if (delegate.exists) {
            delegate.update()
        } else {
            throw new GldapoException("'move' attempted on an object that does not exist")
        }
        delegate.directory.moveEntry(delegate.brdn, newbrdn)
        delegate.parent = null
        delegate.namingValue = null
        delegate.brdn = newbrdn
    }
    
    @InjectAs("move")
    def moveByNamingAndParent = { namingValue, parent ->
        def brdn = new DistinguishedName()
        
        if (parent == null)
            parent = delegate.parent
        
        if (parent != null) {
            if (!(parent instanceof DistinguishedName)) 
                parent = new DistinguishedName(parent as String)
            brdn.append(parent)
        }
        
        if (namingValue == null) {
            if (delegate.namingValue == null)
                throw new GldapoException("First argument to move(String,Object) cannot be null if object has no naming value")
            namingValue = delegate.namingValue
        }
        brdn.append(delegate.namingAttribute, namingValue.toString())

        delegate.move(brdn)
    }
    
    def replace = { target ->
        target = (target instanceof DistinguishedName) ? target : new DistinguishedName(target.toString())
        assumeDefaultDirectoryIfNoneSet()
        assertHasDirectoryForOperation('replace')
        delegate.directory.replaceEntry(target, delegate.attributes)
        delegate.parent = null
        delegate.namingValue = null
        delegate.brdn = target
        delegate.snapshotStateAsClean()
    }
    
    @InjectAs("replace")
    def replaceByNamingAndParent = { String namingValue, parent ->
        def brdn = new DistinguishedName()
        
        if (parent != null) {
            if (!(parent instanceof DistinguishedName)) 
                parent = new DistinguishedName(parent as String)
            brdn.append(parent)
        }
        
        if (namingValue == null) {
            if (delegate.namingValue == null)
                throw new GldapoException("First argument to replace(String,Object) cannot be null if object has no naming value")
            namingValue = delegate.namingValue
        }
        brdn.append(delegate.namingAttribute, namingValue)

        delegate.replace(brdn)
    }

    @InjectAs("replace")
    def replaceNoParams = { ->
        delegate.replace(delegate.brdn)
    }

    def delete = {
        assumeDefaultDirectoryIfNoneSet()
        delegate.assertHasNamingValueAndDirectoryForOperation('delete')
        delegate.directory.deleteEntry(delegate.brdn)
        delegate.exists = false
    }

    def deleteRecursively = {
        assumeDefaultDirectoryIfNoneSet()
        delegate.assertHasNamingValueAndDirectoryForOperation('delete')
        delegate.directory.deleteEntryRecursively(delegate.brdn)
        delegate.exists = false
    }
    
    
    static findAll = { Map options ->
        def parser = new SearchOptionParser(delegate, options)
        parser.directory.search(delegate.schemaRegistration, parser.base, parser.filter, parser.controls)
    }
    
    @InjectAs("findAll")
    static findAllWithOptionsAndClosure = { Map options, Closure filter ->
        options.filter = filter
        delegate.findAll(options)
    }
    
    @InjectAs("findAll")
    static findAllWithClosure = { Closure filter ->
        delegate.findAll(filter: filter)
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
    static findWithOptionsAndClosure = { Map options, Closure filter ->
        options.filter = filter
        delegate.find(options)
    }
    
    @InjectAs("find")
    static findWithClosure = { Closure filter ->
        delegate.find(filter: filter)
    }
    
    @InjectAs("find")
    static findNoArgs = { -> 
        delegate.find([:])
    }
    
    def authenticate = { String password ->
        delegate.assertHasNamingValueAndDirectoryForOperation('authenticate')
        def contextSource = delegate.directory.getSubContextSource(delegate.brdn)
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
    
    def setProperty = { String name, value ->
        if (name == delegate.namingAttribute) {
            delegate.setNamingValue(value)
        } else {
            def metaProperty = delegate.class.metaClass.getMetaProperty(name)
            if (metaProperty) {
                metaProperty.setProperty(delegate, value)
            } else
                throw new MissingPropertyException(name, delegate.class)
        }
    }
}