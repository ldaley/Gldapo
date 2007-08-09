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


/*class T
{
	List<Integer> l
}

T.getDeclaredField("l").genericType
*//**
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
	 * Schema classes can declare a psuedo type like 'ActiveDirectoryDate' for conversion
	 */
	String pseudoType
	
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
		def pseudoTypeAnnotation = property.getAnnotation(GldapoPseudoType)

		
		this.attributeName = (synonymAnnotation) ? synonymAnnotation.value() : property.name
		
		if (pseudoTypeAnnotation) this.pseudoType = pseudoTypeAnnotation.value()
		
		def realPropertyType = property.genericType
		
		if (Collection.isAssignableFrom(realPropertyType))
		{
			if (realPropertyType instanceof ParameterizedType)
			{
				this.collectionType = realPropertyType.rawType
				this.propertyType = realPropertyType.actualTypeArguments[0]
			}
			else
			{
				this.collectionType = realPropertyType
				this.propertyType = defaultCollectionElementType
			}
			
			if (collectionTypeMap.keys.contains(this.collectionType) == false)
			{
				// Throw exception here, illegal collection type
			}
		}
		else
		{
			this.propertyType = realPropertyType
		}
	}
	
	/**
	 * 
	 */
	def convertAttributeToProperty(List attributes)
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
	
	String getTypeNameForConversion()
	{
		if (typeNameForConversion == null)
		{
			typeNameForConversion = (conversionPsuedoType) ? conversionPsuedoType : propertyType.simpleName
		}
		return typeNameForConversion
	}
	
	def getAttributeToPropertyByPropertyConverterMethodName()
	{
		"convertTo" + WordUtils.capitalize(propertyName) + "Property"
	}
	
	def getAttributeToPropertyByTypeConverterMethodName()
	{
		"convertTo" + WordUtils.capitalize(getTypeNameForConversion()) + "Type"
	}
	
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