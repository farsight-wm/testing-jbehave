package farsight.testing.jbehave.steps.core;

import com.wm.data.IData;

import farsight.testing.jbehave.ExecutionContext;
import farsight.testing.jbehave.steps.WmStep;
import farsight.testing.utils.jexl.JexlExpressionUtil;

public class PipelineVariableStep implements WmStep {

	private final String jexlValueExpressions;
	
	public PipelineVariableStep(String jexlValueExpressions) {
		this.jexlValueExpressions = jexlValueExpressions;
	}


	@Override
	public void execute(ExecutionContext ctx) throws Exception {
		IData idata = ctx.getPipeline();
		idata = JexlExpressionUtil.executeValueExpressions(idata, jexlValueExpressions, ctx.getResources());
		ctx.setPipeline(idata);
	}

}
