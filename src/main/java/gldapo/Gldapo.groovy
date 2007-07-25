package gldapo;
import org.springframework.beans.factory.InitializingBean;

class Gldapo
{
	static final DEFAULT_CONFIG_FILENAME = 'gldapo-conf.groovy'
	static final DEFAULT_CONFIG_ENVIRONMENT = 'production'

	static templateRegistry
	static schemaRegistry

	void initializeFromConfig(ConfigObject config)
	{
		schemaRegistry = GldapoSchemaRegistry.newFromConfig(config)
		templateRegistry = GldapoTemplateRegistry().newFromConfig(config)
		
	}
			
	void initialize()
	{
		initialize(DEFAULT_CONFIG_ENVIRONMENT)
	}
	
	void initialize(String environment)
	{
		initializeFromConfigURL(new File(DEFAULT_CONFIG_FILENAME).toURL(), environment)
	}
	
	void initializeFromConfigURL(URL configUrl)
	{
		initializeFromConfigURL(configUrl, DEFAULT_CONFIG_ENVIRONMENT)
	}
	
	void initializeFromConfigURL(URL configUrl, String environment)
	{
		def slurper
		if (environment)
		{
			slurper = new ConfigSlurper(environment)
		}
		else
		{
			slurper = new ConfigSlurper()
		}
			
		def config = slurper.parse(configUrl)
		initializeFromConfig(config)
	}
}