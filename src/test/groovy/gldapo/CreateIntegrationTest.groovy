package gldapo
import gldapo.schema.annotation.GldapoSynonymFor
import gldapo.schema.annotation.GldapoNamingAttribute

public class CreateIntegrationTest extends AbstractGldapoIntegrationTest
{
    def schemaClasses = [CreateIntegrationTestPerson]
    
    void testCreateWithAllAttributes() {
        def rdn = "cn=allattributes"
        def p = new CreateIntegrationTestPerson(
            rdn: rdn,
            objectClass: ["top", "person", "organizationalPerson"],
            cn: "allattributes",
            lastName: "surname",
            telephoneNumber: ["123455", "1245"]
        )
        p.save()
        
        assertEqualsLdif(rdn, """
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
        def rdn = "cn=someattributes"
        def p = new CreateIntegrationTestPerson(
            cn: "someattributes",
            rdn: rdn,
            objectClass: ["top", "person", "organizationalPerson"],
            lastName: "sn"
        )
        p.save()
        
        assertEqualsLdif(rdn, """
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