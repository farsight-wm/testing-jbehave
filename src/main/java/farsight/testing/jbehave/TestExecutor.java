package farsight.testing.jbehave;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.wm.app.b2b.client.ServiceException;
import com.wm.data.IData;
import com.wm.data.IDataFactory;
import com.wm.util.coder.IDataXMLCoder;

import farsight.testing.jbehave.jbehave.StoryResourceContext;
import farsight.testing.jbehave.steps.AopAdvice;
import farsight.testing.jbehave.steps.WmStep;
import farsight.testing.jbehave.steps.core.DocumentMatchStep;
import farsight.testing.jbehave.steps.core.InvokeServiceStep;
import farsight.testing.jbehave.steps.core.PipelineJexlStep;
import farsight.testing.jbehave.steps.core.TeardownStep;
import farsight.testing.jbehave.steps.wmaop.AssertInvokeCountStep;
import farsight.testing.jbehave.steps.wmaop.ExceptionSetupStep;
import farsight.testing.jbehave.steps.wmaop.MockServiceStep;
import farsight.testing.jbehave.steps.wmaop.PipelineCaptureStep;
import farsight.testing.jbehave.steps.wmaop.PipelineCaptureVerifyStep;
import farsight.testing.jbehave.utils.AdviceIDManager;
import farsight.utils.idata.DataBuilder;

public class TestExecutor {
	
	// ThreadLocal TestExecutors
	
	public static TestExecutor current() {
		return USER_THREAD_LOCAL.get();
	}
	
    private static final ThreadLocal<TestExecutor> USER_THREAD_LOCAL = new ThreadLocal<TestExecutor>() {
    	protected TestExecutor initialValue() {
    		return new TestExecutor(new ExecutionContext());
    	}
    };
	

	private static final Logger logger = LogManager.getLogger(TestExecutor.class);
	private static final String EOL = System.getProperty("line.separator");
	private static final String SEP = EOL + "-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-" + EOL;
	
	private final ExecutionContext executionContext;
	private int executedStep = 0;

	public TestExecutor(ExecutionContext executionContext) {
		this.executionContext = executionContext;
	}
	

	// -- pipeline, context, exception --
	
	public IData getPipeline() {
		return executionContext.getPipeline();
	}
	
	public ExecutionContext getContext() {
		return executionContext;
	}

	public void reset(Scope scope) {
		executionContext.reset(scope);
	}

	// -- Advices --
	
	public AdviceIDManager advices() {
		return executionContext.getAdviceIDManager();
	}
	
	public String define(String adviceId, String section) {
		return advices().define(adviceId, section);
	}
	
	public String define(String adviceId, String section, Object qualifier) {
		return advices().define(adviceId, section, String.valueOf(qualifier));
	}
	
	public String access(String adviceId, String section) {
		return advices().access(adviceId, section);
	}
	
	// -- Resources --
	
	public StoryResourceContext resources() {
		return executionContext.getResources();
	}
	
	// -- Properties --

	public void setProperty(String key, String value, Scope scope) {
		executionContext.setProperty(key, value, scope);
	}

	public void getProperty(String key) {
		executionContext.getProperty(key);
	}

	public void getProperty(String key, String defaultValue) {
		executionContext.getProperty(key, defaultValue);
	}
	
	
	// -- Step execution --

	public int getExecutedStep() {
		return executedStep;
	}
	
	public int stepIndex() {
		return executedStep;
	}
	
	public void withStep(WmStep step) {
		executeStep(step);
	}
	
	public void stepDone() {
		this.executedStep++;
		executionContext.setStepIndex(executedStep);
	}
	
	protected void executeStep(WmStep step) {
		// Subtle difference in types of error handling to ensure reported correctly
		try {
			executionContext.setStepIndex(executedStep);
			step.execute(executionContext);
			stepDone();
		} catch (AssertionError e) {
			showPipeline();
			throw e;
		} catch (Error e) {
			logger.error(e);
			showPipeline();
			throw e;
		} catch (RuntimeException e) {
			logger.error(e);
			showPipeline();
			throw e;
		} catch (Throwable e) {
			logger.error(e);
			showPipeline();
			fail(e.getMessage());
		}
	}

	private void logInvokeException(String serviceName, Exception use, String additionalMessage) {
		if (use instanceof ServiceException) {
			executionContext.setPipeline(((ServiceException)use).getErrorInfo().getValues("$pipeline"));
		} else {
			executionContext.setPipeline(IDataFactory.create()); // Pipeline not set from invoke so prevent NPE
		}
		executionContext.setThrownException(use);
		String msg = additionalMessage ==null?"":" - " + additionalMessage;
		logger.warn("Caught Exception while invoking [" + serviceName + "] this may not be the expected exception and could cause premature step failure.  Error is: " + use.getMessage() + msg);
	}

	// -- IData dumping --

	public static void showIData(String title, IData idata) {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			new IDataXMLCoder().encode(baos, idata);
			dump(title, baos);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	public static void dump(String title, Object out) {
		StringBuilder sb = new StringBuilder(title + ":").append(SEP).append(out).append(SEP);
		logger.info(sb);
	}
	
	// core steps

	public boolean testConnection() throws ServiceException{
		return executionContext.testConnection();
	}

	public void verify() throws Throwable {
		if (executionContext.getThrownException() != null) {
			showPipeline();
			throw (Throwable) executionContext.getThrownException();
		}
	
	}

	public void terminate() {
		executionContext.terminate();
	}

	public void teardown() throws Exception {
		new TeardownStep().execute(executionContext);
		executionContext.setPipeline(IDataFactory.create());
		executionContext.setThrownException(null);
		executedStep = 0;
	}
	
	// simple steps

	public void showPipeline() {
		showIData("Pipeline contents", executionContext.getPipeline());
	}

	public void showPipeline(List<String> filter) {
		DataBuilder builder = DataBuilder.wrap(executionContext.getPipeline()).asClone();
		builder.filter(filter.toArray(new String[filter.size()]));
		showIData("Pipeline contents", builder.build());
	}
	
	public void showCapture(String adviceId, String[] filter, int no) throws Exception {
		PipelineCaptureStep.dumpCaptures(adviceId, no, filter);
	}
	
	public void showCapture(String adviceId, Collection<String> filter, int no) throws Exception {
		showCapture(adviceId, filter == null ? null : filter.toArray(new String[filter.size()]), no);
	}

	public void withPipelineFromFile(String idataFile) throws Exception {
		final IData idata = resources().getAsIData(idataFile);
		executionContext.setPipeline(idata);
		stepDone();
	}
	
	
	// framework setup
	
	public void withMock(AopAdvice advice, MockServiceStep.Mode mode, List<String> inputs) {
		executeStep(new MockServiceStep(advice, mode, inputs));
	}
	
	public void withAssertion(AopAdvice advice) {
		executeStep(new AssertInvokeCountStep(advice));
	}
	
	public void withCapture(AopAdvice advice) {
		executeStep(new PipelineCaptureStep(advice));
	}
	
	public void withCapture(AopAdvice advice, int capacity) {
		executeStep(new PipelineCaptureStep(advice, capacity));
	}
	
	public void withException(AopAdvice advice, String exception) {
		executeStep(new ExceptionSetupStep(advice, exception));
		
	}
	
	// assertions
	
	public void assertInvokeCount(String assertionId, int invokeCount) {
		AssertInvokeCountStep.verify(assertionId, invokeCount);
	}
	
	public void assertPipelineMatches(String jexlExpression) {
		executeStep(new PipelineJexlStep(jexlExpression));
	}
	
	public void assertPipelineMatches(String document, String idataFile, boolean exact) throws Exception {
		executeStep(new DocumentMatchStep(
				document,
				resources().getAsIData(idataFile),
				exact));
	}
	
	public void assertExceptionWasThrown(String exception) {
		ExceptionSetupStep.verify(exception);
	}
	
	public void assertCaptureMatches(String adviceId, int no, String document, String jexlExpression) {
		executeStep(new PipelineCaptureVerifyStep(adviceId, no, document, jexlExpression));
	}
	
	public void assertCaptureMatches(String adviceId, int no, String document, String idataFile, boolean exact) throws Exception {
		executeStep(new PipelineCaptureVerifyStep(adviceId, no, document, resources().getAsIData(idataFile), exact));
	}
	
	
	public IData invoke(String serviceName, IData input) throws ServiceException {
		return getContext().invokeService(serviceName, input);
	}
	
	
	
	// test service invocation
	
	

	public void invokeTestService(String serviceName, String idataFile) throws Exception {
		IData idata = idataFile == null ? null : resources().getAsIData(idataFile);
		try {
			InvokeServiceStep step = new InvokeServiceStep(serviceName, idata);
			step.execute(executionContext);
		} catch (ServiceException use) {
			if (use.getErrorType().contains("UnknownServiceException")) {
				fail("Unknown service [" + serviceName + ']');
			} else {
				logInvokeException(serviceName, use, use.getErrorType());
			}
		} catch (Exception e) {
			logInvokeException(serviceName, e, null);
		}
	}

}
