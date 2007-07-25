package gldapo.exception;

class GldapoNoDefaultTemplateException extends GldapoException
{
	GldapoNoDefaultTemplateException()
	{
		super("A request was made for the default template but none has been specified")
	}
}
