package gldapo
import gldapo.exception.GldapoException

public class DeleteIntegrationTest extends AbstractGldapoIntegrationTest
{
    def schemaClasses = [DeleteIntegrationTestPerson, DeleteIntegrationTestOrgUnit]

    void testDeleteContainer() {
        importEntry(ou: "deleteSub", """
            objectclass: top
            objectclass: organizationalUnit
        """)

        importEntry(cn: "deleteSubChild", "ou=deleteSub", """
            objectclass: top
            objectclass: person
            sn: deleteSubChild
        """)

        def ou = DeleteIntegrationTestOrgUnit.find(filter: "ou=deleteSub")
        assertNotNull(ou)

        try
        {
            ou.delete()
            fail("Exception should have been thrown")
        }
        catch (org.springframework.ldap.ContextNotEmptyException e)
        {

        }

        ou.deleteRecursively()
        assertNull(getEntry("ou=deleteSub"))
    }

    void testDeleteLeaf() {

        importEntry(cn: "delete", """
            objectclass: top
            objectclass: person
            sn: delete
        """)

        def entry = DeleteIntegrationTestPerson.find(filter: "cn=delete")
        assertNotNull(entry)
        entry.delete()
        assertNull(getEntry("cn=delete"))

        try
        {
            entry.sn = "changed"
            entry.save()
            fail("Exception should have been thrown")
        }
        catch (GldapoException)
        {

        }
    }
}

class DeleteIntegrationTestOrgUnit {
    String ou
}

class DeleteIntegrationTestPerson {
    String sn
    String cn
}