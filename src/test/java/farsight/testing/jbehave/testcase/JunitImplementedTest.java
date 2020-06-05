package farsight.testing.jbehave.testcase;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.wm.app.b2b.client.ServiceException;
import com.wm.data.IData;

import farsight.testing.jbehave.ExecutionContext;
import farsight.testing.jbehave.TestExecutor;
import farsight.utils.idata.DataBuilder;

@Ignore
public class JunitImplementedTest {
	
	private class JunitContext implements AutoCloseable {
		
		public final ExecutionContext ctx;
		public final TestExecutor executor;


		public JunitContext() {
			ctx = new ExecutionContext(); 
			executor = new TestExecutor(ctx);
		}
		
		public IData pipeline() {
			return ctx.getPipeline();
		}
		
		public Throwable exception() {
			return ctx.getThrownException();
		}
		
		public void connect() throws ServiceException {
			ctx.getConnectionContext();
		}

		@Override
		public void close() throws Exception {
			try {
				executor.teardown();
			} finally {
				executor.terminate();
			}
		}
		
	}
	
	
	@Test
	public void foo() throws Exception {
		try (JunitContext ctx = new JunitContext()) {
			ctx.connect();
			ctx.executor.invokeTestService("wm.server:ping", null);
			Assert.assertNull(ctx.exception());
			DataBuilder builder = DataBuilder.wrap(ctx.pipeline());
			
			System.out.println(builder.get("date"));
			Assert.assertNotNull(builder.get("date"));
			
		}
		
		
	}

}
