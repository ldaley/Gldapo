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
package gldapo.schema.gynamo;
import gldapo.exception.GldapoException
import gldapo.schema.annotation.GldapoSchemaFilter
import gynamo.*

class SchemaFilterTest extends GroovyTestCase 
{
	SchemaFilterTest()
	{
		Gynamo.gynamize(HasFilterSchema, SchemaFilterGynamo)
		Gynamo.gynamize(NoFilterSchema, SchemaFilterGynamo)
	}
	void testHasFilter() 
	{		
		assertEquals("(objectclass=person)", HasFilterSchema.getSchemaFilter())
	}

	void testNoFilter()
	{
		assertEquals(SchemaFilterGynamo.NO_FILTER_FILTER, NoFilterSchema.getSchemaFilter())
	}
	
	void testFilterAnding()
	{
		assertEquals("(objectclass=person)", HasFilterSchema.andSchemaFilterWithFilter(null))
		assertEquals("(&(objectclass=person)(a=b))", HasFilterSchema.andSchemaFilterWithFilter("(a=b)"))
		
		assertEquals(SchemaFilterGynamo.NO_FILTER_FILTER, NoFilterSchema.andSchemaFilterWithFilter(null))
		assertEquals("(a=b)", NoFilterSchema.andSchemaFilterWithFilter("(a=b)"))
	}
}

@GldapoSchemaFilter("(objectclass=person)")
class HasFilterSchema
{
	
}

class NoFilterSchema
{
	
}