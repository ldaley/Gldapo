package gldapwrap.gynamo;
import gynamo.Gynamo

class FilterGynamo extends Gynamo
{
	static final public NO_FILTER_FILTER = "(distinguishedName=*)"
		
	static getFilter = { ->
		// TODO look for annotation
	}
	
	static andClassFilterWithFilter = { String filter ->
		String classFilter = clazz.getFilter()
		if (filter == null)
		{
			if (classFilter == null)
			{
				filter = FilterGynamo.NO_FILTER_FILTER
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