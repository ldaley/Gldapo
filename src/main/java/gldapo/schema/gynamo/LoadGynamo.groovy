package gldapo.gynamo;
import gldapo.exception.GldapwrapException
import org.springframework.ldap.filter.EqualsFilter
import gynamo.Gynamo
import gynamo.GynamoDependencies

@GynamoDependencies([IdentifierGynamo, FindGynamo])
class LoadGynamo extends Gynamo
{
	static load = { String identifyingValue ->
		def identifier = clazz.getIdentifier()
		if (identifier == null)
		{
			throw new GldapwrapException(clazz, "Cannot use load() if no identifier has been set")
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