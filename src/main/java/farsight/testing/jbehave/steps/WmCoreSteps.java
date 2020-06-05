package farsight.testing.jbehave.steps;

import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.AfterStory;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.ScenarioType;

import farsight.testing.jbehave.Scope;
import farsight.testing.jbehave.TestExecutor;
import farsight.testing.jbehave.jbehave.StoryResourceContext;

public class WmCoreSteps {

	private StoryResourceContext resources;

	public WmCoreSteps(StoryResourceContext resources) {
		this.resources = resources;
	}
	
	private TestExecutor testExecutor() {
		return TestExecutor.current();
	}
	
	@BeforeStory
	public void setup() throws Exception {
		testExecutor().getContext().setResources(resources);
		try {
			testExecutor().teardown();
		} catch (Throwable e) {
			e.printStackTrace(); // Ensures output in Eclipse console
			throw e;
		}
	}

	@AfterScenario(uponType=ScenarioType.ANY)
	public void teardown() throws Throwable {
		try {
			TestExecutor testExecutor = testExecutor();
			testExecutor.verify();
			testExecutor.reset(Scope.Scenario);
			testExecutor.teardown();
		} catch (Throwable e) {
			e.printStackTrace(); // Ensures output in Eclipse console
			throw e;
		}
	}

	@AfterStory
	public void terminateSession() {
		testExecutor().getContext().setResources(resources);
		try {
			testExecutor().terminate();
		} catch (Throwable e) {
			e.printStackTrace(); // Ensures output in Eclipse console
			throw e;
		}
	}
	



}
