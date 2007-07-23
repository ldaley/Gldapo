package gldapo.schema.gynamo;
import gynamo.Gynamo
import gldapo.schema.typeconversion.GldapoTypeConversions
import gldapo.exception.GldapoTypeConversionException
import gldapo.exception.GldapoNoAvailableTypeConversionException
import org.apache.commons.lang.WordUtils
import javax.naming.directory.Attribute

class TypeConversionGynamo extends Gynamo
{
	static coerceLdapAttributeToGroovy = { Class groovyType, String attributeName, Attribute attributeValue ->
		if (attributeValue == null) return null

		try
		{
			def convertedValue = TypeCoercionGynamo.tryToGroovyAttributeConversion(attributeName, attributeValue)
			if (convertedValue == null)
			{
				convertedValue = delegate.tryToGroovyTypeConversion(groovyType, attributeValue)
				if (convertedValue == null) throw new GldapoNoAvailableTypeConversionException()
			}
			
			return convertedValue
		}
		catch (Exception cause)
		{
			throw new GldapoTypeConversationException(delegate, attributeName, attributeValue.class, groovyType, cause)
		}
	}
	
	static tryToGroovyAttributeConversion = { 
		String attributeName, Attribute attributeValue ->

		String methodName = "coerceTo" + WordUtils.capitalize(attributeName) + "Attribute"
		
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

		String methodName = "coerceTo" + WordUtils.capitalize(groovyType.simpleName) + "Type"

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