package gldapo.exception;

public class GldapwrapTypeCoercionException extends GldapwrapException
{
	GldapwrapTypeCoercionException(Class injectee, String attributeName, Class from, Class to, Throwable cause) 
	{
		super("Error in " + injectee.getName() + " converting " + attributeName + " from " + from.getName() + " to " + to.getName(), cause);
	}
	
	public String getMessage()
	{
		String m = super.getMessage();
		return m + " (Cause - " + getCause().toString() + ")";
	}
}
