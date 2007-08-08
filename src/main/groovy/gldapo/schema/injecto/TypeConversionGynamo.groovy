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
import gldapo.schema.attribute.typeconversion.GldapoTypeConversions
import gldapo.exception.GldapoTypeConversionException
import gldapo.exception.GldapoNoTypeConversionAvailableException
import org.apache.commons.lang.WordUtils
import javax.naming.directory.Attribute

class TypeConversionGynamo 
{
	static final CONVERT_TO = "convertTo"
	
	static convertLdapAttributeToGroovy = { Class groovyType, String attributeName, Attribute attributeValue ->
		if (attributeValue == null) return null

		try
		{
			def convertedValue = delegate.tryToGroovyAttributeConversion(attributeName, attributeValue)
			if (convertedValue == null)
			{
				convertedValue = delegate.tryToGroovyTypeConversion(groovyType, attributeValue)
				if (convertedValue == null) throw new GldapoNoTypeConversionAvailableException()
			}
			
			return convertedValue
		}
		catch (Exception cause)
		{
			throw new GldapoTypeConversionException(delegate, attributeName, attributeValue.class, groovyType, cause)
		}
	}
	
	static tryToGroovyAttributeConversion = { 
		String attributeName, Attribute attributeValue ->

		String methodName = TypeConversionGynamo.CONVERT_TO + WordUtils.capitalize(attributeName) + "Attribute"
		
		try
		{
			return delegate."${methodName}"(attributeValue)
		}
		catch (MissingMethodException e)
		{
			return null
		}
	}
	
	static tryToGroovyTypeConversion = {
		Class groovyType, Attribute attributeValue ->

		String methodName = TypeConversionGynamo.CONVERT_TO + WordUtils.capitalize(groovyType.simpleName) + "Type"

		// Try local first
		try
		{
			Object convertedValue = delegate."${methodName}"(attributeValue)
			if (convertedValue != null) return convertedValue
		}
		catch (MissingMethodException e)
		{
		}
		
		// Try global conversions
		try
		{
			return GldapoTypeConversions."${methodName}"(attributeValue)
		}
		catch (MissingMethodException e)
		{
			return null
		}
	}
}