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
package gldapo.filter;

import groovy.lang.Closure;
import org.springframework.ldap.filter.*;

import java.util.List;

/**
 * Utility class for the {@link FilterBuilder filter builder}.
 * <p>
 * Example:
 * <p>
 * <pre>
 * def filter = FilterUtil.build {
 *     and {
 *         or { like "cn", "foo*" like "cn", "bar*" }
 *         eq "groupMembership", "cn=admingroup,ou=system,o=mycompany"
 *     }
 * }
 * assert filter instanceof org.springframework.ldap.filter.Filter
 * assert filter.encode() == "(&(|(cn=foo*)(cn=bar*))(groupMembership=cn=admingroup,ou=system,o=mycompany))"
 * </pre>
 * <p>
 * see the 
 * <a href="http://svn.gldapo.codehaus.org/browse/gldapo/trunk/src/test/groovy/gldapo/filter/FilterBuilderTest.groovy?r=HEAD">builder test cases</a>
 * for more complex cases.
 * 
 * @since 0.7
 * @author Siegfried Puchbauer
 * @author Luke Daley
 */
public class FilterUtil {

    public static Filter build(Closure closure) {
        return new FilterBuilder(closure).getFilter();
    }

    public static Filter or(List<Filter> filters) {
        if (filters.size() == 1) return filters.get(0);
        OrFilter currentFilter = new OrFilter();
        for (Filter filter : filters) {
            currentFilter.or(filter);
        }
        return currentFilter;
    }

    public static Filter and(List<Filter> filters) {
        if (filters.size() == 1) return filters.get(0);
        AndFilter currentFilter = new AndFilter();
        for (Filter filter : filters) {
            currentFilter.and(filter);
        }
        return currentFilter;
    }

    public static Filter and(Filter... filter) {
        if (filter.length == 1) return filter[0];
        AndFilter result = new AndFilter();
        for (int i = 0; i < filter.length; i++) {
            result.and(filter[i]);
        }
        return result;
    }

    public static Filter or(Filter... filter) {
        if (filter.length == 1) return filter[0];
        OrFilter result = new OrFilter();
        for (int i = 0; i < filter.length; i++) {
            result.or(filter[i]);
        }
        return result;
    }

    public static Filter eq(String field, String value) {
        return new EqualsFilter(field, value);
    }

    public static Filter eq(String field, int value) {
        return new EqualsFilter(field, value);
    }

    public static Filter gte(String field, String value) {
        return new GreaterThanOrEqualsFilter(field, value);
    }

    public static Filter gte(String field, int value) {
        return new GreaterThanOrEqualsFilter(field, value);
    }

    public static Filter lte(String field, String value) {
        return new LessThanOrEqualsFilter(field, value);
    }

    public static Filter lte(String field, int value) {
        return new LessThanOrEqualsFilter(field, value);
    }

    public static Filter like(String field, String value) {
        return new LikeFilter(field, value);
    }

    public static Filter not(Filter filter) {
        return new NotFilter(filter);
    }
}
