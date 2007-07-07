package gldapwrap.aspect;
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import javax.naming.directory.SearchControls
import gldapwrap.util.InjectedClassAttributes
import gldapwrap.exception.GldapwrapInjectionException

class SearchControlsAspect
{
	static searchControlOptions = [
		"countLimit", "derefLinkFlag", "returningAttributes", 
		"returningObjFlag", "searchScope", "timeLimit"
	]
	
	static public void inject(Class clazz)
	{
		clazz.metaClass."static".getSearchControls << { 
			SearchControls controls = InjectedClassAttributes[clazz, "searchControls"]
			if (controls == null)
			{
				return new SearchControls()
			}
			else
			{
				return controls
			}
		}
		
		clazz.metaClass."static".setSearchControls << { SearchControls controls -> 
			InjectedClassAttributes[clazz, "searchControls"] = controls
		}
		
		clazz.metaClass."static".mergeClassSearchControlsWithOptions << { Map options ->
			def classControls = clazz.getSearchControls()
			if (options != null)
			{
				searchControlOptions.each {
					if (options[it] != null)
					{
						classControls."${it}" = options[it]
					}
				}
			}
			
			return classControls
		}
	}	
}