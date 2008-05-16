package gldapo
import gldapo.schema.annotation.GldapoSchemaFilter

public class AuthenticateIntegrationTest extends AbstractGldapoIntegrationTest
{
    def schemaClasses = [AuthenticateIntegrationTestPerson]

    void testAuthenticate() {
        def password = "password"
        
        importEntry(cn: "authenticate", """
            sn: authenticate
            objectclass: top
            objectclass: person
            userPassword: $password
        """)

        def e = AuthenticateIntegrationTestPerson.find(filter: "(objectclass=*)")
        assertFalse(e.authenticate(password + "not"))
        assertTrue(e.authenticate(password))
    }
}

@GldapoSchemaFilter("(objectclass=person)")
class AuthenticateIntegrationTestPerson {
    String sn
    String cn
}