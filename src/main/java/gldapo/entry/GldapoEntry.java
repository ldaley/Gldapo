/*
* Copyright 2007 the original author or authors.
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
package gldapo.entry;
import gldapo.*;
import gldapo.exception.*;
import gldapo.schema.annotation.GldapoNamingAttribute;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapRdn;

/**
 * The API reference for schema classes.
 * 
 * Schema classes do <strong>not</strong> extend this class
 * (i.e. {@code schemaObject instanceof GldapoEntry} will return false.)
 */
public abstract class GldapoEntry {
    
    /**
     * The connection to an actual LDAP directory.
     * 
     * @return The directory for this object, or {@code null} if not set yet.
     */
    public GldapoDirectory getDirectory() { return null; }
    
    /**
     * Before an <em>operation</em> (create, update, delete etc.) can be performed on an object,
     * it must have {@link GldapoDirectory directory} to perform the operation against.
     * 
     * @param directory If not an instance of {@link GldapoDirectory}, will be {@code toString()}'d
     *        and treated as a directory name and the corresponding directory will be pulled
     *        from the class's gldapo instance.
     * @throws GldapoDirectoryNotFoundException If {@code directory} is used as a name, and no directory
     *         is registered with that name.
     */
    public void setDirectory(Object directory) throws GldapoDirectoryNotFoundException {}
    
    /**
     * The <em>Base Relative Distinguished Name</em> for this entry.
     * 
     * The 'brdn' denotes the location of the entry in the directory, relative to the {@link GldapoDirectory#getBase() base}
     * of the object's directory. It is a combination of the {@link #getParent() parent}, and 
     * {@link #getNamingAttribute() namingAttribute}/{@link #getNamingValue() namingValue} properties.
     * 
     * @return The brdn, or null if not set yet
     * @throws GldapoException if the {@link #getNamingValue() naming value} for this object is {@code null}
     */
    public DistinguishedName getBrdn() throws GldapoException { return null; }
    
    /**
     * Set the location of this entry, with a dn relative to the entry's directory base.
     * 
     * The {@code brdn} is converted to a string, and then a {@link DistinguishedName}. 
     * The leading (leftmost) {@link LdapRdn rdn} is seperated from the dn and the value
     * passed to {@link #setNamingValue(String)}. Any remaining rdns are passed as a single value
     * to {@link #setParent(Object)}.
     * 
     * @param brdn An object whose string representation will be the brdn
     * @throws GldapoException if the leading rdn of {@code brdn} has a different attribute name
     *         to the defined {@link #getNamingAttribute() naming attribute} for this class
     */
    public void setBrdn(Object brdn) throws GldapoException {}
    
    /**
     * The dn for the entry in the directory that contains this entry.
     * 
     * A dn of {@code ""} indicates that the entry's parent is the 
     * base of it's directory. If a parent has not been explicitly set,
     * an empty dn will be returned.
     * 
     * @return A dn, never {@code null}
     */
    public DistinguishedName getParent() { return null; }
    
    /**
     * Defines the location for this entry by defining it's parent container.
     * 
     * @param parent the string representation of this object will form the parent {@link DistinguishedName dn}
     */
    public void setParent(Object parent) {} 

    /**
     * The value for the {@link #getNamingAttribute() naming attribute}.
     * 
     * This defines part of the location/{@link #getBrdn() brdn} of this entry.
     * 
     * @return the naming value, or {@code null} if not yet set
     */
    public String getNamingValue() { return null; }
    
    /**
     * Sets the value of the {@link #getNamingAttribute() naming attribute}.
     * 
     * This value cannot be changed once set. See {@link #move(String,Object)} if you need
     * to move an entry (i.e. change it's naming value)
     * 
     * @param value the naming value
     * @throws GldapoException if this object already has a naming value
     */
    public void setNamingValue(String value) throws GldapoException {} 
    
    /**
     * The <em>name</em> of the property that is used to define this entry's name.
     * 
     * The naming attribute is defined by annotating a property with {@link GldapoNamingAttribute @GldapoNamingAttribute}
     * and is mandatory for schema classes.
     * 
     * @return the attribute name, never {@code null}
     */
    public String getNamingAttribute() { return null; }
    
}