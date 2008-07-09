package gldapo
import gldapo.schema.annotation.GldapoSynonymFor
import gldapo.schema.annotation.GldapoNamingAttribute

public class CreateIntegrationTest extends AbstractGldapoIntegrationTest
{
    def schemaClasses = [CreateIntegrationTestPerson]
    
    void testCreateWithAllAttributes() {
        def brdn = "cn=allattributes"
        def p = new CreateIntegrationTestPerson(
            brdn: brdn,
            objectClass: ["top", "person", "organizationalPerson"],
            cn: "allattributes",
            lastName: "surname",
            telephoneNumber: ["123455", "1245"]
        )
        p.save()
        
        assertEqualsLdif(brdn, """
            cn: allattributes
            sn: surname
            objectClass: organizationalPerson
            objectClass: person
            objectClass: top
            telephonenumber: 123455
            telephonenumber: 1245
        """)
    }
    
    void testCreateWithSomeAttributes() {
        def brdn = "cn=someattributes"
        def p = new CreateIntegrationTestPerson(
            cn: "someattributes",
            brdn: brdn,
            objectClass: ["top", "person", "organizationalPerson"],
            lastName: "sn"
        )
        p.save()
        
        assertEqualsLdif(brdn, """
            cn: someattributes
            sn: sn
            objectClass: organizationalPerson
            objectClass: person
            objectClass: top
        """)
    }
    
}


class CreateIntegrationTestPerson {
    Set<String> objectClass
    @GldapoSynonymFor("sn")
    String lastName
    @GldapoNamingAttribute
    String cn
    String title
    Set<String> telephoneNumber
}