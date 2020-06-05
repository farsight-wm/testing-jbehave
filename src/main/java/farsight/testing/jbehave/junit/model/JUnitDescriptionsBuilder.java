package farsight.testing.jbehave.junit.model;

import java.util.ArrayList;
import java.util.EnumMap;

import org.jbehave.core.annotations.AfterScenario.Outcome;
import org.junit.runner.Description;

import farsight.testing.jbehave.junit.model.JUnitScenario.StepSource;

public class JUnitDescriptionsBuilder {
	
	private EnumMap<StepSource, ArrayList<Description>> map = new EnumMap<>(StepSource.class);
	private ArrayList<Outcome> outcomes = new ArrayList<>();
	
	private StepSource phase = StepSource.scenario;
	private DescriptionBuilder currentBuilder = null;
	
	public JUnitDescriptionsBuilder(DescriptionBuilder builder) {
		for(StepSource phase: StepSource.values())
			map.put(phase, new ArrayList<>());
		currentBuilder = builder;
	}
	
	public void addStep(String step) {
		Description desc = currentBuilder.append(DescriptionBuilder.junitSaveName(step));
		map.get(phase).add(desc);
	}
	
	public DescriptionBuilder addGroup(String name) {
		DescriptionBuilder parent = currentBuilder;
		currentBuilder = currentBuilder.addGroup(name);
		return parent;
	}
	
	public void setCurrentGroup(DescriptionBuilder parent) {
		this.currentBuilder = parent;
	}

	public JUnitDescriptions build() {
		EnumMap<StepSource, Description[]> result = new EnumMap<>(StepSource.class);
		for(StepSource phase: StepSource.values()) {
			ArrayList<Description> list = map.get(phase);
			result.put(phase, list.toArray(new Description[list.size()]));
		}
		return new JUnitDescriptions(result, outcomes.toArray(new Outcome[outcomes.size()]));
	}
	
	public void phase(StepSource phase) {
		this.phase = phase;
	}
	
	public void add(Description description) {
		map.get(phase).add(description);
	}
	
	public void add(Outcome outcome) {
		outcomes.add(outcome);
	}
	
}