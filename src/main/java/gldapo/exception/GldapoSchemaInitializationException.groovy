package gldapo.exception;

public class GldapoSchemaInitializationException extends GldapoException
{
	GldapoSchemaInitializationException(Class schema, Exception cause)
	{
		super("Error initialising schema class {$schema.name}", cause)
	}
}
