package gldapwrap.schema;
class Person 
{
	static filter = "(objectclass=person)"
	static identifier = "uid"
	
	List objectclass
	String sn
	String distinguishedName
	String givenName
}