package farsight.testing.jbehave.junit;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TestcaseSetup {

	public static final String DEFAULT_STORY_PATTERN = "**/*.story";

	public static class Builder {
		private ArrayList<String> packages = new ArrayList<>();
		private String storyPattern = DEFAULT_STORY_PATTERN;
		@SuppressWarnings("rawtypes")
		private ArrayList<Class> additionalSteps = new ArrayList<>();

		public static Builder create() {
			return new Builder();
		}

		public Builder withStoryPattern(String storyPattern) {
			this.storyPattern = storyPattern;
			return this;
		}

		public Builder withPackage(String packageName) {
			packages.add(packageName);
			return this;
		}

		public Builder withPackages(Collection<String> packageNames) {
			packages.addAll(packageNames);
			return this;
		}

		public Builder withAdditionalSteps(Class<?> additionalStepsClass) {
			this.additionalSteps.add(additionalStepsClass);
			return this;
		}

		public Builder withAdditionalSteps(Collection<Class<?>> additionalStepsClasses) {
			this.additionalSteps.addAll(additionalStepsClasses);
			return this;
		}

		@SuppressWarnings("unchecked")
		private <T> T[] asArray(Class<T> clazz, List<T> list) {
			return list.toArray((T[]) Array.newInstance(clazz, list.size()));
		}

		@SuppressWarnings("rawtypes")
		public TestcaseSetup build() {
			return new TestcaseSetup(asArray(String.class, packages), storyPattern,
					asArray(Class.class, (List<Class>) additionalSteps));
		}

	}

	public final String[] packages;
	public final String storyPattern;
	public final Class<?>[] additionalSteps;

	private TestcaseSetup(String[] packages, String storyPattern, Class<?>[] additionalSteps) {
		if (packages == null || packages.length == 0)
			throw new IllegalArgumentException("At least one package is required");
		this.packages = packages;
		this.storyPattern = storyPattern;
		this.additionalSteps = additionalSteps;
	}

	public boolean isMultiPackage() {
		return packages.length > 1;
	}

	public String getPackage() {
		return packages[0];
	}

	public String[] getPackageList() {
		return packages;
	}

	public String getStoryPattern() {
		return storyPattern;
	}

	public String toString() {
		return "TestCaseSetup(packages=" + String.join(",", packages)
				+ (storyPattern == null ? "" : " storyPattern=" + storyPattern) + ")";
	}

	public static TestcaseSetup getFor(Class<?> clazz) {
		ISPackage isPackage = clazz.getAnnotation(ISPackage.class);
		ISPackages isPackages = clazz.getAnnotation(ISPackages.class);

		if (isPackage != null && isPackages != null) {
			throw new AssertionError("Classes must be annotated by only one of @ISPackage, @ISPackages");
		}

		Builder builder = Builder.create();

		if (isPackage != null) {
			builder.withPackage(isPackage.value()).withStoryPattern(isPackage.story());

		} else if (isPackages != null) {
			builder.withPackages(Arrays.asList(isPackages.value())).withStoryPattern(isPackages.story());
			
		} else {
			builder.withPackage(clazz.getSimpleName());
		}
		
		AdditionalSteps additionalSteps = clazz.getAnnotation(AdditionalSteps.class);
		if(additionalSteps != null) {
			builder.withAdditionalSteps(Arrays.asList(additionalSteps.value()));
		}

		return builder.build();
	}

}