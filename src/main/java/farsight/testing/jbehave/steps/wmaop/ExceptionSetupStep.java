package farsight.testing.jbehave.steps.wmaop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;

import com.wm.app.b2b.server.ServiceException;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;
import com.wm.lang.flow.FlowException;

import farsight.testing.constants.ServiceNames;
import farsight.testing.constants.ServiceParameters;
import farsight.testing.jbehave.ExecutionContext;
import farsight.testing.jbehave.TestExecutor;
import farsight.testing.jbehave.steps.AbstractAdvicedStep;
import farsight.testing.jbehave.steps.AopAdvice;
import farsight.utils.idata.DataBuilder;

public class ExceptionSetupStep extends AbstractAdvicedStep {

	public static final String ANY_EXCEPTION = "any";
	
	private static final String EXCEPTION_MESSAGE_PIPELINE_KEY = "exceptionMessage";

	private static HashMap<String, String> exceptionAliasMap = new HashMap<>();

	public static void defineAlias(String exception, Class<? extends Throwable> exceptionClass) {
		defineAlias(exception, exceptionClass.getCanonicalName());
	}
	public static void defineAlias(String exception, String exceptionClass) {
		exceptionAliasMap.put(exception, exceptionClass);
	}

	public static String getExceptionClass(String exception) {
		String result = exceptionAliasMap.get(exception);
		if (result != null)
			return result;

		if (exception.indexOf('.') > 0)
			return exception;

		// assume java.lang.
		return "java.lang." + exception;
	}

	static {
		defineAlias(ANY_EXCEPTION, ANY_EXCEPTION);
		defineAlias("ServiceException", ServiceException.class);
		defineAlias("service", ServiceException.class);
		defineAlias("FlowException", FlowException.class);
		defineAlias("flow", FlowException.class);
	}

	private final String exceptionClass;

	public ExceptionSetupStep(AopAdvice advice, String exception) {
		super(advice, AopAdvice.MOCK);
		this.exceptionClass = getExceptionClass(exception); 
	}

	@Override
	protected String setupService() {
		return ServiceNames.REGISTER_EXCEPTION;
	}
	
	@Override
	protected void setupParameters(ExecutionContext ctx, DataBuilder builder) throws Exception {
		builder.put(ServiceParameters.EXCEPTION, exceptionClass);
	}
	
	public static void verify(String exception) {
		String expectedException = getExceptionClass(exception);
		ExecutionContext executionContext = TestExecutor.current().getContext();
		Throwable e = executionContext.getThrownException();
		if (e == null) {
			fail("No exception found from service ");
		}
		if (e instanceof com.wm.app.b2b.client.ServiceException
				&& ((com.wm.app.b2b.client.ServiceException) e).getErrorType() != null) {
			if (ExceptionSetupStep.ANY_EXCEPTION.equals(expectedException)) {
				// well, we are satisfied
			} else {
				assertEquals(expectedException, ((com.wm.app.b2b.client.ServiceException) e).getErrorType());
			}
			addExceptionMessageToPipeline(executionContext);
			executionContext.setThrownException(null);
		}
	}

	private static void addExceptionMessageToPipeline(ExecutionContext ctx) {
		IDataCursor idc = ctx.getPipeline().getCursor();
		IDataUtil.put(idc, EXCEPTION_MESSAGE_PIPELINE_KEY, ctx.getThrownException().getMessage());
		idc.destroy();
	}

}
