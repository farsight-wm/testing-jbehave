package farsight.testing.jbehave.junit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.steps.AbstractStepsFactory;

public class StepsFactory extends AbstractStepsFactory {
	
	public static interface StepsProvider<T> {
		T createSteps() throws Exception;
		Class<T> getStepClass();
	}
	
	public static class SingltonStepsProvider<T> implements StepsProvider<T> {
		
		final T instance;
		
		public SingltonStepsProvider(T singltonInstance) {
			instance = singltonInstance;
		}

		@Override
		public T createSteps() {
			return instance;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Class<T> getStepClass() {
			return (Class<T>) instance.getClass();
		}
	}
	
	public static class LazySingltonStepsProvider<T> implements StepsProvider<T> {
		
		final Class<T> type;
		private T instance = null;
		
		public LazySingltonStepsProvider(Class<T> type) {
			this.type = type;
		}

		@Override
		public T createSteps() throws Exception {
			if(instance == null)
				instance = type.newInstance();
			return instance;
		}

		@Override
		public Class<T> getStepClass() {
			return type;
		}
	}
	
	public static class NonSingltonStepsProvider<T> implements StepsProvider<T> {
		final Class<T> type;
		
		public NonSingltonStepsProvider(Class<T> type) {
			this.type = type;
		}

		@Override
		public T createSteps() throws Exception {
			return type.newInstance();
		}

		@Override
		public Class<T> getStepClass() {
			return type;
		}
	}	
	
	private HashMap<Class<?>, StepsProvider<?>> providers = new HashMap<>();

	
	public <T> void registerSingltonSteps(Class<T> type) {
		providers.put(type, new LazySingltonStepsProvider<T>(type));
	}
	
	public <T> void registerSingltonSteps(T type) {
		providers.put(type.getClass(), new SingltonStepsProvider<T>(type));
	}
	
	public <T> void registerNonSingltonSteps(Class<T> type) {
		providers.put(type.getClass(), new NonSingltonStepsProvider<T>(type));
	}
	
	public <T> void registerStepsProvider(StepsProvider<T> stepsProvider) {
		providers.put(stepsProvider.getStepClass(), stepsProvider);
	}
	
	
	public StepsFactory(Configuration configuration) {
		super(configuration);
	}

	@Override
	public Object createInstanceOfType(Class<?> type) {
		StepsProvider<?> provider = providers.get(type);
		try {
			return provider == null ? null : provider.createSteps();
		} catch (Exception e) {
			throw new RuntimeException("Could not create instance for StepsClass: " + type);
		}
	}

	@Override
	protected List<Class<?>> stepsTypes() {
		return new ArrayList<>(providers.keySet());
	}

}
