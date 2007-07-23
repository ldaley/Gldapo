import gldapo.GldapwrapTemplate
import gldapo.GldapwrapInjector
import gldapo.schema.Person
import javax.naming.directory.SearchControls

class IntegrationTests extends GroovyTestCase {
	
	IntegrationTests()
	{
		def ldapTemplate = new GldapwrapTemplate(
			url: "ldap://directory.washington.edu",
			base: "o=University of Washington,c=US"
		)

		GldapwrapInjector.inject(Person, ldapTemplate)
	}
	
	void testFind() 
	{
		assertEquals(2, Person.find(
			base: "ou=Faculty and Staff,ou=People", 
			searchScope: SearchControls.SUBTREE_SCOPE,
			countLimit: 2 // Only get two so we don't hit their server hard
		).size())
	}
}