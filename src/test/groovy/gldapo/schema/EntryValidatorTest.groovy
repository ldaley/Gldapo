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
package gldapo.schema
import gldapo.schema.annotation.*
import org.springframework.validation.BeanPropertyBindingResult
import gldapo.Gldapo

class EntryValidatorTest extends GroovyTestCase {

    def gldapo = new Gldapo(schemas: [EntryValidatorTestEntry], constraintTypes: [NullOnly])
    
    void testSupport() {
        def validator = new EntryValidator()
        assertTrue(validator.supports(EntryValidatorTestEntry))
        assertFalse(validator.supports(String))
    }
    
    void testValidate() {
        def e = new EntryValidatorTestEntry()
        def errors = new BeanPropertyBindingResult(e, e.class.name)
        def validator = new EntryValidator()
        validator.validate(e, errors)
        assertEquals(0, errors.fieldErrors.size())
        errors = new BeanPropertyBindingResult(e, e.class.name)
        e.v1 = "a"
        validator.validate(e, errors)
        assertEquals(1, errors.fieldErrors.size())
        errors = new BeanPropertyBindingResult(e, e.class.name)
        e.v2 = "a"
        validator.validate(e, errors)
        assertEquals(2, errors.fieldErrors.size())
        
        ["v1", "v2"].each { f ->
            def fe = errors.fieldErrors.find { it.field == f }
            assertNotNull(fe)
            ["notnull", "${e.class.simpleName}.${f}.notnull", "${e.class.simpleName}.notnull"].each { c ->
                assertNotNull("field error for $f does not contain $c", fe.codes.find { it == c })
            }
        }
        
    }
}

class EntryValidatorTestEntry {
    @GldapoNamingAttribute 
    String name
    
    @NullOnly
    String v1
    
    @NullOnly
    String v2
    
}