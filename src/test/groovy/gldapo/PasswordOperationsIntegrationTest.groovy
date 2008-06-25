package gldapo
import gldapo.schema.annotation.GldapoNamingAttribute

public class PasswordOperationsIntegrationTest extends AbstractGldapoIntegrationTest
{
    def schemaClasses = [PasswordOperationsIntegrationTestPerson]

    def newPerson(name, password) {
        new PasswordOperationsIntegrationTestPerson(
            cn: name,
            rdn: "cn=${name}",
            userPassword: password
        )
    }

    void testCreateWithNoPassword() {
        def p = newPerson("nopassword", null)
        p.save()
    }

    void testCreateWithPassword() {
        def p = newPerson("withpassword", "testpass")
        p.save()

        assertTrue(p.authenticate("testpass"))
        assertFalse(p.authenticate("nottherightpass"))
    }

    void testUpdatePassword() {
        importEntry(cn: "updatepassword", """
            objectclass: top
            objectclass: iNetOrgPerson
            sn: password1
        """)

        def p = PasswordOperationsIntegrationTestPerson.find(filter: "cn=updatepassword")

        p.userPassword = "password2"
        p.save()

        assertTrue(p.authenticate("password2"))
    }
}

class PasswordOperationsIntegrationTestPerson {
    Set<String> objectclass = ["top", "iNetOrgPerson"]
    String sn = "person"
    @GldapoNamingAttribute String cn
    String userPassword
}