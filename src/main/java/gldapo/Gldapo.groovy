package gldapo;
import gynamo.Gynamo;
import gldapo.gynamo.GldapoSchemaMetaGynamo;
import org.springframework.beans.factory.InitializingBean;

class Gldapo
{
	static final DEFAULT_CONFIG_FILENAME = 'gldapo-conf.groovy'
	static final DEFAULT_CONFIG_ENVIRONMENT = 'production'
	static final CONFIG_TEMPLATES_KEY = 'templates'
	static final CONFIG_DEFAULT_TEMPLATE_KEY = 'defaultTemplate'
	static final CONFIG_SCHEMAS_KEY = 'schemas'

	private static Gldapo instance

	Map<GldapoTemplate> template
	List<Class> schemas = []
	GldapoDirectory defaultTemplate
	
	private Gldapo()
	{
		
	}

	static Gldapo getInstance() 
	{ 
		if (instance == null) instance = new Gldapo()
		return instance
	}

	void setSchemas(List schemas)
	{
		this.schemas = null
		schemas.each { registerSchema(it) }
	}
	
	void registerSchema(Class schema)
	{
		use (Gynamo) {
			schema.gynamize(GldapoSchemaMetaGynamo);
		}
		
		this.schemas << schema
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
		initializeFromConfigObject(config)
	}
	
	void initializeFromConfigObject(ConfigObject config)
	{
		initializeDirectoriesFromConfig(config)
		initializeSchemasFromConfig(config)
		initializeDefaultDirectoryFromConfig(config)
	}
		
	void initializeDirectoriesFromConfig(ConfigObject config)
	{
		config[CONFIG_TEMPLATES_KEY]?.each { def templateName, def templateConfig -> 
			this.templates[templateName] = GldapoTemplate.templateFromConfig(templateConfig)
		} 
	}

	void initializeSchemasFromConfig(ConfigObject config)
	{
		this.schemas = config[CONFIG_SCHEMAS_KEY]
	}

	void initializeDefaultTemplateFromConfig(ConfigObject config)
	{
		def defaultTemplate = config[CONFIG_SCHEMAS_KEY]
	}
}