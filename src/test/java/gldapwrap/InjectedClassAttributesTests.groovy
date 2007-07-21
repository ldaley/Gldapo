import gldapwrap.util.InjectedClassAttributes

class InjectedClassAttributesTests extends GroovyTestCase {
	void testNotSet() {
		assertNull(InjectedClassAttributes[String, "notset"])
	}
	void testSetThenGet()
	{
		InjectedClassAttributes[String, "blah"] = "blah"
		assertEquals("blah", InjectedClassAttributes[String, "blah"])
	}
}