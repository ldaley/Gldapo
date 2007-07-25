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
}

class RegistryTestSchema1 {}
class RegistryTestSchema2 {}
class RegistryTestSchema3 {}