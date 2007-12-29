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

/**
 * The schema registry holds {@link GldapoSchemaRegistration} objects that hold the necessary
 * meta data that Gldapo needs to do all the LDAP magic with schema classes.
 * <p>
 * You can add actual schema classes or registrations ...
 * <p>
 * {@code schemaRegistry << MySchemaClass}
 * <p>
 * or ...
 * <p>
 * {@code schemaRegistry << new GldapoSchemaRegistration(MySchemaClass)}
 * <p>
 * Both will have the same effect, but the first way is preffered (the registration object will be created implicitly)
 * 
 * @see #add(Object)
 */
class GldapoSchemaRegistry extends LinkedList<GldapoSchemaRegistration> {

	/**
	 * Registers a new schema class.
	 * <p>
	 * If {@code registration} is a {@link java.lang.Class}  (implicitly creating a {@link GldapoSchemaRegistration} for it), 
	 * or just adding {@code registration} if it is a schema registration. 
	 * <p>
	 * If the schema class in question is already registered, this will silently do nothing.
	 * 
	 * @param registration Either the schema class, or a {@link GldapoSchemaRegistration registration} for a schema class
	 * @throws IllegalArgumentException if {@code registration} is not a Class or GldapoSchemaRegistration object
	 */
	boolean add(registration) {
		if (registration instanceof GldapoSchemaRegistration) {
			if (this.isRegistered(registration.schema) == false) super.add(registration)
		} else if (registration instanceof Class) {
			if (this.isRegistered(registration) == false) super.add(new GldapoSchemaRegistration(registration))
		} else {
			throw new IllegalArgumentException("Only Class objects or GldapoSchemaRegistration objects can be added to the GldapoSchemaRegistry")
		}		
	}
	
	/**
	 * Test to see if a particular schema class is already registered.
	 * 
	 * @param schema the Class in question
	 * @return whether it is registered or not
	 */
	boolean isRegistered(Class schema) {
		this.find { it.schema == schema } != null
	}
	
	/**
	 * Allows fetching of the registration of a schema class, by the schema class
	 * 
	 * @param schema the schema class in question
	 * @return A {@link GldapoSchemaRegistration} object for the schema class, or null if that class is not registered
	 */
	def getAt(Class schema) {
		return this.find { it.schema == schema }
	}
}