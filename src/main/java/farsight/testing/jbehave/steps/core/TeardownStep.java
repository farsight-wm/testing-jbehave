package farsight.testing.jbehave.steps.core;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;

import farsight.testing.constants.ServiceNames;
import farsight.testing.constants.ServiceParameters;
import farsight.testing.jbehave.ExecutionContext;
import farsight.testing.jbehave.steps.BaseServiceStep;

public class TeardownStep extends BaseServiceStep {

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		
		IData idata = IDataFactory.create();
		IDataCursor idc = idata.getCursor();
		IDataUtil.put(idc, ServiceParameters.SCOPE, "all");
		idc.destroy();
		invoke(executionContext, ServiceNames.TEARDOWN_FRAMEWORK, idata );
	}

}
