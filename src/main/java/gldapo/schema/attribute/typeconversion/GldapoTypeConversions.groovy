package gldapo;
import java.math.BigInteger
import javax.naming.directory.Attribute

class GldapwrapTypeCoercions 
{
	static public String coerceToStringType(Attribute value)
	{
		return value.get().toString()
	}
	
	static public Integer coerceToIntegerType(Attribute value)
	{
		return value.get().toInteger()
	}
	
	static public BigInteger coerceToBigIntegerType(Attribute value)
	{
		return new BigInteger(value.get());
	}
	
	static public List coerceToListType(Attribute value)
	{
		def list = []
		0.upto(value.size() - 1) {
			list << value.get(it)
		}
		return list
	}
}