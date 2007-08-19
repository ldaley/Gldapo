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
package gldapo.schema.attribute;
import org.apache.commons.lang.WordUtils
import java.lang.reflect.Field
import gldapo.exception.GldapoTypeMappingException
import gldapo.schema.annotation.GldapoSynonymFor

/**
 * Represents the bridging between the data of the LDAP world and the Groovy world
 * 
 * property - Groovy side
 * attribute - LDAP side
 */
abstract class AttributeMapping
{
	
	/**
	 * 
	 */
	Class schema
	
	/**
	 * 
	 */
	Field field
	
	/**
	 * The name on the LDAP side
	 */
	String attributeName

	/**
	 * pseudo property type
	 */
	String typeMapping
	
	/**
	 * 
	 */
	Closure toFieldTypeMapper

	/**
	 * 
	 */
	AttributeMapping(Class schema, Field field)
	{
		this.schema = schema
		this.field = field
		
		this.attributeName = this.calculateAttributeName()
		this.typeMapping = this.calculateTypeMapping()
		this.toFieldTypeMapper = this.calculateToFieldTypeMapper()
	}
	
	protected calculateAttributeName()
	{
		def synonymAnnotation = this.field.getAnnotation(GldapoSynonymFor)
		(synonymAnnotation) ? synonymAnnotation.value() : this.field.name
	}
	
	protected calculateTypeMapping()
	{
		def pseudoTypeAnnotation = this.field.getAnnotation(GldapoPseudoType)
		if (pseudoTypeAnnotation)
		{
			return pseudoTypeAnnotation.value()
		}
		else
		{
			return this.calculateTypeMappingFromUnderlyingType()
		}
	}
		
	protected calculateToFieldTypeMapper()
	{	
		Class[] p = [String] as Class[]
		
		def byFieldMapper = "mapTo" + WordUtils.capitalize(this.field.name) + "Field"
		
		def classByFieldMethod = schema.metaClass.getMetaMethod(byFieldMapper, p)
		if (classByFieldMethod) return { classByFieldMethod.invoke(schema, it) }
		
		def byTypeMapper = "mapTo" + WordUtils.capitalize(this.typeMapping) + "Type"

		def classByTypeMethod = schema.metaClass.getMetaMethod(byTypeMapper, p)
		if (classByTypeMethod) return { classByTypeMethod.invoke(schema, it) }
		
		def defaultByTypeMethod = Gldapo.instance.typeMappingRegistry.toFieldMappingforType(byTypeMapper)
		if (defaultByTypeMethod) return defaultByTypeMethod

		throw new GldapoTypeMappingException(schema, this.field.name, this.typeMapping, MAP_TO_FIELD, "No available type mapping")
	}
	
	def toField(String[] attributeValues)
	{
		try
		{
			this.doToFieldMapping(attributeValues)
		} 
		catch (Exception cause)
		{
			throw new GldapoTypeMappingException(schema, this.field.name, this.typeMapping, MAP_TO_FIELD, cause)
		}
	}
	
	abstract protected calculateTypeMappingFromFieldType()
	
	abstract protected doToFieldMapping(String[] attributeValues)
}