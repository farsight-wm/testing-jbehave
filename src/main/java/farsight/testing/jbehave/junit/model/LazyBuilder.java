package farsight.testing.jbehave.junit.model;

import org.jbehave.core.model.Scenario;

public abstract class LazyBuilder<T> {
	
	private T builder = null;
	protected abstract T createBuilder();
	
	public T get() {
		if(builder == null)
			builder = createBuilder();
		return builder;
	}
	
	public static class LazyScenarioBuilder extends LazyBuilder<JUnitScenarioBuilder> {

		private final JUnitModelBuilder modelBuilder;
		private final Scenario scenario;
		private final String origin;

		public LazyScenarioBuilder(JUnitModelBuilder modelBuilder, Scenario scenario, String origin) {
			this.modelBuilder = modelBuilder;
			this.scenario = scenario;
			this.origin = origin;
		}

		@Override
		protected JUnitScenarioBuilder createBuilder() {
			return new JUnitScenarioBuilder(modelBuilder, scenario, origin);
		}
	}

}
