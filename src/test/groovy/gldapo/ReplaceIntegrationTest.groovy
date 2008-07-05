package gldapo
import gldapo.schema.annotation.GldapoSchemaFilter
import gldapo.schema.annotation.GldapoNamingAttribute

public class ReplaceIntegrationTest extends AbstractGldapoIntegrationTest
{
    def schemaClasses = [ReplaceIntegrationTestPerson]

    void testReplaceUsingRdn() {
        
        importEntry(cn: "replaceusingrdn", """
            objectclass: top
            objectclass: person
            sn: replaceme
        """)
        
        def e = ReplaceIntegrationTestPerson.find(filter: "(cn=replaceusingrdn)")
        assertNotNull(e)
        assertEquals("replaceme", e.sn)
        
        def p = new ReplaceIntegrationTestPerson()
        p.objectclass = ["top", "person"]
        p.cn = "replaceme"
        p.sn = "replaced"
        p.replace("cn=replaceusingrdn")
        
        def e2 = ReplaceIntegrationTestPerson.find(filter: "(cn=replaceusingrdn)")
        assertNotNull(e2)
        assertEquals("replaced", e2.sn)
    }
    
    void testReplace() {
        
        importEntry(cn: "replace", """
            objectclass: top
            objectclass: person
            sn: replaceme
        """)
        
        def e = ReplaceIntegrationTestPerson.find(filter: "(cn=replace)")
        assertNotNull(e)
        assertEquals("replaceme", e.sn)
        
        def p = new ReplaceIntegrationTestPerson()
        p.objectclass = ["top", "person"]
        p.cn = "replace"
        p.sn = "replaced"
        p.replace()
        
        def e2 = ReplaceIntegrationTestPerson.find(filter: "(cn=replace)")
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
    
    void testReplaceUsingParent() {

        importEntry(ou: "replaceSub2", """
            objectclass: top
            objectclass: organizationalUnit
        """)

        importEntry(cn: "replaceusingparent,ou=replaceSub2", """
            objectclass: top
            objectclass: person
            sn: replaceme
        """)
        
        def p = new ReplaceIntegrationTestPerson(
            sn: "replacedBelow",
            objectclass: ["top", "person"]
        )

        shouldFail(gldapo.exception.GldapoException) {
            p.replace(null, "ou=replaceSub2")
        }
        
        p.cn = "replaceusingparent"
        p.replace(null, "ou=replaceSub2")
        
        assertEqualsLdif("cn=replaceusingparent,ou=replaceSub2", """
            objectClass: top
            objectClass: person
            cn: replaceusingparent
            sn: replacedBelow
        """)
        
        assertEquals("cn=replaceusingparent, ou=replaceSub2", p.rdn as String)
    }
    
}

@GldapoSchemaFilter("(objectclass=person)")
class ReplaceIntegrationTestPerson {
    Set<String> objectclass
    String sn
    @GldapoNamingAttribute
    String cn
}