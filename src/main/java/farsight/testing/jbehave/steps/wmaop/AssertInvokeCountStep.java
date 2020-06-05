package farsight.testing.jbehave.steps.wmaop;

import static org.junit.Assert.assertEquals;

import com.wm.app.b2b.client.ServiceException;

import farsight.testing.constants.ServiceNames;
import farsight.testing.constants.ServiceParameters;
import farsight.testing.jbehave.steps.AbstractAdvicedStep;
import farsight.testing.jbehave.steps.AopAdvice;
import farsight.utils.idata.DataBuilder;

public class AssertInvokeCountStep extends AbstractAdvicedStep {

	public AssertInvokeCountStep(AopAdvice advice) {
		super(advice, AopAdvice.ASSERTION);
	}

	@Override
	protected String setupService() {
		return ServiceNames.SETUP_ASSERTION;
	}
	
	
	public static int getInvokeCount(String adviceId) {
		try {
			return invoke(ServiceNames.ASSERTION_INVOKE_COUNT,
					DataBuilder.create().put(ServiceParameters.ADVICE_ID, adviceId)).get(ServiceParameters.INVOKE_COUNT,
							Integer.class);
		} catch (ServiceException e) {
			return 0;
		}
	}
	
	public static void verify(String adviceId, int expectedInvokeCount) {
		int actualInvokeCount = getInvokeCount(adviceId);
		assertEquals("Failed to match invoke count for: " + adviceId, expectedInvokeCount, actualInvokeCount);
	}
	
}
