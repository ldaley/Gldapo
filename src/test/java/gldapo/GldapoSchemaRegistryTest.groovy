package gldapo;
import gynamo.Gynamo
import gldapo.schema.gynamo.GldapoSchemaMetaGynamo

class GldapoSchemaRegistryTest extends GroovyTestCase 
{
	void testLeftShift() 
	{
		def registry = new GldapoSchemaRegistry()
		registry << RegistryTestSchema1
		assertEquals([RegistryTestSchema1], registry.schemas)
		assertEquals(true, Gynamo.isGynamized(RegistryTestSchema1, GldapoSchemaMetaGynamo))
	}
	
	void testSetSchemas()
	{
		def registry = new GldapoSchemaRegistry()
		registry.schemas = [RegistryTestSchema2, RegistryTestSchema3]

		assertEquals([RegistryTestSchema2, RegistryTestSchema3], registry.schemas)
		assertEquals(true, Gynamo.isGynamized(RegistryTestSchema2, GldapoSchemaMetaGynamo))
		assertEquals(true, Gynamo.isGynamized(RegistryTestSchema3, GldapoSchemaMetaGynamo))
	}
	
	void testNewFromConfig()
	{
		def c = new ConfigObject()
		c.schemas = [RegistryTestSchema4, RegistryTestSchema5]
		def r = GldapoSchemaRegistry.newFromConfig(c)
		assert(r instanceof GldapoSchemaRegistry)
		assert(Gynamo.isGynamized(RegistryTestSchema4, GldapoSchemaMetaGynamo))
		assert(Gynamo.isGynamized(RegistryTestSchema5, GldapoSchemaMetaGynamo))
		assertEquals([RegistryTestSchema4, RegistryTestSchema5], r.schemas)
	}
}

class RegistryTestSchema1 {}
class RegistryTestSchema2 {}
class RegistryTestSchema3 {}
class RegistryTestSchema4 {}
class RegistryTestSchema5 {}


