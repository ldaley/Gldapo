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
package gldapo.operation;

/**
 * Operations are abstractions of particular LDAP functions.
 */
public interface GldapoOperation
{
    /**
     * Invokes the operation.
     */
    public Object execute() throws Exception;
    
    /**
     * The operation registry that the operation comes from passes on the gldapo instance.
     */
    public void setGldapo(Object gldapo);
}