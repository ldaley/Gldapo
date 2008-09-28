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
        """)
        
        def i = ByteArrayAttributeIntegrationTestPerson.find { eq("cn", cn) }
        assertEquals(img1, i.photo as List)
        
        def img2 = []
        this.class.classLoader.findResource("img2.jpg").eachByte { img2 << it }
        i.photo = img2 as byte[]
        i.save()
        
        def i2 = ByteArrayAttributeIntegrationTestPerson.find { eq("cn", cn) }
        assertEquals(img2, i2.photo as List)
    }
}

class ByteArrayAttributeIntegrationTestPerson {
    Set<String> objectClass
    String sn
    @GldapoNamingAttribute
    String cn
    byte[] photo
}