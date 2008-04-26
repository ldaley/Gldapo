package gldapo

import java.io.File

import javax.naming.Context
import javax.naming.InitialContext
import javax.naming.NamingException
import javax.naming.directory.Attribute
import javax.naming.directory.Attributes
import javax.naming.directory.BasicAttribute
import javax.naming.directory.BasicAttributes
import javax.naming.directory.DirContext

import org.apache.directory.server.core.configuration.MutablePartitionConfiguration
import org.apache.directory.server.unit.AbstractServerTest

abstract public class AbstractGldapoIntegrationTest extends AbstractServerTest
{
    def gldapo
    def partitionName = "gldapo"
    def partitionSuffix = "o=$partitionName"
    
     public void setUp()
     {
         def pcfg = new MutablePartitionConfiguration()
         pcfg.name = partitionName
         pcfg.suffix = partitionSuffix
         pcfg.indexedAttributes = ["objectClass", "o"] as Set

         def attrs = new BasicAttributes(true)
         def attr = new BasicAttribute("objectClass")
         attr.add("top")
         attr.add("organization")
         attrs.put(attr)

         attr = new BasicAttribute("o")
         attr.add(partitionName)
         attrs.put(attr)
         pcfg.contextEntry = attrs
         
         def pcfgs = new HashSet<MutablePartitionConfiguration>()
         pcfgs.add(pcfg)

         configuration.contextPartitionConfigurations = pcfgs
         configuration.workingDirectory = new File("target/ldap-server")

         super.setUp()
         
         initGldapo()
    }

    def initGldapo() {
        gldapo = new Gldapo(
            directories: [
                local: [
                    url: "ldap://localhost:${configuration.ldapPort}",
                    base: partitionSuffix,
                    userDn: "uid=admin,ou=system",
                    password: "secret"
                ]
            ],
            schemas: getSchemaClasses(),
            typeMappings: getTypeMappings()
        )
    }
    
    def getSchemaClasses() {
        []
    }
    
    def getTypeMappings() {
        []
    }
    
    void importLdif(ldif) {
        importLdif(this.class.getResourceAsStream(ldif));
    }
    
    public void tearDown()
    {
        super.tearDown()
    }
}