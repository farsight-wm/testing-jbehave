package farsight.testing.jbehave.junit.model;

import java.util.EnumMap;

import org.jbehave.core.annotations.AfterScenario.Outcome;
import org.junit.runner.Description;

import farsight.testing.jbehave.junit.model.JUnitScenario.StepSource;

public class JUnitDescriptions {
	
	private final EnumMap<StepSource, Description[]> descriptions;
	private final Outcome[] outcomes;
	
	public JUnitDescriptions(EnumMap<StepSource, Description[]> descriptions, Outcome[] outcomes) {
		this.descriptions = descriptions;
		this.outcomes = outcomes;
	}
	
	public int size(StepSource phase) {
		return descriptions.get(phase).length;
	}
	
	public Description get(StepSource phase, int index) {
		Description[] array = descriptions.get(phase);
		if(index >= 0 && index < array.length)
			return array[index];
		return null;
	}

	public Description[] getDescriptions(StepSource phase) {
		return descriptions.get(phase);
	}
	
	public Outcome[] getOutcomes() {
		return outcomes;
	}
	
}