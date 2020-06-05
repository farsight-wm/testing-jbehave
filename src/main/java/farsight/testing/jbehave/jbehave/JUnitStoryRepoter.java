package farsight.testing.jbehave.jbehave;

import java.util.Map;

import org.jbehave.core.annotations.AfterScenario.Outcome;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.failures.UUIDExceptionWrapper;
import org.jbehave.core.model.Lifecycle;
import org.jbehave.core.model.OutcomesTable;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.reporters.NullStoryReporter;
import org.jbehave.core.reporters.StoryReporter;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import farsight.testing.jbehave.junit.model.JUnitDescriptions;
import farsight.testing.jbehave.junit.model.JUnitScenario;
import farsight.testing.jbehave.junit.model.JUnitScenario.StepSource;
import farsight.testing.jbehave.utils.SysErrStoryReporter;

public class JUnitStoryRepoter extends NullStoryReporter {

	private static class JUnitState {

		private Lifecycle lifecycle = null;
		private JUnitDescriptions[] descriptions = null;
		private Description current = null;
		private int index = 0;
		private StepSource phase;
		private Description[] jUnitDescriptions;
		private Outcome[] outcomes = null;
		private boolean failures = false;
		private int exIdx = 0;
		
		private JUnitState() {
		}
		
		public void startScenario(JUnitDescriptions[] descriptions) {
			exIdx = 0;
			this.descriptions = descriptions;
			setPhase(lifecycle.hasBeforeSteps() ? StepSource.before : StepSource.scenario);
			this.outcomes = descriptions[0].getOutcomes();
			failures = false;
		}
		
		public void setExampleIndex(int exampleIndex) {
			exIdx = exampleIndex;
			setPhase(lifecycle.hasBeforeSteps() ? StepSource.before : StepSource.scenario);
			failures = false;
		}
		
		public boolean dealWithJBheabeBug() {
			//jBehove does not trigger beforeStep-events for steps in After-Lifecycle !!! :(
			return phase == StepSource.after || phase == StepSource.scenario &&	index == jUnitDescriptions.length;
		}
		
		public void scenarioFailed() {
			failures = true;
		}
		
		public boolean runAfterStep() {
			Outcome cur = outcomes[index - 1];
			return cur == Outcome.ANY || cur == (failures ? Outcome.FAILURE : Outcome.SUCCESS);
		}

		public Description next() {
			current = index < jUnitDescriptions.length ? jUnitDescriptions[index++] : null;
			if(current == null) {
				//next phase?
				switch (phase) {
				case before:
					setPhase(StepSource.scenario);
					return next();
				case scenario:
					if(lifecycle.hasAfterSteps()) {
						setPhase(StepSource.after);
						return next();
					}
					return null;
				case after:
					return null;
				}
			}
			return current;
		}
		
		private void setPhase(StepSource phase) {
			this.phase = phase;
			this.index = 0;
			this.current = null;
			this.jUnitDescriptions = descriptions[exIdx].getDescriptions(phase);
		}

		public Description current() {
			return current;
		}
		
		public void notifyRemainingSkipped(RunNotifier notifier) {
			//mark not ran items as skipped (e.g. Lifecycle After with other Outcomings)
			Description desc;
			while((desc = next()) != null)
				notifier.fireTestIgnored(desc);
		}

		public void setLifecycle(Lifecycle lifecycle) {
			this.lifecycle = lifecycle;
		}

	
		


	}

	private final RunNotifier notifier;
	public static boolean DEBUG = false;

	// There may be multiple threads executing Stories
	private final ThreadLocal<JUnitState> threadState = new ThreadLocal<>();

	public JUnitStoryRepoter(RunNotifier notifier) {
		this.notifier = notifier;
	}

	public static void install(Configuration configuration, RunNotifier notifier) {
		StoryReporter reporter = new JUnitStoryRepoter(notifier);
		if (DEBUG) {
			reporter = new SysErrStoryReporter(reporter);
		}
		configuration.storyReporterBuilder().withReporters(reporter);
	}

	// shortcut
	private JUnitState state() {
		return threadState.get();
	}

	// Story scope
	
	@Override
	public void beforeStory(Story story, boolean givenStory) {
		threadState.set(new JUnitState());
	}
	
	@Override
	public void afterStory(boolean givenStory) {
		threadState.remove();
	}
	
	@Override
	public void lifecyle(Lifecycle lifecycle) {
		state().setLifecycle(lifecycle);
	}
	
	// Scenario scope

	@Override
	public void beforeScenario(Scenario scenario) {
		state().startScenario(((JUnitScenario) scenario).descriptions);
	}

	@Override
	public void afterScenario() {
		state().notifyRemainingSkipped(notifier);
	}
	
	
	@Override
	public void example(Map<String, String> tableRow, int exampleIndex) {
		state().setExampleIndex(exampleIndex);
	}
	
	// Step scope

	private void dealWithJBehaveBug(JUnitState state) {
		//FIXes jBehave issiue, that "beforeStep" is not called for Steps in After: Scope!
		if(state.dealWithJBheabeBug()) {
			Description next = null;
			while ((next = state.next()) != null && !state.runAfterStep()) {
				notifier.fireTestIgnored(next);
			}
			if(next != null) {
				notifier.fireTestStarted(next);
			}
		}
	}
	
	@Override
	public void beforeStep(String step) {
		JUnitState state = state();
		notifier.fireTestStarted(state.next());
	}

	@Override
	public void successful(String step) {
		JUnitState state = state();
		notifier.fireTestFinished(state.current());
		
		dealWithJBehaveBug(state);
	}

	@Override
	public void ignorable(String step) {
		JUnitState state = state();
		notifier.fireTestIgnored(state.next());
	}

	@Override
	public void comment(String step) {
		JUnitState state = state();
		notifier.fireTestIgnored(state.next());
	}

	@Override
	public void pending(String step) {
		JUnitState state = state();
		Description desc = state.next();
		notifier.fireTestFailure(new Failure(desc,
				new RuntimeException("Step is pending, please check if it is correctly spelled!")));
	}

	@Override
	public void notPerformed(String step) {
		JUnitState state = state();
		notifier.fireTestIgnored(state.next());
	}

	@Override
	public void failed(String step, Throwable cause) {
		JUnitState state = state();
		if (cause instanceof UUIDExceptionWrapper)
			cause = cause.getCause();
		
		state.scenarioFailed();
		notifier.fireTestFailure(new Failure(state.current(), cause));

		//FIXes jBehave issiue, that "beforeStep" is not called for Steps in After: Scope!
		dealWithJBehaveBug(state);
	}

	@Override
	public void failedOutcomes(String step, OutcomesTable table) {
		System.err.println("failedOutcomes " + step);
	}




}
