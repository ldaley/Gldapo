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
package gldapo
import gldapo.exception.GldapoInitializationException
import gldapo.exception.GldapoInvalidConfigException
import org.springframework.ldap.core.DistinguishedName
import gldapo.schema.annotation.GldapoNamingAttribute
import gldapo.schema.annotation.NullOnly

class GldapoTest extends GroovyTestCase 
{
    void testInitialiseDefaultConf()  {
        Gldapo.initialize("dev")
        assertEquals(2, Gldapo.instance.directories.size())
        assertEquals(50, Gldapo.instance.directories["t1"].searchControls.countLimit) // Tests env collapse
    }
    
    void testNullUrlExplodes() {
        shouldFail {
            Gldapo.initialize(new File("2853kgmpv0").toURL())
        }
    }
    
    void testExtractDirectoriesFromConfig() {
        def directories = Gldapo.extractDirectoriesFromConfig(
            directories: [
                d1: [
                    url: "ldap://example.com",
                    base: "dc=example,dc=com",
                    searchControls: [
                        countLimit: 20
                    ]
                ],
                d2: [
                    url: "ldap://example.com",
                    base: "dc=example,dc=com",
                    searchControls: [
                        countLimit: 50
                    ]
                ]
            ],
            defaultDirectory: "d2"
        )
        
        assertTrue(directories instanceof List)
        assertEquals(2, directories.size())
        assertEquals("d1", directories[0].name)
        assertEquals(new DistinguishedName("dc=example, dc=com"), directories[0].base)
        assertEquals(20, directories[0].searchControls.countLimit)
        
        shouldFail(GldapoInvalidConfigException) { Gldapo.extractDirectoriesFromConfig(directories: "blah") }
    }

    void testExtractTypeMappingsFromConfig() {
        def typemappings = Gldapo.extractTypeMappingsFromConfig(
            typemappings: [
                GldapoTestSchema1, GldapoTestSchema2
            ]
        )
        assertEquals(2, typemappings.size())
        assertTrue(typemappings.contains(GldapoTestSchema1))
        assertTrue(typemappings.contains(GldapoTestSchema2))
        
        shouldFail(GldapoInvalidConfigException) { Gldapo.extractTypeMappingsFromConfig(typemappings: "blah") }
    }
    
    void testExtractSchemasFromConfig() {
        def schemas = Gldapo.extractSchemasFromConfig(
            schemas: [
                GldapoTestSchema1, GldapoTestSchema2
            ]
        )
        assertEquals(2, schemas.size())
        assertTrue(schemas.contains(GldapoTestSchema1))
        assertTrue(schemas.contains(GldapoTestSchema2))

        shouldFail(GldapoInvalidConfigException) { Gldapo.extractSchemasFromConfig(schemas: "blah") }
    }
    
    void testExtractConstraintTypesFromConfig() {
        def constraintTypes = [NullOnly] 
        assertEquals(constraintTypes, Gldapo.extractConstraintTypesFromConfig(constraintTypes: constraintTypes))
        assertEquals([], Gldapo.extractConstraintTypesFromConfig([:]))
    }
    
    void testConsumeConfig() {
        def gldapo = new Gldapo()
        
        gldapo.consumeConfig(
            directories: [
                d1: [
                    url: "ldap://example.com",
                    base: "dc=example,dc=com",
                    searchControls: [
                        countLimit: 20
                    ]
                ],
                d2: [
                    url: "ldap://example2.com",
                    base: "dc=example2,dc=com",
                    ignorePartialResultException: true,
                    searchControls: [
                        countLimit: 50
                    ]
                ]
            ],
            defaultdirectory: "d2",
            typemappings: [
                String
            ],
            schemas: [
                GldapoTestSchema1, GldapoTestSchema2
            ]
        )
        
        assertEquals(2, gldapo.directories.size())
        assertNotNull("d1", gldapo.directories.d1)
        
        assertEquals(new DistinguishedName("dc=example, dc=com"), gldapo.directories.d1.base)
        
        assertEquals(20, gldapo.directories.d1.searchControls.countLimit)
        assertEquals(50, gldapo.directories.d2.searchControls.countLimit)
        
        assertEquals("d2", gldapo.directories.defaultDirectory.name)
        
        assertTrue(gldapo.typemappings.contains(String))
        assertNotNull(gldapo.schemas.find { it == GldapoTestSchema1 })
    }
    
    void testDefaultConstraintTypesGetRegistered() {
        def g = new Gldapo()
        Gldapo.defaultConstraintTypes.each {
            assertNotNull("default constraint type $it should be registered", g.constraintTypes[it])
        }
    }
}

class GldapoTestSchema1 {
    @GldapoNamingAttribute
    String sn
}

class GldapoTestSchema2 {
    @GldapoNamingAttribute
    String sn
}