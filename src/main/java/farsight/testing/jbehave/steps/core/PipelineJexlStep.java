package farsight.testing.jbehave.steps.core;

import static org.junit.Assert.fail;

import farsight.testing.jbehave.ExecutionContext;
import farsight.testing.jbehave.steps.WmStep;
import farsight.testing.utils.jexl.JexlExpressionUtil;

public class PipelineJexlStep implements WmStep {

	private final String jexlPipelineExpression;

	public PipelineJexlStep(String jexlPipelineExpression) {
		this.jexlPipelineExpression = jexlPipelineExpression;
	}

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		try {
			if(!JexlExpressionUtil.evaluatePipelineExpression(executionContext.getPipeline(), jexlPipelineExpression)) {
				fail("The expression [" + jexlPipelineExpression + "] returned false");
			}
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
