package farsight.testing.jbehave.utils;

import java.util.List;
import java.util.Map;

import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.GivenStories;
import org.jbehave.core.model.Lifecycle;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Narrative;
import org.jbehave.core.model.OutcomesTable;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.model.StoryDuration;
import org.jbehave.core.reporters.NullStoryReporter;
import org.jbehave.core.reporters.StoryReporter;

public class SysErrStoryReporter implements StoryReporter {
	
	private final StoryReporter proxy;

	public SysErrStoryReporter(StoryReporter proxy) {
		this.proxy = proxy == null ? new NullStoryReporter() : proxy;
	}
	

	@Override
	public void beforeStep(String step) {
		System.err.println("beforeStep " + step);
		proxy.beforeStep(step);
	}

	@Override
	public void successful(String step) {
		System.err.println("successful " + step);
		proxy.successful(step);
	}

	@Override
	public void ignorable(String step) {
		System.err.println("ignorable " + step);
		proxy.ignorable(step);
	}

	@Override
	public void comment(String step) {
		System.err.println("comment " + step);
		proxy.comment(step);
	}

	@Override
	public void pending(String step) {
		System.err.println("pending " + step);
		proxy.pending(step);
	}

	@Override
	public void notPerformed(String step) {
		System.err.println("notPerformed " + step);
		proxy.notPerformed(step);
	}

	@Override
	public void failed(String step, Throwable cause) {
		System.err.println("failed " + step);
		proxy.failed(step, cause);
	}

	@Override
	public void failedOutcomes(String step, OutcomesTable table) {
		System.err.println("failedOutcomes " + step);
		proxy.failedOutcomes(step, table);
	}

	@Override
	public void storyNotAllowed(Story story, String filter) {
		System.err.println("storyNotAllowed " + story.getName() + " " + story.getPath());
		proxy.storyNotAllowed(story, filter);
	}

	@Override
	public void beforeStory(Story story, boolean givenStory) {
		System.err.println("beforeStory " + story.getName() + " " + story.getPath());
		proxy.beforeStory(story, givenStory);
	}

	@Override
	public void storyCancelled(Story story, StoryDuration storyDuration) {
		System.err.println("storyCancelled " + story.getName() + " " + story.getPath());
		proxy.storyCancelled(story, storyDuration);
	}

	@Override
	public void afterStory(boolean givenStory) {
		System.err.println("afterStory");
		proxy.afterStory(givenStory);
	}

	@Override
	public void narrative(final Narrative narrative) {
		System.err.println("narrative " + narrative);
		proxy.narrative(narrative);
	}

	@Override
	public void lifecyle(Lifecycle lifecycle) {
		System.err.println("lifecyle " + lifecycle);
		proxy.lifecyle(lifecycle);
	}

	@Override
	public void beforeGivenStories() {
		System.err.println("beforeGivenStories");
		proxy.beforeGivenStories();
	}

	@Override
	public void givenStories(GivenStories givenStories) {
		System.err.println("givenStories " + givenStories);
		proxy.givenStories(givenStories);
	}

	@Override
	public void givenStories(List<String> storyPaths) {
		System.err.println("givenStories " + String.join(", ", storyPaths));
		proxy.givenStories(storyPaths);
	}

	@Override
	public void afterGivenStories() {
		System.err.println("afterGivenStories");
		proxy.afterGivenStories();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void beforeScenario(String title) {
		System.err.println("beforeScenario " + title);
		proxy.beforeScenario(title);
	}

	@Override
	public void beforeScenario(Scenario scenario) {
		System.err.println("beforeScenario(Scenario) " + scenario);
		proxy.beforeScenario(scenario);
	}

	@Override
	public void scenarioNotAllowed(Scenario scenario, String filter) {
		System.err.println("scenarioNotAllowed " + scenario);
		proxy.scenarioNotAllowed(scenario, filter);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void scenarioMeta(Meta meta) {
		System.err.println("scenarioMeta " + meta);
		proxy.scenarioMeta(meta);
	}

	@Override
	public void afterScenario() {
		System.err.println("afterScenario");
		proxy.afterScenario();
	}

	@Override
	public void beforeExamples(List<String> steps, ExamplesTable table) {
		System.err.println("beforeExamples");
		proxy.beforeExamples(steps, table);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void example(Map<String, String> tableRow) {
		System.err.println("example + map");
		proxy.example(tableRow);
	}

	@Override
	public void example(Map<String, String> tableRow, int exampleIndex) {
		System.err.println("example + map + " + exampleIndex);
		proxy.example(tableRow, exampleIndex);
	}

	@Override
	public void afterExamples() {
		System.err.println("afterExamples");
		proxy.afterExamples();
	}

	@Override
	public void dryRun() {
		System.err.println("dryRun");
		proxy.dryRun();
	}

	@Override
	public void pendingMethods(List<String> methods) {
		System.err.println("pendingMethods");
		proxy.pendingMethods(methods);
	}

	@Override
	public void restarted(String step, Throwable cause) {
		System.err.println("restarted");
		proxy.restarted(step, cause);
	}

	@Override
	public void restartedStory(Story story, Throwable cause) {
		System.err.println("restartedStory " + story);
		proxy.restartedStory(story, cause);
	}

}
