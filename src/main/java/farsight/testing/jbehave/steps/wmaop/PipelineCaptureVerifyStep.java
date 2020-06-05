package farsight.testing.jbehave.steps.wmaop;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.softwareag.util.IDataMap;
import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;

import farsight.testing.constants.ServiceNames;
import farsight.testing.jbehave.ExecutionContext;
import farsight.testing.jbehave.TestExecutor;
import farsight.testing.jbehave.steps.BaseServiceStep;
import farsight.testing.jbehave.utils.IDataMatchTool;
import farsight.testing.utils.jexl.JexlExpressionUtil;
import farsight.utils.idata.DataBuilder;


public class PipelineCaptureVerifyStep extends BaseServiceStep {

	private String adviceId = null;
	private int callNo = -1;
	private String documentName;
	private IData idata = null;
	private boolean exact = false;
	private String jexlExpression;

	public PipelineCaptureVerifyStep(String adviceId, int no, String documentName, String jexlExpression) {
		this.adviceId = adviceId;
		this.callNo = no;
		this.jexlExpression = jexlExpression;
		this.documentName = documentName;
	}

	public PipelineCaptureVerifyStep(String adviceId, int no, String documentName, IData idata, boolean exact) {
		this.adviceId = adviceId;
		this.callNo = no;
		this.documentName = documentName;
		this.idata = idata;
		this.exact = exact;
	}

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		IDataMap result = new IDataMap(invoke(executionContext, ServiceNames.PIPELINE_CAPRURE_GETTER,
				DataBuilder.create().put("adviceId", adviceId).build()));
		
		IData[] captures = result.getAsIDataArray("captures");
		
		assertTrue("Pipline was not captured", captures != null && captures.length >= callNo);
		IData capture = captures[callNo - 1];
		
		if(documentName != null) {
			capture = JexlExpressionUtil.evaluateDocumentExpression(documentName, capture);
			if (capture == null) {
				fail("Failed when trying to matching capture. Cannot find document '" + JexlExpressionUtil.findFailedPath(documentName, capture)
						+ "' in capture");
			}
		}
		
		if(jexlExpression != null) {
			try {
				if (!JexlExpressionUtil.evaluatePipelineExpression(capture, jexlExpression)) {
					TestExecutor.showIData("Capture content", capture);
					fail("The expression [" + jexlExpression + "] returned false");
				}
			} catch (Exception e) {
				fail("parsing the expression '"+jexlExpression+"' failed");
				e.printStackTrace(); // Output in Eclipse console
			}
			
		} else {
			//match step
			IData potential = idata == null ? IDataFactory.create() : getDocumentContents();
			try {
				IDataMatchTool.assertMatches(capture, potential, exact, documentName);
			} catch(AssertionError e) {
				TestExecutor.showIData("Capture content", capture);
				throw e;
			}
		}
	}
	
	private IData getDocumentContents() throws Exception {
		IData idreturn;
		IDataCursor idc = idata.getCursor();
		if (IDataUtil.size(idc) == 1) {
			IData id = IDataUtil.getIData(idc, documentName);
			if (id != null) {
				idreturn = id;
			} else {
				idreturn = idata;
			}
		} else {
			idreturn = idata;
		}
		idc.destroy();
		return idreturn;
	}

}
