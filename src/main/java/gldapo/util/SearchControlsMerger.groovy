package gldapo.util;
import javax.naming.directory.SearchControls

class SearchControlsMerger 
{
	static public final searchControlOptions = [
		"countLimit", "derefLinkFlag", "returningAttributes", 
		"returningObjFlag", "searchScope", "timeLimit"
	]
	
	static SearchControls merge(c1, c2)
	{
		def controls = new SearchControls()
		if (c1 == null && c2 == null) return controls 
		if (c1 == null) c1 = [:]
		if (c2 == null) c2 = [:]
		
		searchControlOptions.each {
			if (c2.containsKey(it))
			{
				controls."${it}" = c2[it]
			}
			else if (c1.containsKey(it))
			{
				controls."${it}" = c1[it]
			}
		}
		
		return controls
	}
}