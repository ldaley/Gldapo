package gldapo
import gldapo.schema.annotation.GldapoSchemaFilter

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
}

@GldapoSchemaFilter("(objectclass=person)")
class ReplaceIntegrationTestPerson {
    Set<String> objectclass
    String sn
    String cn
}