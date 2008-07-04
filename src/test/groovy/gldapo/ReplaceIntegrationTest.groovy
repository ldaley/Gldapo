package gldapo
import gldapo.schema.annotation.GldapoSchemaFilter
import gldapo.schema.annotation.GldapoNamingAttribute

public class ReplaceIntegrationTest extends AbstractGldapoIntegrationTest
{
    def schemaClasses = [ReplaceIntegrationTestPerson]

    void testReplace() {
        
        importEntry(cn: "replaceme", """
            objectclass: top
            objectclass: person
            sn: replaceme
        """)
        
        def e = ReplaceIntegrationTestPerson.find(filter: "(cn=replaceme)")
        assertNotNull(e)
        assertEquals("replaceme", e.sn)
        
        def p = new ReplaceIntegrationTestPerson()
        p.objectclass = ["top", "person"]
        p.cn = "replaceme"
        p.sn = "replaced"
        p.replace("cn=replaceme")
        
        def e2 = ReplaceIntegrationTestPerson.find(filter: "(cn=replaceme)")
        assertNotNull(e2)
        assertEquals("replaced", e2.sn)
    }
    
    void testReplaceUsingNamingAttribute() {
        importEntry(cn: "replaceusingnaming", """
            objectclass: top
            objectclass: person
            sn: replaceme
        """)
    
        def p = new ReplaceIntegrationTestPerson(
            sn: "replaced",
            objectclass: ["top", "person"]
        )

        p.replace("replaceusingnaming", null)

        assertEqualsLdif("cn=replaceusingnaming", """
            objectClass: top
            objectClass: person
            cn: replaceusingnaming
            sn: replaced
        """)
    }

    void testReplaceUsingNamingAttributeAndParent() {

        importEntry(ou: "replaceSub", """
            objectclass: top
            objectclass: organizationalUnit
        """)

        importEntry(cn: "replaceusingnaming,ou=replaceSub", """
            objectclass: top
            objectclass: person
            sn: replaceme
        """)
        
        def p = new ReplaceIntegrationTestPerson(
            sn: "replacedBelow",
            objectclass: ["top", "person"]
        )

        p.replace("replaceusingnaming", "ou=replaceSub")
        
        assertEqualsLdif("cn=replaceusingnaming,ou=replaceSub", """
            objectClass: top
            objectClass: person
            cn: replaceusingnaming
            sn: replacedBelow
        """)
        
        assertEquals("cn=replaceusingnaming, ou=replaceSub", p.rdn as String)
    }
}

@GldapoSchemaFilter("(objectclass=person)")
class ReplaceIntegrationTestPerson {
    Set<String> objectclass
    String sn
    @GldapoNamingAttribute
    String cn
}