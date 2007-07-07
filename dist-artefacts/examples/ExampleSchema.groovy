@GldapwrapTypeMapAttributesTo(type=List,mapper={
	// blah
})
@GldapwrapTypeMapAttributesFrom(type=List,mapper={
	// blah
})

@GldapwrapSchemaFilter("(objectclass=person)")
class ExampleSchema 
{
	@GldapwrapSchemaIdentifier
	String uid

	List objectclass
	
	@GldapwrapMapToAttribute({
		return sdfasfd
	})
	@GldapwrapMapFromAttribute({
		return sdfasfd
	})
	Date someDateAttribute
}