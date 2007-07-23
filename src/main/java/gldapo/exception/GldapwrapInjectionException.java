package gldapo.exception;

public class GldapwrapInjectionException extends GldapwrapException
{
	GldapwrapInjectionException(Class clazz, String message)
	{
		super(clazz + " - " + message);
	}
}
