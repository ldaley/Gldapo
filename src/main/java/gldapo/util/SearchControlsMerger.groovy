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
		if (c1 == null) c1 = new SearchControls()
		if (c2 == null) c2 = new SearchControls()
		
		searchControlOptions.each {
			if (c2."${it}" != null) c1."${it}" = c2."${it}"
		}
		
		return c1
	}
}