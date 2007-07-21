package gldapwrap;
import org.springframework.ldap.core.LdapTemplate
import org.springframework.ldap.core.support.LdapContextSource
import org.springframework.beans.factory.InitializingBean

class GldapwrapTemplate implements InitializingBean
{
	LdapContextSource contextSource
	LdapTemplate ldapTemplate
	boolean initialised = false
	
	GldapwrapTemplate()
	{
		
	}
	
	GldapwrapTemplate(Map contextSourceConnectionParams)
	{
		contextSource = new LdapContextSource(contextSourceConnectionParams)
		ldapTemplate = new LdapTemplate(contextSource: contextSource)
	}
	
	public void afterPropertiesSet()
	{
		if (!initialised)
		{
			contextSource.afterPropertiesSet()
			ldapTemplate.afterPropertiesSet()
			initialised = true
		}
	}
}