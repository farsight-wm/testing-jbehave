package farsight.testing.jbehave.junit.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.jbehave.core.model.Description;
import org.jbehave.core.model.GivenStories;
import org.jbehave.core.model.Lifecycle;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Narrative;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;

import farsight.testing.jbehave.junit.model.LazyBuilder.LazyScenarioBuilder;

public class JUnitStoryBuilder {

	private final String path;
	private final Description description;
	private final Narrative narrative;
	private final Meta meta;
	private final GivenStories givenStories;
	private final Lifecycle lifecycle;
	private final LinkedHashMap<String, LazyScenarioBuilder> scenarios;

	public JUnitStoryBuilder(JUnitModelBuilder modelBuilder, Story story) {
		path = story.getPath();
		description = story.getDescription();
		narrative = story.getNarrative();
		meta = story.getMeta();
		givenStories = story.getGivenStories();
		lifecycle = processLifecycle(modelBuilder, story.getLifecycle(), story.getPath());
		scenarios = processScenarios(modelBuilder, story.getScenarios(), story.getPath());
	}

	private LinkedHashMap<String, LazyScenarioBuilder> processScenarios(JUnitModelBuilder modelBuilder,
			List<Scenario> scenarios, String origin) {
		LinkedHashMap<String, LazyScenarioBuilder> result = new LinkedHashMap<>();
		for (Scenario scenario : scenarios) {
			String key = scenario.getTitle();
			if (result.containsKey(key))
				continue; // use only first story of a name
			result.put(key, new LazyScenarioBuilder(modelBuilder, scenario, origin));
		}
		return result;
	}
	
	private Lifecycle processLifecycle(JUnitModelBuilder modelBuilder, Lifecycle lifecycle, String path) {
		if(lifecycle.isEmpty() || (lifecycle.before.size() == 0 && lifecycle.after.size() == 0))
			return Lifecycle.EMPTY;
		return lifecycle;
	}

	public JUnitStory build(DescriptionBuilder builder, int maxDepth) {
		return new JUnitStory(path, description, meta, narrative, givenStories, lifecycle,
				buildScenarios(builder.addGroup(path), maxDepth));
	}

	private List<Scenario> buildScenarios(DescriptionBuilder descriptionBuilder, int maxDepth) {
		ArrayList<Scenario> result = new ArrayList<>(scenarios.size());
		for(LazyScenarioBuilder lazyBuilder: scenarios.values()) {
			JUnitScenarioBuilder scenarioBuilder = lazyBuilder.get();
			if(!scenarioBuilder.title().startsWith("@"))
				result.add(scenarioBuilder.build(descriptionBuilder.addGroup(scenarioBuilder.title()), lifecycle, maxDepth));
		}
		return result;
	}

	public LazyScenarioBuilder getScenarioBuilder(String scenario) {
		LazyScenarioBuilder lazyBuilder = scenarios.get(scenario);
		if(lazyBuilder == null) 
			throw new JUnitModelException("Scenario " + scenario + " not found in " + path);
		return lazyBuilder;
	}

}
