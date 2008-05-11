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
package gldapo.search
import gldapo.exception.GldapoException
 
class SearchControls implements SearchControlProvider, Cloneable {

    public static final OBJECT_SEARCHSCOPE = "object"
    public static final ONELEVEL_SEARCHSCOPE = "onelevel"
    public static final SUBTREE_SEARCHSCOPE = "subtree"
    
    static Map SEARCHSCOPE_MAPPING = [
        (OBJECT_SEARCHSCOPE): javax.naming.directory.SearchControls.OBJECT_SCOPE,
        (ONELEVEL_SEARCHSCOPE): javax.naming.directory.SearchControls.ONELEVEL_SCOPE,
        (SUBTREE_SEARCHSCOPE): javax.naming.directory.SearchControls.SUBTREE_SCOPE
    ]
    
    Integer countLimit
    Boolean derefLinkFlag
    String searchScope
    Integer timeLimit
    Integer pageSize
        
    SearchControls(Map config) {
        if (config) {
            if (config.containsKey("countLimit")) this.countLimit = config.countLimit
            if (config.containsKey("derefLinkFlag")) this.derefLinkFlag = config.derefLinkFlag
            if (config.containsKey("searchScope")) this.searchScope = config.searchScope
            if (config.containsKey("timeLimit")) this.timeLimit = config.timeLimit
            if (config.containsKey("pageSize")) this.pageSize = config.pageSize
        }
    }

    def clone() {
        def n = new SearchControls()
        if (this.countLimit != null) n.countLimit = this.countLimit
        if (this.derefLinkFlag != null) n.derefLinkFlag = this.derefLinkFlag
        if (this.searchScope != null) n.searchScope = this.searchScope
        if (this.timeLimit != null) n.timeLimit = this.timeLimit
        if (this.pageSize != null) n.pageSize = this.pageSize
        return n
    }
    
    /**
     * @todo Use an inspector or something to do this dynamically
     */
    SearchControls mergeWith(controls) {
        def m = this.clone()
        
        if (controls.countLimit != null) m.countLimit = controls.countLimit
        if (controls.derefLinkFlag != null) m.derefLinkFlag = controls.derefLinkFlag
        if (controls.searchScope != null) m.searchScope = controls.searchScope
        if (controls.timeLimit != null) m.timeLimit = controls.timeLimit
        if (controls.pageSize != null) m.pageSize = controls.pageSize
        
        return m
    }
    
    void setSearchScope(String sc) {
        if (!SEARCHSCOPE_MAPPING.containsKey(sc)) throw new GldapoException("'$sc' is not a valid search scope")
        this.searchScope = sc
    }

    Integer getSearchScopeAsInteger() {
        SEARCHSCOPE_MAPPING[this.searchScope]
    }
    
    def asType(Class c)
    {
        if (c.equals(javax.naming.directory.SearchControls)) {
            def controls = new javax.naming.directory.SearchControls()
            if (this.countLimit != null) controls.countLimit = this.countLimit
            if (this.derefLinkFlag != null) controls.derefLinkFlag = this.derefLinkFlag
            if (this.searchScope != null) controls.searchScope = this.searchScopeAsInteger
            if (this.timeLimit != null) controls.timeLimit = this.timeLimit
            controls.returningObjFlag = true
            return controls
        }
        else {
            super(c)
        }
    }
}