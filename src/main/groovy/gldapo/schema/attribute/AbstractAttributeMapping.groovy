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
package gldapo.schema.attribute
import org.apache.commons.lang.WordUtils
import java.lang.reflect.Field
import gldapo.exception.GldapoTypeMappingException
import gldapo.exception.GldapoException
import gldapo.schema.annotation.GldapoSynonymFor
import gldapo.schema.annotation.GldapoPseudoType
import gldapo.Gldapo

/**
 * Represents the bridging between the data of the LDAP world and the Groovy world
 * 
 * property - Groovy side
 * attribute - LDAP side
 */
abstract class AbstractAttributeMapping
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
	AbstractAttributeMapping(Class schema, Field field)
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
			return this.calculateTypeMappingFromFieldType()
		}
	}
	
	/**
	 * @todo Need some caching here
	 */	
	protected calculateToFieldTypeMapper()
	{	
		Class[] p = [String] as Class[]
		
		def byFieldMapperName = toFieldByFieldMapperName(this.field.name)
		def classByFieldMapper = schema.metaClass.getMetaMethod(byFieldMapperName, p)
		if (classByFieldMapper) return { classByFieldMapper.invoke(schema, it) }
		
		def byTypeMapperName = toFieldByTypeMapperName(this.typeMapping)
		def classByTypeMapper = schema.metaClass.getMetaMethod(byTypeMapperName, p)
		if (classByTypeMapper) return { classByTypeMapper.invoke(schema, it) }
		
		def defaultByTypeMapper = Gldapo.instance.typemappings.getToFieldMapperForType(this.typeMapping)
		if (defaultByTypeMapper) return defaultByTypeMapper

		throw new GldapoTypeMappingException(this.schema, this.field.name, this.typeMapping, GldapoTypeMappingException.MAPPING_TO_FIELD, "No available type mapping")
		
	}
	
	def mapFromContext(context, subject)
	{
		try
		{
			subject."${this.field.name}" = this.getFieldValue(context)
		} 
		catch (Exception cause)
		{
			throw new GldapoTypeMappingException(this.schema, this.field.name, this.typeMapping, GldapoTypeMappingException.MAPPING_TO_FIELD, cause)
		}
	}
	
	static toFieldByFieldMapperName(String fieldName)
	{
		"mapTo" + WordUtils.capitalize(fieldName) + "Field"
	}
	
	static toFieldByTypeMapperName(String typeName)
	{
		"mapTo" + WordUtils.capitalize(typeName) + "Type"
	}
	
	abstract protected calculateTypeMappingFromFieldType()
	
	abstract protected getFieldValue(Object context)
}