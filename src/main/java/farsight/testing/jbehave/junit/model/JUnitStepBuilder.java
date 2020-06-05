package farsight.testing.jbehave.junit.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import farsight.testing.jbehave.junit.model.LazyBuilder.LazyScenarioBuilder;

public abstract class JUnitStepBuilder {
	
	public static final Pattern EXAMPLE_PATTERN = Pattern.compile("<(.+?)>");
	public static final Pattern INCLUDE_PATTERN = Pattern.compile("!--\\s+include\\s+([^\r\n]*)\\s+from\\s+([^\r\n]*)");


	public static class JUnitSimpleSteps extends JUnitStepBuilder {

		private final List<String> simpleSteps;

		public JUnitSimpleSteps(List<String> simpleSteps) {
			this.simpleSteps = simpleSteps;
		}

		@Override
		public void build(List<String> into, int maxDepth) {
			for (String step : simpleSteps) {
				into.add(step);
			}
		}

		@Override
		public void buildDescription(JUnitDescriptionsBuilder descHolder, Map<String, String> exampleMap,
				int maxDepth) {
			for (String step : simpleSteps) {
				descHolder.addStep(exampleMap == null ? step : parseExmpleStep(step, exampleMap));
			}
		}

		private String parseExmpleStep(String step, Map<String, String> row) {
			Matcher m = EXAMPLE_PATTERN.matcher(step);
			String result = step;
			while (m.find()) {
				String key = m.group(1);
				String replace = row.get(key);
				if (replace != null)
					result = result.replaceFirst("<" + key + ">", replace);
			}

			return result;
		}

	}

	public static class JUnitIncludeSteps extends JUnitStepBuilder {

		private final LazyScenarioBuilder reference;

		public JUnitIncludeSteps(LazyScenarioBuilder reference) {
			this.reference = reference;
		}

		@Override
		public void build(List<String> into, int maxDepth) {
			reference.get().buildSteps(into, maxDepth - 1);
		}

		@Override
		public void buildDescription(JUnitDescriptionsBuilder descHolder, Map<String, String> exampleMap,
				int maxDepth) {
			JUnitScenarioBuilder reference = this.reference.get();
			DescriptionBuilder parent = descHolder.addGroup("@Include: " + reference.includeName());
			reference.createDescriptions(descHolder, exampleMap, maxDepth);
			descHolder.setCurrentGroup(parent);
		}

	}


	// abstract template
	
	public abstract void build(List<String> into, int maxDepth);

	public abstract void buildDescription(JUnitDescriptionsBuilder descHolder, Map<String, String> exampleMap,
	int maxDepth);
	
	
	// static api
	
	public static List<JUnitStepBuilder> processSteps(JUnitModelBuilder modelBuilder, List<String> steps, String origin) {
		ArrayList<JUnitStepBuilder> result = new ArrayList<>();
		ArrayList<String> current = new ArrayList<>();
		for (String step : steps) {
			Matcher matcher = INCLUDE_PATTERN.matcher(step);
			if (matcher.find()) { // include step

				// flush current
				if (!current.isEmpty()) {
					result.add(new JUnitSimpleSteps(current));
					current = new ArrayList<>();
				}

				// process match
				String file = matcher.group(2).trim();
				String[] scenarios = matcher.group(1).split(",|;");
				for(String scenario: scenarios) {
					LazyScenarioBuilder reference = modelBuilder.findReference(origin, file, scenario.trim());
					result.add(new JUnitIncludeSteps(reference));
				}

			} else {
				current.add(step);
			}
		}

		// flush current
		if (!current.isEmpty()) {
			result.add(new JUnitSimpleSteps(current));
		}

		return result;
	}

}
