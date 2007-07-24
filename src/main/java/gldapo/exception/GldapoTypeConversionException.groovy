package gldapo.exception;

class GldapoTypeConversionException extends GldapoException
{
	GldapoTypeConversionException(Class schema, String attributeName, Class from, Class to, Throwable cause) 
	{
		super("Exception in ${schema.name} converting ${attributeName} from ${from.name} to ${to.name}".toString(), cause)
	}
}
