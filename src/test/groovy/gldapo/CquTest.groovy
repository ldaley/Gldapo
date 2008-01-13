package gldapo
import org.springframework.ldap.core.DistinguishedName

class CquTest extends GroovyTestCase {

    static {
        Gldapo.initialize(
            directories: [
                ad: [
                    url: "ldap://rokstaffdc01.staff.ad.cqu.edu.au",
                    base: "dc=staff,dc=ad,dc=cqu,dc=edu,dc=au",
                    userDn: "cn=daleyl,ou=people,dc=staff,dc=ad,dc=cqu,dc=edu,dc=au",
                    password: "",
                    searchControls: [
                        searchScope: "subtree"
                    ]
                ]
            ],
            schemas: [CquPerson, CquGroup]
        )
        Gldapo.instance.directories.ad.template.ignorePartialResultException = true
    }
    
    void testSave() {
        
        def dialup = CquGroup.find(filter: "(name=*dialup*)")
        dialup.member.each {
            def person = CquPerson.getByDn(it)
            println "${person.givenName} ${person.sn}"
        }
    }
}

class CquPerson {

    Set objectclass
    String givenName
    String sn
    String samaccountname
    Set<DistinguishedName> memberOf
    String extensionAttribute4
}

class CquGroup {
    String name
    Set<DistinguishedName> member
}