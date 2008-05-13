import gldapo.Gldapo
import gldapo.schema.annotation.*

/*
 * See http://ldaley.com/gldapo/schemaclasses/index.html
 */
@GldapoSchemaFilter("(objectclass=person)")
class ExamplePerson 
{    
    String cn
    String givenName
    String mail
    Integer mailstop
    Set<String> objectclass
    String postalAddress
    String sn
    String telephoneNumber
    String title
    
    @GldapoSynonymFor("uid")
    String username
}

/*
 * See http://ldaley.com/gldapo/initialization/programmatic.html
 */
Gldapo.initialize(
    directories: [
        washington: [
            url: "ldap://directory.washington.edu", // <-- Change to be the host name of your directory
            base: "ou=people,o=University of Washington,c=US", // <-- Change to be your search base
            // userDn: "uid=someuser ...", // Change to be the full dn of the user to bind as
            // password: "password" // Your bind user password
        ]    
    ],
    schemas: [ExamplePerson]
)

/*
 * See http://ldaley.com/gldapo/schemaclasses/searching.html
 */
def people = ExamplePerson.findAll(
    countLimit: 50, 
    searchScope: "subtree", 
    base: "ou=Faculty and Staff",
    filter: "(uid=*)"
)

people.each {
    println ""
    println "** ${it.username} **"
    println "name: ${it.givenName} ${it.sn}"
    println "objectclass:"
    it.objectclass.each {
        println "   $it"
    }
}