package gldapo
import gldapo.schema.annotation.GldapoSynonymFor
import gldapo.schema.annotation.GldapoNamingAttribute
import com.novell.ldap.util.Base64

public class ByteArrayAttributeIntegrationTest extends AbstractGldapoIntegrationTest
{
    def schemaClasses = [ByteArrayAttributeIntegrationTestPerson]
    
    void testReadImageAttribute() {
        
        def cn = "photo"
        
        def img1 = []
        this.class.classLoader.findResource("img1.jpg").eachByte { img1 << it }
        
        importEntry(cn: cn, """
            sn: $cn
            objectClass: person
            objectClass: top
            objectClass: inetOrgPerson
            photo:: ${Base64.encode(img1 as byte[])}
            usercertificate:: ${Base64.encode("4" as byte[])}
            usercertificate:: ${Base64.encode("5" as byte[])}
            usercertificate:: ${Base64.encode("6" as byte[])}
        """)
        
        def i = ByteArrayAttributeIntegrationTestPerson.find { eq("cn", cn) }
        assertEquals(img1, i.photo as List)
        "456".each { y ->
            assertNotNull(i.usercertificate.find { it == y as Byte[] })
        }
        
        assertTrue("removing cert 1", i.usercertificate.remove(i.usercertificate.find { Arrays.equals(it as byte[], "4" as byte[]) }))
        assertEquals(2, i.usercertificate.size())
        
        i.usercertificate.add("7" as Byte[])
        
        def img2 = []
        this.class.classLoader.findResource("img2.jpg").eachByte { img2 << it }
        i.photo = img2 as byte[]
        
        i.save()
        
        def i2 = ByteArrayAttributeIntegrationTestPerson.find { eq("cn", cn) }
        assertEquals(img2, i2.photo as List)
                
        "567".each { y ->
            assertNotNull("$y byte not found", i2.usercertificate.find { it == y as Byte[] })
        }
        
    }
}

class ByteArrayAttributeIntegrationTestPerson {
    Set<String> objectClass
    String sn
    @GldapoNamingAttribute
    String cn
    byte[] photo
    Set<Byte[]> usercertificate
}