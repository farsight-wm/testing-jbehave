package farsight.testing.jbehave;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import farsight.testing.jbehave.jbehave.StoryResourceContext;

public class StoryResourceContextTest {

	private final StoryResourceContext resourceContext = new StoryResourceContext(null);

	@Test
	public void shouldLoadIdata() throws IOException {
		String str = resourceContext.getAsString("data/applepear.xml");
		assertNotNull(str);
	}

	@Test
	public void shouldRetrieveMultipleResourceWithImplicitPath() {
		String s = StringUtils.join(resourceContext.getAsString(Arrays.asList("data/nulldata.xml", "lorem.xml")).toArray());
		assertTrue(s.contains("ipsum"));
		assertTrue(s.contains("nulfield"));
	}
	
	@Test
	public void shouldRetrieveMultipleResourceWithExplicitPath() {
		String s = StringUtils.join(resourceContext.getAsString(Arrays.asList("data/nulldata.xml", "data/lorem.xml")).toArray());
		assertTrue(s.contains("ipsum"));
		assertTrue(s.contains("nulfield"));
	
	}
}
