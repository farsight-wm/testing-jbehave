package farsight.testing.jbehave.steps.core;

import com.wm.data.IData;
import com.wm.data.IDataFactory;

import farsight.testing.jbehave.ExecutionContext;
import farsight.testing.jbehave.steps.BaseServiceStep;
import farsight.testing.jbehave.utils.IDataMergeTool;

public class InvokeServiceStep extends BaseServiceStep {

	private final String serviceName;
	private final IData idata;

	public InvokeServiceStep(String serviceName, IData idata) {
		this.serviceName = serviceName;
		this.idata = idata;
	}

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		IData inputPipeline = idata == null ? IDataFactory.create() : idata;
		IData override = executionContext.getPipeline();
		if (override != null) {
			IDataMergeTool.mergeNestedTypes(override, inputPipeline);
		}
		IData pipeline = invoke(executionContext, serviceName, inputPipeline);
		executionContext.setPipeline(pipeline);
	}
	
}
