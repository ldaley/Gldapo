package gldapo;
import org.springframework.ldap.core.support.LdapContextSource

/**
 * @todo Tests needed
 */
class GldapoTemplateTest extends GroovyTestCase 
{
	void testNewFromConfig() 
	{
		def c = new ConfigObject()
		
		c.contextSource.url = "ldap://example.com"
		c.contextSource.base = "ou=example,ou=com"
		c.countextSource.userDn = "cn=user"
		c.contextSource.password = "password"
		
		c.searchControls.countLimit = 50
		c.base = "ou=people"
		
		def t = GldapoTemplate.newFromConfig("testTemplate", c)
		assertNotNull(t)
		assertEquals(true, t instanceof GldapoTemplate)
		assertEquals(50, t.searchControls.countLimit)
		assertEquals("ou=people", t.base)
		assertEquals("testTemplate", t.beanName)
	}
}