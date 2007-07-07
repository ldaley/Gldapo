package gldapwrap.aspect;
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import gldapwrap.mapping.GldapwrapAttributeMapping
import org.apache.commons.lang.WordUtils
import java.beans.Introspector

class AttributeMappingAspect
{
	static public void inject(Class clazz)
	{
		def attributes = introspectForAttributeMappings(clazz)
		clazz.metaClass.'static'.getAttributeMappings << { -> attributes }
	}
	
	static List introspectForAttributeMappings(Class clazz)
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
		return mappings
	}
}