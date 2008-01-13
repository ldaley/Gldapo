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
import gldapo.GldapoDirectory
import injecto.annotation.InjectoDependency
import injecto.annotation.InjectAs
import org.springframework.ldap.core.DistinguishedName

@InjectoDependency(SearchingInjecto)
class GetInjecto 
{
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
}