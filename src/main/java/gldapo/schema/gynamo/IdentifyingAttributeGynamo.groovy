package gldapo.schema.gynamo;
import gldapo.schema.annotations.GldapoIdentifyingAttribute
import gynamo.Gynamo

class IdentifyingAttributeGynamo extends Gynamo
{
	static getIdentifyingAttribute = { ->
		def annotation = delegate.getAnnotation(GldapoIdentifyingAttribute)
		if (annotation)
		{
			return annotation.value()
		}
		else
		{
			return null
		}
	}
}