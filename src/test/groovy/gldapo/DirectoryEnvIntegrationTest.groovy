package gldapo

/**
 * This has to be an integration test because we can only test the setting of the
 * environment properties by actually connecting to an LDAP server.
 */
class DirectoryEnvIntegrationTest extends AbstractGldapoIntegrationTest {

    def createConfig() {
        def m = super.createConfig()
        m.directories.local.env = ['java.naming.ldap.attributes.binary': 'b1 b2']
        m
    }
    
    void testBinaryAttributes() {
        def env = gldapo.directories.local.template.contextSource.readWriteContext.environment
        assertEquals("b1 b2", env['java.naming.ldap.attributes.binary'])
    }
    
}