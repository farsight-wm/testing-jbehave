package farsight.testing.jbehave.steps.core;

import static org.junit.Assert.fail;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataFactory;
import com.wm.data.IDataUtil;

import farsight.testing.jbehave.ExecutionContext;
import farsight.testing.jbehave.steps.WmStep;
import farsight.testing.jbehave.utils.IDataMatchTool;
import farsight.testing.utils.jexl.legacy.ExpressionProcessor;
import farsight.testing.utils.jexl.legacy.IDataJexlContext;

public class DocumentMatchStep implements WmStep {

	private final String documentReference;
	private final IData idata;
	private final String documentName;
	private final boolean exact;

	public DocumentMatchStep(String documentName, IData idata, boolean exact) {
		this.documentName = documentName;
		this.documentReference = ExpressionProcessor.escapedToEncoded(documentName);
		this.idata = idata;
		this.exact = exact;
	}

	@Override
	public void execute(ExecutionContext executionContext) throws Exception {
		IData potential = idata == null ? IDataFactory.create() : getDocumentContents();
		IDataJexlContext docRef = (IDataJexlContext) new IDataJexlContext(executionContext.getPipeline())
				.get(documentReference);
		if (docRef == null) {
			fail("Failed when trying to matching document.  Cannot find document '" + documentName
					+ "' in the pipeline");
		}
		IData doc = docRef.toIData();
		IDataMatchTool.assertMatches(doc, potential, exact, documentName);
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
