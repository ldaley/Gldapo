package gldapo.schema.provided;
import gldapo.schema.annotations.*

@GldapoSchemaFilter("(objectclass=person)")
@GldapoIdentifyingAttribute("uid")
class Person 
{	
	List objectclass
	String sn
	String distinguishedName
	String givenName
}