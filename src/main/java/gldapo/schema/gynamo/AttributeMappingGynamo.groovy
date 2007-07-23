package gldapo.schema.gynamo;
import gynamo.Gynamo
import gynamo.GynamoPropertyStorage
import gldapo.schema.attribute.GldapoAttributeMapping

class AttributeMappingGynamo extends Gynamo
{
	static getAttributeMappings = { ->
		if (GynamoPropertyStorage[delegate].attributeMappings == null)
		{
			def mappings = []
			Introspector.getBeanInfo(clazz, Object).propertyDescriptors.each {
				if (it.name.equals("metaClass") == false)
				{
					mappings << new GldapoAttributeMapping(
						name: it.name,
						type: it.propertyType,
					)
				}
			}
			GynamoPropertyStorage[delegate].attributeMappings = mappings
		}
		return GynamoPropertyStorage[delegate].attributeMappings
	}	
}