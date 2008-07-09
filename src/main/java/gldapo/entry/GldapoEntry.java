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
     * A value of both {@code null} and a dn of {@code ""} indicate that the
     * entry's parent is the base of it's directory.
     */
    public DistinguishedName getParent() { return null; }
    
    /**
     * Defines the location for this entry by defining it's parent container.
     * 
     * @param parent the string representation of this object will form the parent {@link DistinguishedName dn}
     */
    public void setParent(Object parent) {} 
    
}