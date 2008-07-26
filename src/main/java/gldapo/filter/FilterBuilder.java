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
import groovy.lang.GroovyObjectSupport;
import org.springframework.ldap.filter.Filter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A groovy builder for search filters.
 * 
 * For usage examples, see the 
 * <a href="http://svn.gldapo.codehaus.org/browse/gldapo/trunk/src/test/groovy/gldapo/filter/FilterBuilderTest.groovy?r=HEAD">test cases</a>.
 * 
 * @since 0.7
 * @author Siegfried Puchbauer
 * @author Luke Daley
 */
public class FilterBuilder extends GroovyObjectSupport {

    protected Stack<List<Filter>> stack = new Stack<List<Filter>>();

    protected Filter result;

    private Log log = LogFactory.getLog(FilterBuilder.class);

    public FilterBuilder(Closure closure) {
        push("root");
        call(closure);
        result = FilterUtil.and(pop("root"));
    }

    public void push(String debugLabel) {
        log.debug("Entering " + debugLabel);
        stack.push(new ArrayList<Filter>());
    }

    public List<Filter> pop(String debugLabel) {
        log.debug("Leafing " + debugLabel);
        return stack.pop();
    }

    public void call(Closure c) {
        c.setDelegate(this);
        c.call();
    }

    private Integer add(Filter f) {
        log.debug("Adding Filter to current result: " + DefaultGroovyMethods.inspect(f));
        stack.peek().add(f);
        return stack.peek().size() - 1;
    }

    public Object invokeMethod(String s, Object o) {
        if(o instanceof Object[]) {
            return invokeMethod(s, (Object[])o);
        } else if(o == null) {
            return invokeMethod(s, new Object[0]);
        } else {
            return invokeMethod(s, new Object[]{ o });
        }
    }

    public Object invokeMethod(String name, Object[] args) {
        if(log.isDebugEnabled()) log.debug("["+hashCode()+"Invoked " + name + " (" + DefaultGroovyMethods.inspect(args) + ")");
        if ("and".equals(name)) {
            if (args.length == 1 && args[0] instanceof Closure) {
                Closure c = (Closure) args[0];
                push("and");
                call(c);
                return add(FilterUtil.and(pop("and")));
            } else {
                throw new IllegalArgumentException("FilterBuilder 'and' call is invalid!");
            }
        } else if ("or".equals(name)) {
            if (args.length == 1 && args[0] instanceof Closure) {
                Closure c = (Closure) args[0];
                push("and");
                call(c);
                return add(FilterUtil.or(pop("and")));
            } else {
                throw new IllegalArgumentException("FilterBuilder 'or' call is invalid!");
            }
        } else if ("eq".equals(name)) {
            if (args.length == 2 && args[0] instanceof String && args[1] instanceof String) {
                return add(FilterUtil.eq((String) args[0], (String) args[1]));
            } else {
                throw new IllegalArgumentException("FilterBuilder 'eq' call must have two arguments of type String");
            }
        } else if ("like".equals(name)) {
            if (args.length == 2 && args[0] instanceof String && args[1] instanceof String) {
                return add(FilterUtil.like((String) args[0], (String) args[1]));
            } else {
                throw new IllegalArgumentException("FilterBuilder 'like' call must have two arguments of type String");
            }
        } else if ("gte".equals(name)) {
            if (args.length == 2 && args[0] instanceof String && args[1] instanceof String) {
                return add(FilterUtil.gte((String) args[0], (String) args[1]));
            } else {
                throw new IllegalArgumentException("FilterBuilder 'gte' call must have two arguments of type String");
            }
        } else if ("lte".equals(name)) {
            if (args.length == 2 && args[0] instanceof String && args[1] instanceof String) {
                return add(FilterUtil.lte((String) args[0], (String) args[1]));
            } else {
                throw new IllegalArgumentException("FilterBuilder 'lte' call must have two arguments of type String");
            }
        } else if ("not".equals(name)) {
            if (args.length == 1 && args[0] instanceof Closure) {
                push("not");
                call((Closure) args[0]);
                return add(FilterUtil.not(FilterUtil.and(pop("not"))));
            } else if (args.length == 1 && args[0] instanceof Integer) {
                int idx = (Integer) args[0];
                stack.peek().set(idx, FilterUtil.not(stack.peek().get(idx)));
                return idx;
            } else {
                throw new IllegalArgumentException("FilterBuilder 'not' call is invalid!");
            }
        } else
            throw new IllegalAccessError("FilterBuilder invalid call: " + name);
    }

    public Filter getFilter() {
        return result;
    }
}