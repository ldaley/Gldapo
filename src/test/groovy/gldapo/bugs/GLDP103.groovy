package gldapo.bugs
import gldapo.*
import gldapo.schema.annotation.*
import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class GLDP103 extends AbstractGldapoIntegrationTest {

    def schemaClasses = [GLDP103Entry]
    
    void testModify() {
        def e = new GLDP103Entry(sn: "sn", cn: "cn")
        e.save()
        e.sn = "sn2"
        e.save()
    }
    
}

class GLDP103Entry {

    boolean equals(Object obj) {
        EqualsBuilder.reflectionEquals(this, obj)
    }
    
    int hashCode() { 
        HashCodeBuilder.reflectionHashCode(this) 
    }
    
    Set<String> objectClass = ["top", "person", "organizationalPerson"]
    String sn
    
    @GldapoNamingAttribute
    String cn

}