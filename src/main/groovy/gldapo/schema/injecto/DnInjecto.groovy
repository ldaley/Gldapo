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
import gldapo.exception.GldapoException
import org.springframework.ldap.core.DistinguishedName
import injecto.annotation.InjectoProperty
import injecto.annotation.InjectAs
import injecto.annotation.InjectoDependency

@InjectoDependency(DirectoryInjecto)
class DnInjecto {
    
    @InjectoProperty
    DistinguishedName rdn = null

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
    
    @InjectoProperty(write = false)
    DistinguishedName dn = null
    
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
}