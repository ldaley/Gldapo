package gldapo.exception;

public class GldapwrapNoTemplateException extends GldapwrapException
{
	GldapwrapNoTemplateException(Class offender)
	{
		super(offender.getName() + " does not have an LdapTemplate set");
	}
}
