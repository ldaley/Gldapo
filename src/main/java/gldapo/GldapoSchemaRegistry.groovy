package gldapo;
import gynamo.Gynamo;
import gldapo.schema.gynamo.GldapoSchemaMetaGynamo;

class GldapoSchemaRegistry 
{
	static final CONFIG_SCHEMAS_KEY = 'schemas'
	
	List schemas
	
	List getSchemas()
	{
		if (this.schemas == null) this.schemas = []
		return this.schemas
	}
	
	void setSchemas(List<Class> schemas)
	{
		this.schemas = []
		schemas.each { this << it }
	}
	
	void leftShift(Class schema)
	{
		use (Gynamo) {
			schema.gynamize(GldapoSchemaMetaGynamo)
		}

		this.getSchemas() << schema
	}
	
	static newFromConfig(ConfigObject config)
	{
		def registry = this.newInstance()
		if (config.containsKey(CONFIG_SCHEMAS_KEY))
		{
			config[CONFIG_SCHEMAS_KEY].each { registry << it }
		}

		return registry
	}
}