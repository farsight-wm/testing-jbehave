package farsight.testing.jbehave.junit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.io.StoryLoader;
import org.jbehave.core.reporters.NullStoryReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.RunNotifier;

import farsight.testing.jbehave.jbehave.JUnitDescriptiveEmbedder;
import farsight.testing.jbehave.jbehave.StoryResourceContext;
import farsight.testing.jbehave.steps.WmBehaveStepTypes;
import farsight.testing.jbehave.steps.WmCoreSteps;

public class StoryTestcaseRunner extends Runner implements Filterable {

	private static final ConfigProperties properties = ConfigProperties.instance();
	private static final String PS = "/";
	private static final String EXCLUDE_STORY_PATTERN = "**/*.inc.story";
	
	private static final String[] includePaths = getIncludePaths();
	
	private static String readFileAsString(Path path) {
		try {
			return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Cannot load file: " + path, e);
		}
	}

	private static class RunnerSetup {
		private final Description description;
		private JUnitDescriptiveEmbedder embedder;

		private RunnerSetup(StoryTestcase testInstance, String packageName, TestcaseSetup setup)
				throws InstantiationException, IllegalAccessException {

			final String basePath = properties.get("package.base");
			final String testPath = basePath + PS + packageName + PS + properties.get("package.testPath", "test");

			List<String> stories = new StoryFinder().findPaths(new File(testPath).getAbsolutePath(), setup.storyPattern,
					EXCLUDE_STORY_PATTERN);

			embedder = new JUnitDescriptiveEmbedder(stories, testInstance.getClass(), packageName);
			
			final Configuration configuration = createConfiguration(new StoryLoader() {
				@Override
				public String loadResourceAsText(String resourcePath) {

					if (resourcePath.startsWith("@")) {
						// search include paths
						String resource = resourcePath.substring(1);
						for (String base : includePaths) {
							Path path = Paths.get(base, resource);
							if (Files.isRegularFile(path) && Files.isReadable(path))
								return readFileAsString(path); 
						}
						throw new RuntimeException("Could not find file " + resource + " in include paths.");
					} else {
						return readFileAsString(Paths.get(testPath, resourcePath));
					}
				}

				@Override
				public String loadStoryAsText(String storyPath) {
					return loadResourceAsText(storyPath);
				}
			});
			
			testInstance.useConfiguration(configuration);
			StepsFactory stepsFactory = new StepsFactory(testInstance.configuration());
			stepsFactory.registerSingltonSteps(new WmCoreSteps(new StoryResourceContext(configuration)));
			stepsFactory.registerSingltonSteps(new WmBehaveStepTypes());
			
			for(Class<?> stepsClass: setup.additionalSteps)
				stepsFactory.registerSingltonSteps(stepsClass);
			
			testInstance.useStepsFactory(stepsFactory);
			testInstance.useEmbedder(embedder); // replace original embedder
			testInstance.configuredEmbedder(); // configure embedder

			embedder.embedderControls().doIgnoreFailureInView(true).doIgnoreFailureInStories(true).useThreads(1);
			
			embedder.useMetaFilters(Arrays.asList("-ignore"));
			
			this.description = embedder.getDescription();

		}

		private static Configuration createConfiguration(StoryLoader storyLoader) {
			Configuration config = new MostUsefulConfiguration();
			config.usePendingStepStrategy(new FailingUponPendingStep());
			config.useStoryReporterBuilder(new StoryReporterBuilder().withReporters(new NullStoryReporter()));
			config.useStoryLoader(storyLoader);
			return config;
		}

		public void run(final RunNotifier notifier, Filter filter) {
			embedder.run(notifier, filter);
		}

	}

	private RunnerSetup[] runSetups;
	private Description rootDescription;
	private Filter filter;

	public StoryTestcaseRunner(Class<? extends StoryTestcase> testClass)
			throws InstantiationException, IllegalAccessException {
		TestcaseSetup setup = TestcaseSetup.getFor(testClass);
		rootDescription = Description.createSuiteDescription(testClass);
		runSetups = new RunnerSetup[setup.packages.length];
		for (int i = 0; i < runSetups.length; i++) {
			runSetups[i] = new RunnerSetup(testClass.newInstance(), setup.packages[i], setup);
			rootDescription.addChild(runSetups[i].description);
		}
	}

	public static String[] getIncludePaths() {
		String includes = properties.get("package.includes"), base = properties.get("package.base"),
				test = properties.get("package.testPath", "test");
		
		if(includes == null)
			return new String[0];
		
		
		//for now, only allow other packages to be include packages...
		String[] paths = includes.split(",|;");
		for(int i = 0; i < paths.length; i++)
			paths[i] = base + PS + paths[i].trim() + PS + test;
		
		return paths;
	}

	@Override
	public Description getDescription() {
		return rootDescription;
	}

	@Override
	public void run(final RunNotifier notifier) {
		for (RunnerSetup setup : runSetups) {
			if (filter == null || filter.shouldRun(setup.description))
				setup.run(notifier, filter);
		}
	}

	@Override
	public void filter(Filter filter) throws NoTestsRemainException {
		this.filter = filter;
	}

}
