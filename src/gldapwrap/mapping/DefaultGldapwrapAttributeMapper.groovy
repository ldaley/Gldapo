package gldapwrap.mapping;
import javax.naming.directory.Attributes;

class DefaultGldapwrapAttributeMapper implements GldapwrapAttributeMapper {
	Class mapToClass
	List attributeMappings
	
	void setMapToClass(Class clazz)
	{
		mapToClass = clazz
		attributeMappings = clazz.getAttributeMappings()
	}
	
	Object mapFromAttributes(Attributes attributes)
	{
		def match = mapToClass.newInstance()
		attributeMappings.each { GldapwrapAttributeMapping attributeMapping ->
			def coercedValue = mapToClass.coerceLdapToNative(
				attributeMapping.type, 
				attributeMapping.name, 
				attributes.get(attributeMapping.name)
			)
			match."${attributeMapping.setter}"(coercedValue)
		}
		return match
	}
}