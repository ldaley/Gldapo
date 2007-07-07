package gldapwrap;
import gldapwrap.aspect.*
import org.springframework.ldap.core.LdapOperations
import gldapwrap.GldapwrapTemplate


abstract class GldapwrapInjector
{
	static aspects = [
		AttributeMappingAspect, SearchControlsAspect, TemplateAspect, 
		FilterAspect, FindAspect, TypeCoercionAspect, 
		IdentifierAspect, LoadAspect
	]
	
	static void inject(Class clazz)
	{
		inject(clazz, null)
	}
	
	static void inject(Class clazz, GldapwrapTemplate gldapwrapTemplate)
	{
		gldapwrapTemplate.afterPropertiesSet() // Make sure the underlying spring stuff gets inited
		inject(clazz, gldapwrapTemplate.ldapTemplate)
	}
	
	static void inject(Class clazz, LdapOperations ldapTemplate)
	{
		aspects.each { aspect ->
			aspect.inject(clazz)
		}
		
		if (ldapTemplate)
		{
			clazz.setLdapTemplate(ldapTemplate)
		}
	}
}