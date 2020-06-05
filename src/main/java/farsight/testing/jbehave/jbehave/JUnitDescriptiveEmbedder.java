package farsight.testing.jbehave.jbehave;

import java.util.ArrayList;
import java.util.List;

import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.failures.BatchFailures;
import org.jbehave.core.model.Story;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.notification.RunNotifier;

import farsight.testing.jbehave.junit.model.DescriptionBuilder;
import farsight.testing.jbehave.junit.model.JUnitModelBuilder;;

public class JUnitDescriptiveEmbedder extends Embedder {

	private List<String> storyPaths;
	private List<Story> stories;
	private Class<?> testClass;
	private String title;
	private boolean isInitialized;
	private Description description = null;
	
	public JUnitDescriptiveEmbedder(List<String> storyPaths, Class<?> testClass, String title) {
		this.storyPaths = storyPaths;
		this.testClass = testClass;
		this.title = title;
	}
	
	private void initialize() {
		if (isInitialized)
			return;

		DescriptionBuilder root = DescriptionBuilder.create(testClass, title);
		stories = new JUnitModelBuilder(configuration()).parseStories(storyPaths, root);
		description = root.build();
		isInitialized = true;
	}	
	
	private List<Story> getStories() {
		initialize();
		return stories;
	}
	
	public Description getDescription() {
		initialize();
		return description;
	}
	
	//rewritten runStoriesAsPath to match JUnit and IDE integration requirements
	//report generation removed. Do we need some kind of report to effectively run stories from jenkins?!
	public void run(final RunNotifier notifier, Filter filter) {
		JUnitStoryRepoter.install(configuration, notifier);
		applyFilter(filter, notifier);
		try {
			//processSystemProperties();
			//embedderMonitor.usingControls(embedderControls());
			
			
			// what is this for?
			BatchFailures failures = new BatchFailures(embedderControls.verboseFailures());

			// run stories
			storyManager().runStories(getStories(), metaFilter(), failures);

			// handle any failures
			handleFailures(failures);

		} finally {
			// shutdown regardless of failures in reports view
			shutdownExecutorService();
			// reset story manager as executor service is shutdown
			storyManager = null;
			
			//do we need reports? what for? what about running in jenkins/ant?
//			generateCrossReference();
//			generateSurefireReport();

		}
	}

	//what does this?
	private void handleFailures(BatchFailures failures) {
		if (failures.size() > 0) {
			if (embedderControls().ignoreFailureInStories()) {
				embedderMonitor.batchFailed(failures);
			} else {
				embedderFailureStrategy().handleFailures(failures);
			}
		}
	}

	private void applyFilter(Filter filter, RunNotifier notifier) {
		if(filter == null)
			return;
		// create meta filter based on titles
		ArrayList<String> metaFilter = new ArrayList<>();
		for(Description storyDesc: description.getChildren()) {
			if(filter.shouldRun(storyDesc)) {
				// story should run, include by path
				metaFilter.add("+path " + storyDesc.getDisplayName());
				for(Description scenarioDesc: storyDesc.getChildren()) {
					if(!filter.shouldRun(scenarioDesc)) {
						//scenario should not run, exclude by title
						//XXX this may exclude scenarios with same title in other stories, but for now it is good enough
						metaFilter.add("-title " + scenarioDesc.getDisplayName());
						notifier.fireTestIgnored(scenarioDesc);
					}
				}
			} else {
				notifier.fireTestIgnored(storyDesc);
			}
		}
		useMetaFilters(metaFilter);
	}


}
