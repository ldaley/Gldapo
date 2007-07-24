package gldapo.schema.gynamo;
import gldapo.schema.annotations.GldapoSchemaFilter
import gynamo.Gynamo
import gynamo.GynamoPropertyStorage

class SchemaFilterGynamo extends Gynamo
{
	static final public NO_FILTER_FILTER = "(objectclass=*)"
		
	static getSchemaFilter = { ->
		def filterAnnotation = delegate.getAnnotation(GldapoSchemaFilter)
		if (filterAnnotation)
		{
			return filterAnnotation.value()
		}
		else
		{
			return SchemaFilterGynamo.NO_FILTER_FILTER
		}
	}
	
	static andSchemaFilterWithFilter = { String filter ->
		String schemaFilter = delegate.getSchemaFilter()
		if (filter == null)
		{
			if (schemaFilter == null || schemaFilter.equals(SchemaFilterGynamo.NO_FILTER_FILTER))
			{
				filter = SchemaFilterGynamo.NO_FILTER_FILTER
			}
			else
			{
				filter = schemaFilter
			}
		}
		else
		{
			if (schemaFilter != null && schemaFilter.equals(SchemaFilterGynamo.NO_FILTER_FILTER) == false)
			{
				filter = "(&${schemaFilter}${filter})"
			}	
		}
		
		return filter
	}
}