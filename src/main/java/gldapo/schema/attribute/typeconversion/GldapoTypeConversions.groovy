package gldapo.schema.attribute.typeconversion;
import java.math.BigInteger
import javax.naming.directory.Attribute

class GldapoTypeConversions 
{
	static public String convertToStringType(Attribute value)
	{
		return value.get().toString()
	}
	
	static public Integer convertToIntegerType(Attribute value)
	{
		return value.get().toInteger()
	}
	
	static public BigInteger convertToBigIntegerType(Attribute value)
	{
		return new BigInteger(value.get());
	}
	
	static public List convertToListType(Attribute value)
	{
		def list = []
		0.upto(value.size() - 1) {
			list << value.get(it)
		}
		return list
	}
}