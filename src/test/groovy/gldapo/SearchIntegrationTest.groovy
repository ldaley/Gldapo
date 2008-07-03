package gldapo
import gldapo.schema.annotation.GldapoSchemaFilter
import gldapo.schema.annotation.GldapoNamingAttribute

public class SearchIntegrationTest extends AbstractGldapoIntegrationTest
{
    def schemaClasses = [SearchIntegrationTestPerson]
    def numEntries = 10
    def halfNumEntries = Math.ceil(numEntries / 2) as Integer
    def entryRange = 1..numEntries

    void setUp() {
        super.setUp()

        entryRange.each {
            importEntry(cn: "search$it", """
                objectclass: top
                objectclass: person
                sn: search$it
            """)
        }

        importEntry(ou: "searchSub", """
            objectclass: top
            objectclass: organizationalUnit
        """)

        entryRange.each {
            importEntry(cn: "searchSub$it", "ou=searchSub", """
                objectclass: top
                objectclass: person
                sn: searchSub$it
            """)
        }
    }

    void testFind() {
        def entry = SearchIntegrationTestPerson.find(filter: "(cn=search$numEntries)")
        assertEquals("search$numEntries", entry.cn)
    }

    void testFindAll() {
        def all = SearchIntegrationTestPerson.findAll()
        assertEquals(numEntries, all.size())

        // That we got objects for each, not just X copies of the same
        entryRange.each {
            def targetCn = "search$it"
            assertNotNull("Should contain match for cn = $targetCn", all.find { it.cn == targetCn } )
        }
    }

    void testFindAllSubtree() {
        def all = SearchIntegrationTestPerson.findAll(searchScope: "subtree")
        assertEquals(numEntries * 2, all.size())

        entryRange.each {
            def targetCn = "search$it"
            assertNotNull("Should contain match for cn = $targetCn", all.find { it.cn == targetCn } )
        }

        entryRange.each {
            def targetCn = "searchSub$it"
            assertNotNull("Should contain match for cn = $targetCn", all.find { it.cn == targetCn } )
        }
    }

    void testCountLimit() {
        def half = SearchIntegrationTestPerson.findAll(countLimit: halfNumEntries)
        assertEquals(halfNumEntries, half.size())
    }
}

@GldapoSchemaFilter("(objectclass=person)")
class SearchIntegrationTestPerson {
    String sn
    @GldapoNamingAttribute
    String cn
}