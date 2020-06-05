package farsight.testing.jbehave.junit.model;

import java.util.List;

import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.GivenStories;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Scenario;

public class JUnitScenario extends Scenario {
	
	public static enum StepSource {
		before, scenario, after
	}
	
	public final JUnitDescriptions[] descriptions;

	public JUnitScenario(String title, Meta meta, GivenStories givenStories, ExamplesTable examplesTable,
			List<String> steps, JUnitDescriptions[] descriptions) {
		super(title, meta, givenStories, examplesTable, steps);
		this.descriptions = descriptions;
	}

}
