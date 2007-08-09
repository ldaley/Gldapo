package gldapo.util;
import java.lang.reflect.*
import org.apache.commons.lang.WordUtils

class FieldInspector 
{
	static fieldIsReadableAndWritable(Class clazz, Field field)
	{
		def fieldNameCapitalised = WordUtils.capitalize(field.name)

		def getter
		def setter

		try { getter = clazz.getDeclaredMethod("get" + fieldNameCapitalised) } catch (NoSuchMethodException) {}
		try { setter = clazz.getDeclaredMethod("set" + fieldNameCapitalised, field.type) } catch (NoSuchMethodException) {}
		
		return ((getter != null) && (setter != null))
	}
}