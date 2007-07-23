package gldapo.schema.typeconversion;
import java.math.BigInteger
import javax.naming.directory.Attribute

class GldapoConversions 
{
	static public String convertToString(Attribute value)
	{
		return value.get().toString()
	}
	
	static public Integer convertToInteger(Attribute value)
	{
		return value.get().toInteger()
	}
	
	static public BigInteger convertToBigInteger(Attribute value)
	{
		return new BigInteger(value.get());
	}
	
	static public List convertToList(Attribute value)
	{
		def list = []
		0.upto(value.size() - 1) {
			list << value.get(it)
		}
		return list
	}
}