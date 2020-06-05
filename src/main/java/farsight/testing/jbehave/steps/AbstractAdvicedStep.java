package farsight.testing.jbehave.steps;

import farsight.testing.constants.ServiceParameters;
import farsight.testing.jbehave.ExecutionContext;
import farsight.utils.idata.DataBuilder;

public abstract class AbstractAdvicedStep extends BaseServiceStep {
	
	protected final AopAdvice advice;
	protected final String adviceSection;
	
	public AbstractAdvicedStep(AopAdvice advice, String adviceSection) {
		this.advice = advice;
		this.adviceSection = adviceSection;
	}
	
	protected DataBuilder putAdvice(ExecutionContext ctx, DataBuilder builder) {
		String fqAdviceId = ctx.getAdviceIDManager().define(advice.adviceId, adviceSection, advice.createQualifier(String.valueOf(ctx.stepIndex()))); 
		putNonNull(builder, ServiceParameters.ADVICE_ID, fqAdviceId);
		putNonNull(builder, ServiceParameters.INTERCEPT_POINT, advice.interceptPoint.toString());
		putNonNull(builder, ServiceParameters.SERVICE_NAME, advice.service);
		putNonNull(builder, ServiceParameters.CONDITION, advice.condition);
		putNonNull(builder, ServiceParameters.CALLED_BY, advice.parent);
		putNonNull(builder, ServiceParameters.SCOPE, advice.scope.toString());
		return builder;
	}
	
	protected DataBuilder createInputWithAdvice(ExecutionContext ctx) {
		return putAdvice(ctx, DataBuilder.create());
	}
	
	protected void setupParameters(ExecutionContext ctx, DataBuilder builder) throws Exception {
		//can be used to setup additional parameters
	}
	
	protected abstract String setupService();
	
	public void execute(ExecutionContext ctx) throws Exception {
		DataBuilder input = createInputWithAdvice(ctx);
		setupParameters(ctx, input);
		invoke(ctx, setupService(), input.build());
	}

}
