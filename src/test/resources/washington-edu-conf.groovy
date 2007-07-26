templates {
	washington {
		contextSource {
			url = "ldap://directory.washington.edu"
			base = "o=University of Washington,c=US"
		}
	}
}
defaultTemplate = "washington"

schemas = [gldapo.schema.provided.Person]