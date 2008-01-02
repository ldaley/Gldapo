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
import gldapo.schema.GldapoSchemaRegistration

class GldapoSchemaRegistryTest extends GroovyTestCase {

    
    
    void testAddClass() {
        def gldapo = new Gldapo()
        def registry = gldapo.schemas
        registry << RegistryTestSchema1
        assertEquals(1, registry.size())
        assertEquals(GldapoSchemaRegistration, registry[0].class)
        assertSame(RegistryTestSchema1, registry[0].schema)
    }

    void testAddRegistration() {
        def gldapo = new Gldapo()
        def registry = gldapo.schemas
        registry << new GldapoSchemaRegistration(RegistryTestSchema1, new Gldapo())
        assertEquals(1, registry.size())
        assertEquals(GldapoSchemaRegistration, registry[0].class)
        assertSame(RegistryTestSchema1, registry[0].schema)
    }

    void testAddRubbish() {
        shouldFail(IllegalArgumentException) { 
            def gldapo = new Gldapo()
            def registry = gldapo.schemas
            registry << "" 
        }
    }

    void testIsRegistered() {
        def gldapo = new Gldapo()
        def registry = gldapo.schemas
        registry << RegistryTestSchema1
        assertTrue(registry.isRegistered(RegistryTestSchema1))
        assertFalse(registry.isRegistered(RegistryTestSchema2))
        registry << RegistryTestSchema2
        assertTrue(registry.isRegistered(RegistryTestSchema2))
    }

    void testGetAt() {
        def gldapo = new Gldapo()
        def registry = gldapo.schemas
        
        registry << RegistryTestSchema1
        registry << RegistryTestSchema2
        assertNotNull(registry[RegistryTestSchema1])
        assertEquals(GldapoSchemaRegistration, registry[RegistryTestSchema1].class)
        
        assertNull(registry[String])
    }
}

class RegistryTestSchema1 {}
class RegistryTestSchema2 {}


