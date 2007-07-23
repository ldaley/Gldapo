package gldapo.schema.provided;
import gldapo.schema.annotations.*

@GldapoSchemaFilter("(objectclass=person)")
@GldapoIdentifyingAttribute("samaccountname")
class ActiveDirectoryPerson 
{
	List objectclass
	String sn
	String samaccountname
	String givenName
}