package gldapwrap.schema;

class ActiveDirectoryPerson 
{
	static filter = "(objectclass=person)"
	static identifier = "samaccountname"
	
	List objectclass
	String sn
	String samaccountname
	String givenName
}