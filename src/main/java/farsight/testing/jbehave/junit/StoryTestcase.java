package farsight.testing.jbehave.junit;

import static org.junit.Assume.assumeThat;

import org.hamcrest.core.IsNull;
import org.jbehave.core.ConfigurableEmbedder;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.wm.app.b2b.client.ServiceException;

import farsight.testing.jbehave.TestExecutor;

@RunWith(StoryTestcaseRunner.class)
public abstract class StoryTestcase extends ConfigurableEmbedder {
	
	@Override
	public void run() {
		//not used
	}

	@BeforeClass
	public static void setup() throws ServiceException {
		assumeThat(System.getProperty("skipStories"), IsNull.nullValue());
		boolean connected = TestExecutor.current().testConnection(); // throws AssertionError if unable to connect
		if (!connected){	// Should never happen without an exception being thrown but for sake of completeness
			throw new IllegalStateException("In testing connection to the server, isConnected came back false");
		}
	}
	
	public TestcaseSetup getSetup() {
		 return getSetup(getClass());
	}
	
	public static TestcaseSetup getSetup(Class<?> clazz) {
		return TestcaseSetup.getFor(clazz);
	}
}
