templates {
	t1 {
		contextSource {
			url = "ldap://example.com"
			base = "ou=example,dc=com"
		}
		base = "ou=people"
	}
	t2 {
		contextSource {
			url = "ldap://example2.com"
			base = "ou=example2,dc=com"
		}
		base = "ou=people2"
	}
}
defaultTemplate = "t1"
schemas = [gldapo.schema.provided.Person, gldapo.schema.provided.ActiveDirectoryPerson]

environments {
	dev {
		templates.t2.base = "development"
	}
}