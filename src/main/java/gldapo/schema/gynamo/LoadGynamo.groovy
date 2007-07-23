package gldapo.schema.gynamo;
import gldapo.exception.GldapoException
import org.springframework.ldap.filter.EqualsFilter
import gynamo.Gynamo
import gynamo.GynamoDependencies

@GynamoDependencies([IdentifyingAttributeGynamo, FindGynamo])
class LoadGynamo extends Gynamo
{
	static load = { String identifyingValue ->
		def identifier = delegate.getIdentifyingAttribute()
		if (identifier == null)
		{
			throw new GldapoException("Cannot use load() on ${delegate.name} as it has no GldapoIdentifyingAttribute annotation")
		}
		
		def filter = new EqualsFilter(identifier, identifyingValue)
		def matches = delegate.find(filter: filter.encode(), pageSize: 1)
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