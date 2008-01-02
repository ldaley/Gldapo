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
package gldapo.operation
import gldapo.exception.GldapoOperationException

/**
 * Provides basic support for implementers of {@link GldapoOptionSubjectableOperation}
 * <p>
 * Allows subclasses to specify their option list in terms or required and optionals.
 */
abstract class AbstractGldapoOptionSubjectableOperation extends AbstractGldapoOperation implements GldapoOptionSubjectableOperation {
    
    /**
     * A list of option names that are required by the operation
     * 
     * @see #validateOptions()
     */
    List required
    
    /**
     * A list of option names that are optional for the operation
     */
    List optionals
    
    /**
     * The actual map of options
     */
    protected Map options
    
    abstract Object execute()
    
    /**
     * A hook for subclasses to act upon the options after they have been set, but before {@link #execute()}
     */
    abstract void inspectOptions()
    
    /**
     * Performs validation and inspection after setting the options.
     * <p>
     * After setting the options instance var, calls ...
     * <ul>
     * <li>{@link #validateOptions()}
     * <li>{@link #inspectOptions()}
     * </ul>
     * 
     * @param options The new options map
     */
    void setOptions(Map options) {
        this.options = options
        this.validateOptions()
        this.inspectOptions()
    }
    
    /**
     * Ensures that the required options were met.
     * 
     * @throws GldapoOperationException if any of the required options are present
     */
    void validateOptions() throws GldapoOperationException {
        def requiredNotSeen = required.clone()
        
        options?.keySet()?.each {
            if (required?.contains(it)) {
                requiredNotSeen.remove(it)
            }
            else if (optionals?.contains(it) == false) { 
                throw new GldapoOperationException("Option '${it}' of ${this.class.simpleName} is not supported")
            }
        }
        
        if (requiredNotSeen.size() != 0) throw new GldapoOperationException("Required options '${requiredNotSeen}' of ${this.class.simpleName} is not present in options")
    }
}