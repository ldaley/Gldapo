package gldapo
import gldapo.schema.annotation.GldapoSynonymFor

public class SaveIntegrationTest extends AbstractGldapoIntegrationTest
{
    def schemaClasses = [SaveIntegrationTestPerson]
    
    void testModifySingleValueAttribute() {
        def p = new SaveIntegrationTestPerson()
        def cn = "modify_single_value"
        def sn = "sn"
        def title = "original"
        def titleChanged = "changed"
        def rdn = "cn=$cn"
        
        def constantLdif = """
            cn: $cn
            sn: $sn
            objectClass: person
            objectClass: top
            objectClass: organizationalPerson
        """
        
        p.rdn = rdn
        p.objectClass = ["top", "person", "organizationalPerson"]
        p.cn = cn
        p.sn = sn
        p.title = title
        p.create()
        
        assertEqualsLdif(rdn, """
            $constantLdif
            title: $title
        """)
        
        p.title = titleChanged
        p.update()
        
        assertEqualsLdif(rdn, """
            $constantLdif
            title: $titleChanged
        """)
        
        p.title = null
        p.update()
        
        assertEqualsLdif(rdn, """
            $constantLdif
        """)
        
        p.title = titleChanged
        p.update()
        
        assertEqualsLdif(rdn, """
            $constantLdif
            title: $titleChanged
        """)
    }
    
    void testModifyMutliValueAttribute() {
        def p = new SaveIntegrationTestPerson()
        def cn = "modify_multi_value"
        def sn = "sn"
        def telephoneNumber = ["123"]
        def rdn = "cn=$cn"
        
        def constantLdif = """
            cn: $cn
            sn: $sn
            objectClass: person
            objectClass: top
            objectClass: organizationalPerson
        """
        
        p.rdn = rdn
        p.objectClass = ["top", "person", "organizationalPerson"]
        p.cn = cn
        p.sn = sn
        p.telephoneNumber = telephoneNumber
        p.create()
        
        assertEqualsLdif(rdn, """
            $constantLdif
            telephonenumber: 123
        """)
        
        p.telephoneNumber << "456"
        p.update()

        assertEqualsLdif(rdn, """
            $constantLdif
            telephonenumber: 123
            telephonenumber: 456
        """)
        
        p.telephoneNumber.remove("123")
        p.update()

        assertEqualsLdif(rdn, """
            $constantLdif
            telephonenumber: 456
        """)
        
        p.telephoneNumber = null
        p.update()

        assertEqualsLdif(rdn, """
            $constantLdif
        """)
        
        p.telephoneNumber = [] as Set
        p.update()

        assertEqualsLdif(rdn, """
            $constantLdif
        """)
        
        p.telephoneNumber << "789"
        p.update()

        assertEqualsLdif(rdn, """
            $constantLdif
            telephonenumber: 789
        """)
    }
}

class SaveIntegrationTestPerson {
    Set<String> objectClass
    String sn
    String cn
    String title
    Set<String> telephoneNumber
}