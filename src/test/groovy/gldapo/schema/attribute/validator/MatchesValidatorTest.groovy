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
package gldapo.schema.attribute.validator
import java.util.regex.PatternSyntaxException

class MatchesValidatorTest extends AbstractAttributeValidatorTest {
    
    def validator = MatchesValidator
    
    void testBadPattern() {
        shouldFail(PatternSyntaxException) {
            createValidator([pattern: "["], [:])
        }
    }

    void testGoodPattern() {
        createValidator([pattern: "a"], [:])
    }
    
    void testNullPattern() {
        shouldFail(IllegalStateException) {
            createValidator([pattern: null], [:])
        }
    }
    
    void testErrorCodes() {
        def v = createValidator([pattern: "a", label: "a"], [:])
        assertEquals(["matches.invalid", "matches.invalid.a"], v.errorCodes)
        v = createValidator([pattern: "a"], [:])
        assertEquals(["matches.invalid"], v.errorCodes)
    }
    
    void testValidate() {
        def v = createValidator([pattern: "a"], [:])
        
        assertNull(v.validate("a"))
        assertNotNull(v.validate("b"))
        
        assertEquals(2, v.validate(["a", "b", "c"]).size())
        assertNull(v.validate(["a", "a", "a"]))
        
        assertNull(v.validate(null))
        assertNull(v.validate([]))
    }
}