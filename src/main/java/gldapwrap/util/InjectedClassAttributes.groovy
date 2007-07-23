package gldapwrap.util;

class InjectedClassAttributes 
{
	static Map attributes = Collections.synchronizedMap([:])
	
	static Object getAt(List l)
	{
		return attributes[keyize(l)]
	}
	
	static Object putAt(List l, Object v)
	{
		attributes[keyize(l)] = v
	}
	
	static private String keyize(List l)
	{
		return l[0].name + '-' + l[1]
	}
}