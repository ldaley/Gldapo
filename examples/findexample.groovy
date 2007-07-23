import gldapo.GldapwrapTemplate
import gldapo.GldapwrapInjector
import gldapo.schema.Person
import javax.naming.directory.SearchControls

def ldapTemplate = new GldapwrapTemplate(
	url: "ldap://directory.washington.edu",
	base: "o=University of Washington,c=US"
)

GldapwrapInjector.inject(Person, ldapTemplate)

Person.find(
	base: "ou=Faculty and Staff,ou=People", 
	searchScope: SearchControls.SUBTREE_SCOPE,
	countLimit: 50
).each {
	println "${it.givenName} ${it.sn} - ${it.objectclass}"
}