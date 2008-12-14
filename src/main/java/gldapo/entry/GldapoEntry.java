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
import gldapo.schema.annotation.*;
import gldapo.filter.FilterBuilder;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapRdn;
import java.util.Map;
import java.util.List;
import groovy.lang.Closure;
import org.springframework.validation.Errors;

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
     * The absolute location of this entry in it's directory.
     * 
     * The {@link #getBrdn() brdn}, plus the {@link GldapoDirectory#getBase() base} 
     * of this object's directory.
     * 
     * @throws GldapoException if this object has no directory, or naming value set 
     */
    public DistinguishedName getDn() throws GldapoException { return null; }
    
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
     * This value cannot be changed once set. See {@link #move(Object,Object)} if you need
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
    
    /**
     * Fetches the entry at {@code dn} from {@code directory}.
     * 
     * If {@code directory} is {@code null}, the {@link GldapoDirectoryRegistry#getDefaultDirectory() default directory} will
     * be used.
     * 
     * @param dn an object's whose string representation is the dn of the target entry
     * @param directory if not a {@link GldapoDirectory directory}, an objects whose string representation
     *        is the name of a registered directory
     * @return an entry object, or {@code null} if there is no entry at {@code dn}
     * @throws GldapoException if dn or directory are invalid, or an LDAP error occurs.
     * 
     */
    public static GldapoEntry getByDn(Object dn, Object directory) throws GldapoException { return null; }
    
    /**
     * Fetches the entry at {@code dn} from the the {@link GldapoDirectoryRegistry#getDefaultDirectory() default directory}.
     * 
     * Calls {@link #getByDn(Object,Object) getByDn(dn,null)}.
     */
    public static GldapoEntry getByDn(Object dn) throws GldapoException { return null; }    
    
    /**
     * Test attribute values to make sure they satisfy any specified constraints.
     * 
     * See http://gldapo.codehaus.org/validation.
     * 
     * @return {@code true} if all constraints are satisfied, {@code false} if not
     */
    public boolean validate() {}
    
    /**
     * The {@link Errors} object that contains the validation errors.
     * 
     * See http://gldapo.codehaus.org/validation.
     * 
     * @return the validation errors.
     */
    public Errors getErrors() {}
    
    /**
     * Discards any previously recorded validation errors.
     * 
     * See http://gldapo.codehaus.org/validation.
     */
    public void clearErrors() {}
    
    /**
     * Writes the object in it's entirety to the directory after {@link #validate() validating} it.
     * 
     * Before an object can be written, it must have a location and a {@link #setDirectory(Object) directory}. To have
     * a location, it must at least have a naming value.
     * 
     * @return {@code true} if the entry was created, {@code false} if it failed validation.
     * @throws GldapoException If the object is in an invalid state for creating, or an LDAP error occurs.
     */
    public boolean create() throws GldapoException {} 
    
    /**
     * Saves any modifications made to this object to the directory after {@link #validate() validating} it.
     * 
     * This will only send any changes made, so is not suitable for {@link #create() creating} new objects.
     * If this object has no modifications, this is a no-op.
     * 
     * @return {@code true} if the entry was updated, {@code false} if it failed validation.
     * @throws GldapoException if the object is not in a suitable state for updating, or an LDAP error occurs.
     */
    public boolean update() throws GldapoException {} 
    
    /**
     * {@link #create() Creates} or {@link #update() updates} the object depending on whether it is a new object or not,
     * after {@link #validate() validating} it.
     * 
     * @return {@code true} if the entry was saved, {@code false} if it failed validation.
     * @throws GldapoException if the object is not in a suitable state for saving, or an LDAP error occurs.
     */
    public boolean save() throws GldapoException {} 
    
    /**
     * Relocate this object in the directory, after {@link #update() sending any updates}.
     * 
     * @param brdn the new {@link #getBrdn() brdn} of the object (the string representation will be used).
     * @return {@code true} if the entry was moved, {@code false} if it failed validation.
     * @throws GldapoException if {@code brdn} is invalid, this is not an existing entry, or an LDAP error occurs.
     */
    public boolean move(Object brdn) throws GldapoException {} 
    
    /**
     * Relocate this object in the directory, after {@link #update() sending any updates}.
     * 
     * @param namingValue the new naming value (the string representation will be used). If {@code null},
     *        the existing naming value will be used.
     * @param parent the new parent container (the string representation will be used). If {@code null},
     *        the existing parent value will be used. Use {@code ""} to specify no parent.
     * @return {@code true} if the entry was moved, {@code false} if it failed validation.
     * @throws GldapoException if {@code brdn} is invalid, this is not an existing entry, or an LDAP error occurs.
     */
    public boolean move(Object namingValue, Object parent) throws GldapoException {} 
    
    /**
     * Replaces the entry @ {@code brdn} with this object, after {@link #validate() validating} it.
     * 
     * @param brdn the location of the entry to replace (the string representation will be used).
     * @return {@code true} if the entry was replaced, {@code false} if it failed validation.
     * @throws GldapoException if this object has no directory, or and LDAP error occurs.
     */
    public boolean replace(Object brdn) throws GldapoException {}
    
    /**
     * Replaces the entry at the location specified by {@code namingValue} and {@code parent} with this
     * object, after {@link #validate() validating} it.
     * 
     * @param namingValue the replacee's naming value (the string representation will be used). If {@code null},
     *        this object's naming value will be used.
     * @param parent the replacee's parent container (the string representation will be used). If {@code null},
     *        this object's parent value will be used. Use {@code ""} to specify no parent.
     * @return {@code true} if the entry was created, {@code false} if it failed validation.
     * @throws GldapoException if this object has no directory, or and LDAP error occurs.
     */
    public boolean replace(Object namingValue, Object parent) throws GldapoException {}
    
    /**
     * Replaces the entry specified by this objects's {@link #getBrdn() brdn} with this object, 
     * after {@link #validate() validating} it.
     * 
     * @return {@code true} if the entry was replaced, {@code false} if it failed validation.
     * @throws GldapoException if this object has no directory, no valid brdn, or and LDAP error occurs.
     */
    public boolean replace() throws GldapoException {} 
    
    /**
     * Removes the entry specified by this object's {@link #getBrdn() brdn} from the directory.
     * 
     * This <strong>will</strong> fail if the entry has children, use {@link #deleteRecursively()} in that case.
     * 
     * @throws GldapoException if this object has no directory, no valid brdn, or and LDAP error occurs.
     */
    public void delete() throws GldapoException {}
    
    /**
     * Removes the entry specified by this object's {@link #getBrdn() brdn} from the directory, and all it's children.
     * 
     * @throws GldapoException if this object has no directory, no valid brdn, or and LDAP error occurs.
     */
    public void deleteRecursively() throws GldapoException {}
    
    /**
     * Test if {@code password} is this entry's password.
     * 
     * @throws GldapoException if this object has no location or directory defined, or an LDAP error occurs
     */
    public boolean authenticate(String password) throws GldapoException { return true; }
    
    /**
     * Performs a search returning all of the matching entries.
     * <p>
     * The {@code options} map can contain the following items (but requires none):
     * <p>
     * <table border="1" cellspacing="0" cellpadding="2">
     * <tr><th>Key</th><th>Value Description</th></tr>
     * <tr><td>{@code directory}</td><td>The directory to use. Either by name or instance. If {@code null}, the default directory will be used</td></tr> 
     * <tr><td>{@code filter}</td><td>The filter used to restrict the search (string value will be used, or passed to filter builder if {@link Closure}). Will be anded with the {@link GldapoSchemaFilter schema filter} if present.</td></tr>
     * <tr><td>{@code base}</td><td>The dn (relative to the base of the directory) to start the search at. If {@code null}, the directory base will be used.</td></tr> 
     * <tr><td>{@code absoluteBase}</td><td>The dn to start the search at, but absolutely, not relative to the directory base.</td></tr> 
     * <tr><td>{@code searchScope}</td><td>A string indicating the scope of the search (either {@code "object"}, {@code "onelevel"} or {@code "subtree"})</td></tr> 
     * <tr><td>{@code pageSize}</td><td>The number of entries to return in one page. If {@code null} or less than 1, paging will <em>not</em> be used</td></tr>
     * <tr><td>{@code countLimit}</td><td>The maximum amount of entries for the server to return. No limit is used if {@code null}. (be mindful that some servers set limits server side)</td></tr> 
     * <tr><td>{@code timeLimit}</td><td>The maximum number of milliseconds to spend searching. No limit is used if {@code null}. (be mindful that some servers set limits server side)</td></tr> 
     * <tr><td>{@code derefLinkFlag}</td><td>A boolean to enable/disable link dereferencing during the search.</td></tr> 
     * </table>
     * <p>
     * Values override the default search options defined by the target directory. Where an option is omitted from {@code options},
     * the value from the directory's set options.
     * <p>
     * If the {@code filter} option is a closure, it will be used with {@link FilterBuilder} to build the filter string.
     * 
     * @return the found objects, or an empty list if none were found.
     * @throws GldapoException if the proposed options are invalid in anyway, or an LDAP error occurs.
     */
    static public List<GldapoEntry> findAll(Map options) throws GldapoException { return null; } 
    
    /**
     * Calls {@link #findAll(Map)}, after setting {@code filter} in {@code options} to {@code closure}
     */
    static public List<GldapoEntry> findAll(Map options, Closure closure) throws GldapoException { return null; } 

    /**
     * Calls {@link #findAll(Map)}, with a {@code filter} option of {@code closure}.
     */
    static public List<GldapoEntry> findAll(Closure closure) throws GldapoException { return null; } 
    
    /**
     * Peforms a search using all default options.
     * 
     * @see #findAll(Map)
     * @return the found objects, or an empty list if none were found.
     * @throws GldapoException if the proposed options are invalid in anyway, or an LDAP error occurs.
     */
    static public GldapoEntry findAll() throws GldapoException { return null; }
    
    /**
     * Peform a search, but returning the first object found.
     * 
     * @param options the same as {@link #findAll(Map)}
     * @return the entry, or {@code null} if not found
     * @throws GldapoException if the proposed options are invalid in anyway, or an LDAP error occurs.
     */
    static public GldapoEntry find(Map options) throws GldapoException { return null; } 

    /**
     * Calls {@link #find(Map)}, after setting {@code filter} in {@code options} to {@code closure}
     */
    static public List<GldapoEntry> find(Map options, Closure closure) throws GldapoException { return null; } 

    /**
     * Calls {@link #find(Map)}, with a {@code filter} option of {@code closure}.
     */
    static public List<GldapoEntry> find(Closure closure) throws GldapoException { return null; }
    
    /**
     * Peform a search (using default options), returning the first object found.
     * 
     * @return the entry, or {@code null} if not found
     * @throws GldapoException if the proposed options are invalid in anyway, or an LDAP error occurs.
     */    
    static public GldapoEntry find() throws GldapoException { return null; } 
}