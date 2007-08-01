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
import gldapo.schema.gynamo.GldapoSchemaMetaGynamo;

class GldapoSchemaRegistry 
{
	static final CONFIG_SCHEMAS_KEY = 'schemas'
	
	List schemas
	
	List getSchemas()
	{
		if (this.schemas == null) this.schemas = []
		return this.schemas
	}
	
	void setSchemas(List<Class> schemas)
	{
		this.schemas = []
		schemas.each { this << it }
	}
	
	void leftShift(Class schema)
	{
		use (Gynamo) {
			schema.gynamize(GldapoSchemaMetaGynamo)
		}

		this.getSchemas() << schema
	}
	
	static newFromConfig(ConfigObject config)
	{
		def registry = this.newInstance()
		if (config.containsKey(CONFIG_SCHEMAS_KEY))
		{
			config[CONFIG_SCHEMAS_KEY].each { registry << it }
		}

		return registry
	}
}