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
import gldapo.schema.GldapoSchemaRegistration;

class GldapoSchemaRegistry extends LinkedList<GldapoSchemaRegistration>
{
	static final CONFIG_SCHEMAS_KEY = 'schemas'

	/**
	 * @todo To support inheritance, this needs to register all subclasses
	 */
	boolean add(s) {
		if (s instanceof GldapoSchemaRegistration) {
			if (this.isRegistered(s.schema) == false) super.add(s)
		} else if (s instanceof Class) {
			if (this.isRegistered(s) == false) super.add(new GldapoSchemaRegistration(s))
		} else {
			throw new IllegalArgumentException("Only Class objects or GldapoSchemaRegistration objects can be added to the GldapoSchemaRegistry")
		}		
	}
	
	boolean isRegistered(schema) {
		this.find { it.schema == schema } != null
	}
	
	def getAt(Class schema)
	{
		return this.find { it.schema == schema }
	}
	
	static newInstance(Map config)
	{
		def registry = new GldapoSchemaRegistry()
		if (config.containsKey(CONFIG_SCHEMAS_KEY))
		{
			config[CONFIG_SCHEMAS_KEY].each { registry << it }
		}

		return registry
	}
}