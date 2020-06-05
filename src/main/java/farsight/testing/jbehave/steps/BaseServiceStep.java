package farsight.testing.jbehave.steps;

import com.wm.app.b2b.client.ServiceException;
import com.wm.data.IData;

import farsight.testing.jbehave.ExecutionContext;
import farsight.testing.jbehave.TestExecutor;
import farsight.utils.idata.DataBuilder;

public abstract class BaseServiceStep implements WmStep {

	//static invoke api
	
	protected static IData invoke(String service, IData input) throws ServiceException {
		return TestExecutor.current().invoke(service, input);
	}

	protected static DataBuilder invoke(String service, DataBuilder input) throws ServiceException {
		return DataBuilder.wrap(invoke(service, input.build()));
	}
	
	// non static api
	
	protected DataBuilder invoke(ExecutionContext ctx, String service, DataBuilder input) throws ServiceException {
		return DataBuilder.wrap(invoke(ctx, service, input.build()));
	}
	
	protected IData invoke(ExecutionContext ctx, String service, IData input) throws ServiceException {
		return ctx.invokeService(service, input); 
	}
	
	protected void putNonNull(DataBuilder builder, String key, Object value) {
		if(value != null) {
			builder.put(key, value);
		}
	}
}
