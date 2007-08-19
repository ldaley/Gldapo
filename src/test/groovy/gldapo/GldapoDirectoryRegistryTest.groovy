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
package gldapo;
import gldapo.exception.GldapoNoDefaultTemplateException
import gldapo.exception.GldapoException
import gldapo.template.GldapoTemplate
import gldapo.template.GldapoTemplateImpl

class GldapoTemplateRegistryTest extends GroovyTestCase 
{
	void testGetDefaultWhenIsNone() 
	{
		def registry = new GldapoTemplateRegistry()
		shouldFail (GldapoNoDefaultTemplateException) {
			def defaultTemplate = registry.defaultTemplate
		}
	}
	
	void testGetDefaultWhenDoesntExist() 
	{
		def registry = new GldapoTemplateRegistry()
		registry.defaultTemplateName = "abc"
		shouldFail (GldapoException) {
			def defaultTemplate = registry.defaultTemplate
		}
	}
	
	void testGetDefault()
	{
		def registry = new GldapoTemplateRegistry()
		def template = new GldapoTemplateImpl()
		template.beanName = "test"
		registry.defaultTemplateName = "test"
		registry << template
		assertSame(template, registry.defaultTemplate)
	}
	
	void testNewFromOkConfig()
	{
		def c = new ConfigObject()
		
		c.templates.t1.contextSource.url = "ldap://example.com"
		c.templates.t2.contextSource.url = "ldap://example2.com"
		c.defaultTemplate = "t1"
		
		def r = GldapoTemplateRegistry.newInstance(c)
		
		assert(r instanceof GldapoTemplateRegistry)
		assertEquals(2, r.templates.size())
		assertNotNull(r["t1"])
		assertNotNull(r["t2"])
		assert(r["t1"] instanceof GldapoTemplate)
		assertSame(r.defaultTemplate, r["t1"])
	}
	
}