package gldapwrap.gynamo;
import gynamo.Gynamo
import gynamo.GynamoDependencies
import org.springframework.ldap.filter.Filter
import javax.naming.directory.SearchControls
import gldapwrap.mapping.DefaultGldapwrapAttributeMapper
import gldapwrap.mapping.GldapwrapAttributeMapper
import org.springframework.ldap.core.LdapOperations
import org.springframework.ldap.core.CollectingNameClassPairCallbackHandler
import org.springframework.ldap.core.AttributesMapperCallbackHandler
import org.springframework.ldap.control.PagedResultsRequestControl
import org.springframework.ldap.core.AttributesMapper
import org.springframework.ldap.LimitExceededException

@GynamoDependencies([AttributeMappingGynamo, FilterGynamo, SearchControlsGynamo, TemplateGynamo, TypeCoercionGynamo])
class FindGynamo
{
	static DEFAULT_PAGE_SIZE = 500
	
	static find = { Map options ->
			
		LdapOperations template = delegate.getLdapTemplateAssert()
		List attributeMappings = delegate.getAttributeMappings()
		
		String filter = delegate.andClassFilterWithFilter(options?.filter)
		String base = (options?.base == null) ? "" : options.base
		SearchControls controls = clazz.mergeClassSearchControlsWithOptions(options)
		int pageSize = options?.pageSize == null ? FindGynamo.DEFAULT_PAGE_SIZE : options.pageSize
		
		// Restrict the returning attributes to just those specified in the schema
		controls.returningAttributes = attributeMappings*.name
		
		GldapwrapAttributeMapper mapper = new DefaultGldapwrapAttributeMapper(mapToClass: clazz)
		CollectingNameClassPairCallbackHandler handler = new AttributesMapperCallbackHandler(mapper)

		try
		{
			PagedResultsRequestControl requestControl = new PagedResultsRequestControl(pageSize)
			template.search(base, filter, controls, handler, requestControl)
			
			while (requestControl?.cookie?.cookie != null)
			{
				requestControl = new PagedResultsRequestControl(pageSize, requestControl.cookie)
				template.search(base, filter, controls, handler, requestControl)
			} 

			return handler.list
		}
		catch (LimitExceededException e)
		{
			// If the number have entries has hit the specified count limit OR
			// The server is unwilling to send more entries we will get here.
			// It's not really an error condition hence we just return what we found.
			
			return handler.list
		}
		
	}
}