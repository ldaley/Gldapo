package gldapwrap.gynamo;
import gynamo.Gynamo
import gynamo.GynamoPropertyStorage
import javax.naming.directory.SearchControls

class SearchControlsGynamo extends Gynamo
{
	static public final searchControlOptions = [
		"countLimit", "derefLinkFlag", "returningAttributes", 
		"returningObjFlag", "searchScope", "timeLimit"
	]
	
	static getSearchControls = { ->
		SearchControls controls = GynamoPropertyStorage[delegate].searchControls
		if (controls == null)
		{
			return new SearchControls()
		}
		else
		{
			return controls
		}
	}
		
	static setSearchControls << { SearchControls controls -> 
		GynamoPropertyStorage[delegate].searchControls = controls
	}
		
	static mergeClassSearchControlsWithOptions << { Map options ->
		def classControls = delegate.getSearchControls()
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