package farsight.testing.jbehave.junit.model;

import java.util.List;

import org.jbehave.core.model.Description;
import org.jbehave.core.model.GivenStories;
import org.jbehave.core.model.Lifecycle;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Narrative;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;

public class JUnitStory extends Story {
	
	public JUnitStory(String path, Description description, Meta meta, Narrative narrative, GivenStories givenStories, Lifecycle lifecycle, List<Scenario> scenarios) {
		super(path, description, meta, narrative, givenStories, lifecycle, scenarios);
	}


}
