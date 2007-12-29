import gldapo.schema.annotation.*

@GldapoSchemaFilter("(objectclass=uwPerson)")
class WashingtonEduPerson 
{    
    String cn
    String givenName
    String mail
    Integer mailstop
    List<String> objectclass
    String postalAddress
    String sn
    String telephoneNumber
    String title
    String uid
}