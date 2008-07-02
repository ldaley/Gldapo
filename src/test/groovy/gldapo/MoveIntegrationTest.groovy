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
        def rdn = "cn=$cn"

        def constantLdif = """
            sn: $sn
            objectClass: person
            objectClass: top
        """

        p.rdn = rdn
        p.objectClass = ["top", "person"]
        p.cn = cn
        p.sn = sn
        p.create()

        assertEqualsLdif(rdn, """
            cn: $cn
            $constantLdif
        """)

        def oldRdn = rdn
        cn = "moved"
        rdn = "cn=$cn"
        p.move(rdn)

        assertNull(getEntry(oldRdn))

        assertEqualsLdif(rdn, """
            $constantLdif
            cn: moved
        """)

    }

    void testMoveModified() {
        def p = new MoveIntegrationTestPerson()
        def cn = "move"
        def sn = "sn"
        def rdn = "cn=$cn"

        def constantLdif = """
            objectClass: person
            objectClass: top
        """

        p.rdn = rdn
        p.objectClass = ["top", "person"]
        p.cn = cn
        p.sn = sn
        p.create()

        assertEqualsLdif(rdn, """
            $constantLdif
            cn: $cn
            sn: $sn
        """)

        def oldRdn = rdn
        cn = "moved"
        sn = "sn_changed"
        rdn = "cn=$cn"

        p.sn = sn
        p.move(rdn)

        assertNull(getEntry(oldRdn))

        assertEqualsLdif(rdn, """
            $constantLdif
            cn: $cn
            sn: $sn
        """)
    }
}

class MoveIntegrationTestPerson {
    Set<String> objectClass
    String sn
    @GldapoNamingAttribute String cn
}