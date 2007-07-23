package gldapwrap.gynamo;
import org.springframework.ldap.core.LdapOperations
import gldapwrap.exception.GldapwrapNoTemplateException
import gldapwrap.GldapwrapTemplate
import gynamo.Gynamo
import gynamo.GynamoPropertyStorage

class TemplateGynamo extends Gynamo
{
	static getLdapTemplate = { -> 
		return GynamoPropertyStorage[delegate].ldapTemplate 
	}
		
	static setLdapTemplate = { LdapOperations template ->
		GynamoPropertyStorage[delegate].ldapTemplate = template 
	}
		
	static setGldapwrapTemplate = { GldapwrapTemplate template ->
		if (!template.initialised) template.afterPropertiesSet()
		delegate.setLdapTemplate(template.ldapTemplate)
	}
		
	static getLdapTemplateAssert = { ->
		LdapOperations template = delegate.getLdapTemplate()
		if (template == null) throw new GldapwrapNoTemplateException(delegate)
		return template
	}
}