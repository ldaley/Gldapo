package gldapo.schema.gynamo;
import gynamo.Gynamo
import gldapo.schema.attribute.typeconversion.GldapoTypeConversions
import gldapo.exception.GldapoTypeConversionException
import gldapo.exception.GldapoNoTypeConversionAvailableException
import org.apache.commons.lang.WordUtils
import javax.naming.directory.Attribute

class TypeConversionGynamo extends Gynamo
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