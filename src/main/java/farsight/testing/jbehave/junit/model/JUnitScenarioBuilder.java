package farsight.testing.jbehave.junit.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbehave.core.annotations.Scope;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.GivenStories;
import org.jbehave.core.model.Lifecycle;
import org.jbehave.core.model.Lifecycle.Steps;

import farsight.testing.jbehave.junit.model.JUnitScenario.StepSource;

import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Scenario;

public class JUnitScenarioBuilder {

	private final String title;
	private final Meta meta;
	private final GivenStories givenStories;
	private final ExamplesTable examplesTable;
	private final List<JUnitStepBuilder> steps;
	private final String origin;

	public JUnitScenarioBuilder(JUnitModelBuilder modelBuilder, Scenario scenario, String origin) {
		title = scenario.getTitle();
		meta = scenario.getMeta();
		givenStories = scenario.getGivenStories();
		examplesTable = scenario.getExamplesTable();
		this.origin = origin;
		steps = JUnitStepBuilder.processSteps(modelBuilder, scenario.getSteps(), origin);
	}

	public String includeName() {
		return origin + " # " + title;
	}



	public JUnitScenario build(DescriptionBuilder descriptionBuilder, Lifecycle lifecycle, int maxDepth) {
		JUnitDescriptions[] descriptions = null;
		
		if(examplesTable != null && examplesTable.getRowCount() > 0) {
			//build example steps
			int exampleCounter = 0;
			descriptions = new JUnitDescriptions[examplesTable.getRowCount()];
			for(Map<String, String> exampleMap: examplesTable.getRows()) {
				JUnitDescriptionsBuilder builder = new JUnitDescriptionsBuilder(descriptionBuilder.addGroup("example " + exampleCounter));
				buildBeforeScenario(builder, lifecycle);
				createDescriptions(builder, exampleMap, maxDepth);
				buildAfterScenario(builder, lifecycle);
				descriptions[exampleCounter++] = builder.build();
			}
			
		} else {
			//build normal steps
			JUnitDescriptionsBuilder builder = new JUnitDescriptionsBuilder(descriptionBuilder);
			buildBeforeScenario(builder, lifecycle);
			createDescriptions(builder, null, maxDepth);
			buildAfterScenario(builder, lifecycle);
			descriptions = new JUnitDescriptions[] { builder.build() };
		}
		
		JUnitScenario scenario = new JUnitScenario(title, meta, givenStories, examplesTable, createSteps(maxDepth),
				descriptions);
		
		return scenario;
	}

	protected void createDescriptions(JUnitDescriptionsBuilder builder, Map<String, String> exampleMap, int maxDepth) {
		if(maxDepth-- < 0)
			throw new JUnitModelException("Maximum include depth reached!");
		builder.phase(StepSource.scenario);
		for (JUnitStepBuilder steps : this.steps)
			steps.buildDescription(builder, exampleMap, maxDepth);
	}

	protected List<String> createSteps(int maxDepth) {
		ArrayList<String> result = new ArrayList<>();
		buildSteps(result, maxDepth);
		return result;
	}

	protected void buildSteps(List<String> into, int maxDepth) {
		if(maxDepth < 0)
			throw new JUnitModelException("Max inclusion depth reached!");
		for (JUnitStepBuilder steps : this.steps)
			steps.build(into, maxDepth);
	}

	public String title() {
		return title;
	}

	private void buildBeforeScenario(JUnitDescriptionsBuilder descriptionsBuilder, Lifecycle lifecycle) {
		if(lifecycle.before.isEmpty())
			return;
		descriptionsBuilder.phase(StepSource.before);
		DescriptionBuilder parent = descriptionsBuilder.addGroup("@BeforeScenario");
		for(String step: lifecycle.getBeforeSteps(Scope.SCENARIO)) {
			descriptionsBuilder.addStep(step);
		}
		descriptionsBuilder.setCurrentGroup(parent);
	}

	private void buildAfterScenario(JUnitDescriptionsBuilder descriptionsBuilder, Lifecycle lifecycle) {
		if(lifecycle.after.isEmpty())
			return;
		descriptionsBuilder.phase(StepSource.after);
		DescriptionBuilder parent = descriptionsBuilder.addGroup("@AfterScenario");
		for(Steps steps: lifecycle.after) {
			for(String step: steps.steps) {
				descriptionsBuilder.addStep(step);
				descriptionsBuilder.add(steps.outcome);
			}
		}
		descriptionsBuilder.setCurrentGroup(parent);		
	}
	

}
