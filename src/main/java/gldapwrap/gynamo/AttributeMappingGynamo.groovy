package gldapwrap.gynamo;
import gynamo.Gynamo
import gynamo.GynamoPropertyStorage
import gldapwrap.mapping.GldapwrapAttributeMapping

class AttributeMappingGynamo extends Gynamo
{
	static getAttributeMappings = { ->
		return GynamoPropertyStorage[delegate].attributeMappings
	}	
		
	void postGynamize(Class clazz)
	{
		def mappings = []
		Introspector.getBeanInfo(clazz, Object).propertyDescriptors.each {
			if (it.name.equals("metaClass") == false)
			{
				mappings << new GldapwrapAttributeMapping(
					name: it.name,
					type: it.propertyType,
					setter: it.writeMethod.name
				)
			}
		}
		GynamoPropertyStorage[clazz].attributeMappings = mappings
	}
}