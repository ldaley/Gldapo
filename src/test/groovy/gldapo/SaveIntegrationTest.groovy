package gldapo
import gldapo.schema.annotation.GldapoSynonymFor
import gldapo.schema.annotation.GldapoNamingAttribute

public class SaveIntegrationTest extends AbstractGldapoIntegrationTest
{
    def schemaClasses = [SaveIntegrationTestPerson]
    
    void testModifySingleValueAttribute() {
        def p = new SaveIntegrationTestPerson()
        def cn = "modify_single_value"
        def sn = "sn"
        def title = "original"
        def titleChanged = "changed"
        def brdn = "cn=$cn"
        
        def constantLdif = """
            cn: $cn
            sn: $sn
            objectClass: person
            objectClass: top
            objectClass: organizationalPerson
        """
        
        p.brdn = brdn
        p.objectClass = ["top", "person", "organizationalPerson"]
        p.cn = cn
        p.sn = sn
        p.title = title
        p.create()
        
        assertEqualsLdif(brdn, """
            $constantLdif
            title: $title
        """)
        
        p.title = titleChanged
        p.update()
        
        assertEqualsLdif(brdn, """
            $constantLdif
            title: $titleChanged
        """)
        
        p.title = null
        p.update()
        
        assertEqualsLdif(brdn, """
            $constantLdif
        """)
        
        p.title = titleChanged
        p.update()
        
        assertEqualsLdif(brdn, """
            $constantLdif
            title: $titleChanged
        """)
    }
    
    void testModifyMutliValueAttribute() {
        def p = new SaveIntegrationTestPerson()
        def cn = "modify_multi_value"
        def sn = "sn"
        def telephoneNumber = ["123"]
        def brdn = "cn=$cn"
        
        def constantLdif = """
            cn: $cn
            sn: $sn
            objectClass: person
            objectClass: top
            objectClass: organizationalPerson
        """
        
        p.brdn = brdn
        p.objectClass = ["top", "person", "organizationalPerson"]
        p.cn = cn
        p.sn = sn
        p.telephoneNumber = telephoneNumber
        p.create()
        
        assertEqualsLdif(brdn, """
            $constantLdif
            telephonenumber: 123
        """)
        
        p.telephoneNumber << "456"
        p.update()

        assertEqualsLdif(brdn, """
            $constantLdif
            telephonenumber: 123
            telephonenumber: 456
        """)
        
        p.telephoneNumber.remove("123")
        p.update()

        assertEqualsLdif(brdn, """
            $constantLdif
            telephonenumber: 456
        """)
        
        p.telephoneNumber = null
        p.update()

        assertEqualsLdif(brdn, """
            $constantLdif
        """)
        
        p.telephoneNumber = [] as Set
        p.update()

        assertEqualsLdif(brdn, """
            $constantLdif
        """)
        
        p.telephoneNumber << "789"
        p.update()

        assertEqualsLdif(brdn, """
            $constantLdif
            telephonenumber: 789
        """)
    }
}

class SaveIntegrationTestPerson {
    Set<String> objectClass
    String sn
    @GldapoNamingAttribute
    String cn
    String title
    Set<String> telephoneNumber
}