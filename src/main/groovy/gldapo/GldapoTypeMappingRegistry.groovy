package gldapo
import gldapo.schema.attribute.DefaultTypeMappings
import gldapo.schema.attribute.AbstractAttributeMapping

class GldapoTypeMappingRegistry extends LinkedList<Class>
{
	GldapoTypeMappingRegistry()
	{
		super()
		this << DefaultTypeMappings
	}
	
	def getToFieldMapperForType(String type)
	{
		findMapper(AbstractAttributeMapping.toFieldByTypeMapperName(type), Object)
	}
	
	def findMapper(String mapperName, Class[] argTypes)
	{
		def mapping
		def provider = this.reverse().find {
			mapping = it.metaClass.getMetaMethod(mapperName, argTypes)
			return mapping?.isStatic()
		}
		
		(provider && mapping) ? { mapping.invoke(provider, it) } : null
	}
}