package gldapwrap.aspect;

class IdentifierAspect 
{
	static public void inject(Class clazz)
	{
		try 
		{
			def filter = clazz.getIdentifier() // Testing to see if it is there
		}
		catch(MissingMethodException e) 
		{
			clazz.metaClass."static".getIdentifier << { null }
		}
	}
}