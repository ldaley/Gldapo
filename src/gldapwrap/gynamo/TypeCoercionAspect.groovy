package gldapwrap.aspect;
import gldapwrap.GldapwrapTypeCoercions
import gldapwrap.exception.GldapwrapTypeCoercionException
import gldapwrap.exception.GldapwrapNoAvailableTypeCoercionAvailableException
import org.apache.commons.lang.WordUtils
import javax.naming.directory.Attribute

class TypeCoercionAspect
{
	static public void inject(Class clazz)
	{
		clazz.metaClass."static".coerceLdapToNative << { Class type, String attributeName, Attribute attributeValue ->
			if (attributeValue == null)
			{
				return null
			}
			
			try
			{
				Object coercedValue = null
				
				coercedValue = TypeCoercionAspect.tryAttributeSpecificCoercion(clazz, type, attributeName, attributeValue)
				
				if (coercedValue == null)
				{
					coercedValue = TypeCoercionAspect.tryGenericCoercion(clazz, type, attributeValue)
					
					if (coercedValue == null)
					{
						throw new GldapwrapNoAvailableTypeCoercionAvailableException()
					}	
				}
				
				return coercedValue
			}
			catch (Exception cause)
			{
				throw new GldapwrapTypeCoercionException(clazz, attributeName, attributeValue.class, type, cause)
			}
		}
	}
	
	static public Object tryAttributeSpecificCoercion(Class injectee, Class type, String attributeName, Attribute attributeValue)
	{
		String methodName = "coerceTo" + WordUtils.capitalize(attributeName) + "Attribute"
		
		try
		{
			return injectee."${methodName}"(attributeValue)
		}
		catch (MissingMethodException e)
		{
			return null
		}
	}
	
	static public Object tryGenericCoercion(Class injectee, Class type, Attribute attributeValue)
	{
		String methodName = "coerceTo" + WordUtils.capitalize(type.simpleName) + "Type"

		// Try local first
		try
		{
			Object coercedValue = injectee."${methodName}"(attributeValue)
			if (coercedValue != null)
			{
				return coercedValue
			}
		}
		catch (MissingMethodException e)
		{
		}
		
		// Try global coercions
		try
		{
			return GldapwrapTypeCoercions."${methodName}"(attributeValue)
		}
		catch (MissingMethodException e)
		{
			return null
		}
	}
}