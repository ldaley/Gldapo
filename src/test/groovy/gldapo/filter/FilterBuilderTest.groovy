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
package gldapo.filter

class FilterBuilderTest extends GroovyTestCase {
    
    def escapes = [
        '*': '\\2a',
        '(': '\\28',
        ')': '\\29',
        '\\': '\\5c',
        'NUL': '\\00',
        '/': '\\2f'
    ]
    
    void assertFilterEquals(expected, actual) {
        def filter = new FilterBuilder(actual).filter
        assert filter instanceof org.springframework.ldap.filter.Filter
        assertEquals filter.encode(), expected
    }
    
    void testEscaping() {
        assertFilterEquals("(a=${escapes['*']}b)") {
            eq("a","*b")
        }
        assertFilterEquals("(a=${escapes['(']}b)") {
            eq("a","(b")
        }        
        
    }
    
    void testSimpleEquals() {
        assertFilterEquals("(a=b)") {
            eq("a","b")
        }
    }
    
    void testSimpleAnd() {
        assertFilterEquals("(&(a=b)(c=d))") {
            and {
                eq "a","b"
                eq "c", "d"
            }
        }
    }
    
    void testSimpleOr() {
        assertFilterEquals("(|(a=b)(c=d))") {
            or {
                eq "a","b"
                eq "c", "d"
            }
        }
    }
    
    void testSimpleNot() {
        assertFilterEquals("(!(a=b))") {
            not {
                eq("a","b")
            }
        }        
    }
    
    void testCompoundNot() {
        assertFilterEquals("(!(&(a=b)(c=d)))") {
            not {
                eq("a","b")
                eq("c", "d")
            }
        }        
    }
    
    void testComplexFilter1() {
        assertFilterEquals("(&(a=b)(c=d)(|(e=f)(!(g=h))))") {
            and {
                eq "a","b"
                eq "c", "d"
                or {
                    eq "e", "f"
                    not {
                        eq "g", "h" 
                    }
                }
            }
        }        
    }
    
}