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
import java.lang.reflect.*
import gldapo.exception.*
import gldapo.schema.annotation.GldapoSynonymFor
import gldapo.util.FieldInspector



/**
 * Represents the bridging between the data of the LDAP world and the Groovy world
 * 
 * property - Groovy side
 * attribute - LDAP side
 */
class AttributeMapping
{
	/**
	 * 
	 */
	static defaultCollectionElementType = String
	
	static collectionTypeMap = [
		(List): LinkedList,
		(Set): LinkedHashSet,
		(SortedSet): TreeSet
	]
	
	/**
	 * The name of the variable on the schema class
	 */
	String propertyName
	
	/**
	 * The name on the LDAP side
	 */
	String attributeName
	
	/**
	 * 
	 */
	Class schemaClass
	
	/**
	 * The type of a single attribute value
	 */
	Class propertyType
	
	/**
	 * 
	 */
	String typeNameForConversion
	
	/**
	 * Multivalue attributes are declared by List<T>, Set<T> or SortedSet<T>
	 */
	Class collectionType

	/**
	 * 
	 */
	AttributeMapping(Class schemaClass, Field property)
	{
		def synonymAnnotation = property.getAnnotation(GldapoSynonymFor)
		this.attributeName = (synonymAnnotation) ? synonymAnnotation.value() : property.name
		
		def realPropertyType = property.genericType
		
		if (Collection.isAssignableFrom(realPropertyType))
		{
			def abstractCollectionType
			
			if (realPropertyType instanceof ParameterizedType)
			{
				abstractCollectionType = realPropertyType.rawType
				this.propertyType = realPropertyType.actualTypeArguments[0]
			}
			else
			{
				abstractCollectionType = realPropertyType
				this.propertyType = defaultCollectionElementType
			}
			
			this.collectionType = collectionTypeMap[abstractCollectionType]
			if (this.collectionType == null)
			{
				// TODO Throw exception here, illegal collection type
			}
			
		}
		else
		{
			this.propertyType = realPropertyType.simpleName
		}
		
		def pseudoTypeAnnotation = property.getAnnotation(GldapoPseudoType)
		if (pseudoTypeAnnotation)
		{
			this.typeNameForConversion = pseudoTypeAnnotation.value()
		}
		else
		{
			this.typeNameForConversion = this.propertyType.simpleName
		}
	}
	
	def convertAttributeToProperty(String[] attributeValues)
	{
		if (attribute == null) return null
		 
		if (this.collectionType)
		{
			def collection = this.collectionType.newInstance()
			attributeValues.each {
				collection << this.convertSingleAttributeValueToProperty(it)
			}
			return collection
		}
		else
		{
			if (attribute.isEmpty()) return null
			return this.convertSingleAttributeValueToProperty(attributeValues[0])
		}
	}
	
	/**
	 * 
	 */
	def convertSingleAttributeValueToProperty(String attribute)
	{
		if (attribute == null) return null
		
		def byPropertyConverter = this.getAttributeToPropertyByPropertyConverterMethodName()
		def byTypeConverter = this.getAttributeToPropertyByTypeConverterMethodName()
		
		try
		{	
			// Try attribute conversion
			try
			{
				return schemaClass."$byPropertyConverter"(attribute)
			}
			catch (MissingMethodException e)
			{
			}
			
			// Try type conversion on the schema class
			try
			{
				return schemaClass."$byTypeConverter"(attribute)
			}
			catch (MissingMethodException e)
			{
			}

			// Try global conversions
			try
			{
				return DefaultTypeConversions."${byTypeConverter}"(attribute)
			}
			catch (MissingMethodException e)
			{
				throw new GldapoNoTypeConversionAvailableException()
			}
		}
		catch (Exception cause)
		{
			throw new GldapoTypeConversionException(schemaClass, propertyName, attribute.class, typeNameForConversion, cause)
		}
	}
	
	/**
	 * 
	 */
	def getAttributeToPropertyByPropertyConverterMethodName()
	{
		"convertTo" + WordUtils.capitalize(this.propertyName) + "Property"
	}
	
	/**
	 * 
	 */
	def getAttributeToPropertyByTypeConverterMethodName()
	{
		"convertTo" + WordUtils.capitalize(this.typeNameForConversion) + "Type"
	}
	
	/**
	 * 
	 */
	static List<AttributeMapping> allFor(Class schemaClass)
	{
		def mappings = []
		
		use (FieldInspector) {
			targetClass.declaredFields.each {
				if (schemaClass.fieldIsReadableAndWritable(it)) mappings << new AttributeMapping(schemaClass, it)
			}
		}
		
		return mappings
	}
}