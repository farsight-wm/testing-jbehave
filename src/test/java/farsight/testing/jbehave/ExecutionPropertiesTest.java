package farsight.testing.jbehave;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import farsight.testing.jbehave.jbehave.ExecutionProperties;

public class ExecutionPropertiesTest {
	
	@Test
	public void shouldLoadWithNoPropertyFile() {
		System.clearProperty("wm.config.filename");
		System.clearProperty("wm.server.hostname");
		
		ExecutionProperties props = new ExecutionProperties();
		assertEquals(null, props.get("wm.server.hostname"));
	}
	
	@Test
	public void shouldLoadWhenNoPropertyFilePresent() {
		System.setProperty("wm.config.filename", "foo");
		System.clearProperty("wmaopkey");

		ExecutionProperties props = new ExecutionProperties();
		assertEquals(null, props.get("wm.server.hostname"));
	}

	@Test
	public void shouldLoadPropertiesWithoutEncryption() {
		System.setProperty("wm.config.filename", "junittest.properties");
		System.clearProperty("wmaopkey");

		ExecutionProperties props = new ExecutionProperties();
		assertEquals("myserver", props.get("wm.server.hostname"));
		assertTrue(((String)props.get("wm.server.password")).startsWith("ENC("));
	}

	@Test
	public void shouldDecryptProperties() {
		System.setProperty("wm.config.filename", "junittest.properties");
		System.setProperty("wmaopkey", "4pPVtN3gCNHe");
		
		ExecutionProperties props = new ExecutionProperties();
		assertEquals("EncodedPropertyPassword", props.get("wm.server.password"));
	}
	
	@Test
	public void shouldOverridePropertyFileValues() {
		System.setProperty("wm.config.filename", "junittest.properties");
		System.setProperty("wm.server.hostname", "foobar");
		System.clearProperty("wmaopkey");

		ExecutionProperties props = new ExecutionProperties();
		assertEquals("foobar", props.get("wm.server.hostname"));
	}
}
