package gldapwrap.aspect;
import gldapwrap.exception.GldapwrapException
import org.springframework.ldap.filter.EqualsFilter

class LoadAspect 
{
	static public void inject(Class clazz)
	{
		clazz.metaClass."static".load << { String identifyingValue ->
			def identifier = clazz.getIdentifier()
			if (identifier == null)
			{
				throw new GldapwrapException(clazz, "Cannot use get() if no identifier has been set")
			}
			
			def filter = new EqualsFilter(identifier, identifyingValue)
			def matches = clazz.find(filter: filter.encode())
			if (matches.size() == 0)
			{
				return null
			}
			else
			{
				return matches[0]
			}
		}
	}
}