package gldapwrap.aspect;
import java.lang.reflect.Modifier
import java.lang.reflect.Field
import gldapwrap.exception.GldapwrapInjectionException

class FilterAspect
{
	static final public NO_FILTER_FILTER = "(distinguishedName=*)"
		
	static public void inject(Class clazz)
	{
		try 
		{
			def filter = clazz.getFilter() // Testing to see if it is there
		}
		catch(MissingMethodException e) 
		{
			clazz.metaClass."static".getFilter << { null }
		}
		
		clazz.metaClass."static".andClassFilterWithFilter << { String filter ->
			String classFilter = clazz.getFilter()
			if (filter == null)
			{
				if (classFilter == null)
				{
					filter = FilterAspect.NO_FILTER_FILTER
				}
				else
				{
					filter = classFilter
				}
			}
			else
			{
				if (classFilter != null)
				{
					filter = "(&" + classFilter + filter + ")"
				}	
			}
			
			return filter
		}
	}
}