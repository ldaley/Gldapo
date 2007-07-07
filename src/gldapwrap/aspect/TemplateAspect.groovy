package gldapwrap.aspect;
import org.springframework.ldap.core.LdapOperations
import gldapwrap.util.InjectedClassAttributes
import gldapwrap.exception.GldapwrapNoTemplateException
import gldapwrap.GldapwrapTemplate

class TemplateAspect 
{
	static public void inject(Class clazz)
	{
		clazz.metaClass."static".getLdapTemplate << { 
			return InjectedClassAttributes[delegate, "ldapwrapTemplate"] 
		}
		
		clazz.metaClass."static".setLdapTemplate << { LdapOperations template ->
			InjectedClassAttributes[delegate, "ldapwrapTemplate"] = template 
		}
		
		clazz.metaClass."static".setLdapTemplate << { GldapwrapTemplate template ->
			if (template.initialised == false)
			{
				template.afterPropertiesSet()
			}
			
			InjectedClassAttributes[delegate, "ldapwrapTemplate"] = template.ldapTemplate 
		}
		
		clazz.metaClass."static".getLdapTemplateAssert << {
			LdapOperations template = delegate.getLdapTemplate()
			if (template == null)
			{
				throw new GldapwrapNoTemplateException(delegate)
			}
			return template
		}
	}
}