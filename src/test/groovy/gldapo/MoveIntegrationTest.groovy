package gldapo
import gldapo.schema.annotation.GldapoSynonymFor
import gldapo.schema.annotation.GldapoNamingAttribute

public class MoveIntegrationTest extends AbstractGldapoIntegrationTest
{
    def schemaClasses = [MoveIntegrationTestPerson]

    void testMoveUnmodified() {
        def p = new MoveIntegrationTestPerson()
        def cn = "move"
        def sn = "sn"
        def brdn = "cn=$cn"

        def constantLdif = """
            sn: $sn
            objectClass: person
            objectClass: top
        """

        p.brdn = brdn
        p.objectClass = ["top", "person"]
        p.cn = cn
        p.sn = sn
        p.create()

        assertEqualsLdif(brdn, """
            cn: $cn
            $constantLdif
        """)

        def oldRdn = brdn
        cn = "moved"
        brdn = "cn=$cn"
        p.move(brdn)

        assertNull(getEntry(oldRdn))

        assertEqualsLdif(brdn, """
            $constantLdif
            cn: moved
        """)

    }

    void testMoveModified() {
        def p = new MoveIntegrationTestPerson()
        def cn = "move"
        def sn = "sn"
        def brdn = "cn=$cn"

        def constantLdif = """
            objectClass: person
            objectClass: top
        """

        p.brdn = brdn
        p.objectClass = ["top", "person"]
        p.cn = cn
        p.sn = sn
        p.create()

        assertEqualsLdif(brdn, """
            $constantLdif
            cn: $cn
            sn: $sn
        """)

        def oldRdn = brdn
        cn = "moved"
        sn = "sn_changed"
        brdn = "cn=$cn"

        p.sn = sn
        p.move(brdn)

        assertNull(getEntry(oldRdn))

        assertEqualsLdif(brdn, """
            $constantLdif
            cn: $cn
            sn: $sn
        """)
    }

    void testMoveUsingNamingAttribute() {
        def cn = "moveByNaming"
        def sn = "sn"
        def brdn = "cn=$cn"
        def objectClass = ["top", "person"]
        
        def constantLdif = """
            objectClass: person
            objectClass: top
        """
        
        def p = new MoveIntegrationTestPerson(
            cn: cn,
            sn: sn,
            objectClass: objectClass
        )

        p.create()

        assertEqualsLdif(brdn, """
            $constantLdif
            cn: $cn
            sn: $sn
        """)

        def oldRdn = brdn
        cn = "movedByNaming"
        brdn = "cn=$cn"

        p.move(cn, null)

        assertNull(getEntry(oldRdn))

        assertEqualsLdif(brdn, """
            $constantLdif
            cn: $cn
            sn: $sn
        """)
    }
    
    void testMoveUsingNamingAttributeAndParent() {
        def cn = "moveByNamingAndParent"
        def sn = "sn"
        def brdn = "cn=$cn"
        def objectClass = ["top", "person"]
        def constantLdif = """
            objectClass: person
            objectClass: top
        """

        def p = new MoveIntegrationTestPerson(
            cn: cn,
            sn: sn,
            objectClass: objectClass
        )
        
        p.create()

        importEntry(ou: "moveByNamingAndParentSub", """
            objectclass: top
            objectclass: organizationalUnit
        """)
        
        cn = "movedByNamingAndParent"
        sn = "changed"
        p.sn = "changed"
        
        p.move(cn, "ou=moveByNamingAndParentSub")
        
        assertEqualsLdif("cn=$cn,ou=moveByNamingAndParentSub", """
            $constantLdif
            cn: $cn
            sn: $sn
        """)
        
    }
    
    void testMoveUsingParent() {
        def cn = "moveByParent"
        def sn = "sn"
        def brdn = "cn=$cn"
        def objectClass = ["top", "person"]
        def constantLdif = """
            objectClass: person
            objectClass: top
        """

        def p = new MoveIntegrationTestPerson(
            cn: cn,
            sn: sn,
            objectClass: objectClass
        )
        
        p.create()

        importEntry(ou: "moveByParentSub", """
            objectclass: top
            objectclass: organizationalUnit
        """)
        
        sn = "changed"
        p.sn = "changed"
        
        p.move(null, "ou=moveByParentSub")
        
        assertEqualsLdif("cn=$cn,ou=moveByParentSub", """
            $constantLdif
            cn: $cn
            sn: $sn
        """)
        
        p = new MoveIntegrationTestPerson()
        shouldFail(gldapo.exception.GldapoException) {
            p.move(null, "ou=moveByParentSub")
        }
        
    }
}

class MoveIntegrationTestPerson {
    Set<String> objectClass
    String sn
    @GldapoNamingAttribute String cn
}