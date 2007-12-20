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
package gldapo.directory
import org.springframework.ldap.core.support.LdapContextSource

/**
 * Provide constructing from config map functionality
 */
class GldapoContextSource extends LdapContextSource {

    /**
     * The list of properties that can be set in the config
     * 
     * @see #newInstance(Map)
     */
    public static final PROPS = ["url", "urls", "base", "userDn", "password"]
    
    /**
     * Constructs a new context source from a config map.
     * <p>
     * Can have the following keys:
     * <ul>
     * <li>url
     * <li>urls
     * <li>base
     * <li>userDn
     * <li>password
     * </ul>
     * <p>
     * {@code url} is a single url string (ldap://example.com). {@code urls} is a List of url strings. Only one of these attributes are required.
     * <p>
     * {@code userDn} and {@code password} are both optional. If ommitted, an anonymous context will be used.
     * <p>
     * {@code base} is the base location for all search operations
     */
    static newInstance(Map config) {
        def c = new GldapoContextSource()
        PROPS.each { if (config.containsKey(it)) c[it] = config[it] }
        c.afterPropertiesSet()
        return c
    }
}