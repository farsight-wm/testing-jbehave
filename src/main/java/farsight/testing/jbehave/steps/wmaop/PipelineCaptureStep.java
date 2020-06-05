package farsight.testing.jbehave.steps.wmaop;

import com.wm.app.b2b.client.ServiceException;
import com.wm.data.IData;

import farsight.testing.constants.ServiceNames;
import farsight.testing.constants.ServiceParameters;
import farsight.testing.jbehave.ExecutionContext;
import farsight.testing.jbehave.TestExecutor;
import farsight.testing.jbehave.steps.AbstractAdvicedStep;
import farsight.testing.jbehave.steps.AopAdvice;
import farsight.utils.idata.DataBuilder;

public class PipelineCaptureStep extends AbstractAdvicedStep {

	private final int capacity;

	public static final String MAX_CAPTURE_CAPACITY_PROPERTY = "pipelineCapture.maxCapacity";
	public static final String DEFAULT_MAX_CAPACITY = "10";
	
	public static final String[] JMS_FILTER = new String[] { 
			"connectionAliasName",
			"destinationName",
			"destinationType",
			"JMSMessage"
	};

	public PipelineCaptureStep(AopAdvice advice) {
		this(advice, 0);
	}

	public PipelineCaptureStep(AopAdvice advice, int capacity) {
		super(advice, AopAdvice.CAPTURE);
		this.capacity = capacity;
	}

	@Override
	protected void setupParameters(ExecutionContext ctx, DataBuilder builder) throws Exception {
		builder.put(ServiceParameters.CAPACITY, capacity > 0 ? String.valueOf(capacity)
				: ctx.getProperty(MAX_CAPTURE_CAPACITY_PROPERTY, DEFAULT_MAX_CAPACITY));
	}

	@Override
	protected String setupService() {
		return ServiceNames.PIPELINE_CAPTURE_INTERCEPTOR;
	}
	
	public static IData[] getAllCaptures(String adviceId) throws ServiceException {
		return invoke(ServiceNames.PIPELINE_CAPRURE_GETTER, DataBuilder.create().put("adviceId", adviceId))
				.get(ServiceParameters.CAPTURES, IData[].class);	
	}
	
	public static IData getCapture(String adviceId, int callNo) throws ServiceException {
		IData[] captures = getAllCaptures(adviceId);
		return (captures != null && callNo > 0 && callNo <= captures.length) ? captures[callNo - 1] : null;
	}
	
	// DUMP
	public static void dumpCaptures(String adviceId, int callNo, String[] filter) throws Exception {
		IData[] captures = getAllCaptures(adviceId);
		
		if(captures == null || captures.length == 0) {
			TestExecutor.dump(adviceId, "Nothing captured");
			return;
		}
		
		if(callNo <= 0) {
			//dump all
			for(int i = 0; i < captures.length; i++) {
				TestExecutor.showIData(adviceId + " - " + (i + 1) , filter(captures[i], filter));
			}
		} else {
			if(captures.length >= callNo) {
				TestExecutor.showIData(adviceId + " - " + callNo , filter(captures[callNo - 1], filter));
			} else {
				if(captures == null || captures.length == 0) {
					TestExecutor.dump(adviceId, "Capture " + callNo + " not captured (capture length is " + captures.length + ")"); 
				}		
			}
		}
	}
	
	private static IData filter(IData data, String[] filter) {
		if(filter == null || data == null)
			return data;
		return DataBuilder.wrap(data).filter(filter).build();
	}
}