package gldapo.exception;

public class GldapwrapException extends Exception
{
	GldapwrapException(Class injectee, String message)
	{
		super(injectee.getName() + " " + message);
	}
	
	GldapwrapException(String message)
	{
		super(message);
	}
	
	GldapwrapException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
