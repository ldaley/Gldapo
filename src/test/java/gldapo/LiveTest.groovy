package gldapo;
import gldapo.schema.provided.Person
import javax.naming.directory.SearchControls

class LiveTest extends GroovyTestCase 
{
	
	LiveTest()
	{
		Gldapo.initialize(this.class.getClassLoader().findResource("washington-edu-conf.groovy"))
	}
	
	void testFind() 
	{
		def people = Person.find(
			base: "ou=Faculty and Staff,ou=People", 
			searchScope: SearchControls.SUBTREE_SCOPE,
			countLimit: 2 // Only get two so we don't hit their server hard
		)
		
		println people[0].objectclass
		assertEquals(2, people.size())
	}
}