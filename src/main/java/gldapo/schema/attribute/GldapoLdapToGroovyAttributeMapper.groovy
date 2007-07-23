package gldapo.schema.attribute;
import javax.naming.directory.Attributes;

class GldapoLdapToGroovyAttributeMapper
{
	Class schema
	List attributeMappings
	
	void setSchema(Class schema)
	{
		schema = schema
		attributeMappings = schema.getAttributeMappings()
	}
	
	Object mapFromAttributes(Attributes attributes)
	{
		def object = schema.newInstance()
		attributeMappings.each { GldapoAttributeMapping attributeMapping ->
			def coercedValue = schema.coerceLdapAttributeToGroovy(
				attributeMapping.type, 
				attributeMapping.name, 
				attributes.get(attributeMapping.name)
			)
			object."${attributeMapping.name}" = coercedValue
		}
		return object
	}
}