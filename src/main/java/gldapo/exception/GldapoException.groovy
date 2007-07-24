package gldapo.exception;

class GldapoException extends Exception
{
	GldapoException(String message)
	{
		super(message)
	}
	
	GldapoException(String message, Exception cause)
	{
		super(message, cause)
	}
}
