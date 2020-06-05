package farsight.testing.jbehave.steps.wmaop;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.wm.data.IData;
import com.wm.data.IDataFactory;
import com.wm.util.coder.IDataXMLCoder;

import farsight.testing.constants.ServiceNames;
import farsight.testing.constants.ServiceParameters;
import farsight.testing.jbehave.ExecutionContext;
import farsight.testing.jbehave.jbehave.StoryResourceContext;
import farsight.testing.jbehave.steps.AbstractAdvicedStep;
import farsight.testing.jbehave.steps.AopAdvice;
import farsight.testing.utils.jexl.JexlExpressionUtil;
import farsight.testing.utils.jexl.context.ResourceContext;
import farsight.utils.idata.DataBuilder;

public class MockServiceStep extends AbstractAdvicedStep {

	public static enum Mode {
		IDataXMLs, IDataFiles, JexlValueExpression, JexlScript, Nothing 
	}
	
	private final List<String> inputs;
	private final Mode mode;
	
	public MockServiceStep(AopAdvice advice, Mode mode, List<String> inputs) {
		super(advice, AopAdvice.MOCK);
		this.mode = mode;
		this.inputs = inputs;
	}
	
	@Override
	protected String setupService() {
		return mode == Mode.JexlScript ? ServiceNames.JEXL_RESPONSE_MOCK : ServiceNames.FIXED_RESPONSE_MOCK;
	}
	
	@Override
	protected void setupParameters(ExecutionContext ctx, DataBuilder builder) throws Exception {
		switch (mode) {
		case IDataXMLs:
			putIDataXMLs(builder, inputs);
			break;
		case IDataFiles:
			putIDataFiles(builder, inputs, ctx.getResources());
			break;
		case JexlValueExpression:
			putValueExpressions(builder, inputs, ctx.getResources());
			break;
		case Nothing:
			putEmptyExpression(builder);
			break;
		case JexlScript:
			builder.put(ServiceParameters.JEXL_SCRIPT, inputs.get(0));
			//TODO add all others as references? -- named references?
			break;
		}
	}
	
	private void putIDataXMLs(DataBuilder input, List<String> idataXMLs) throws IOException {
		IData[] responses = new IData[idataXMLs.size()];
		int i = 0;
		IDataXMLCoder coder = new IDataXMLCoder(StandardCharsets.UTF_8.name());
		for(String xml: idataXMLs) {
			responses[i++] = coder.decodeFromBytes(xml.getBytes(StandardCharsets.UTF_8));
		}
		input.put(ServiceParameters.RESPONSE, responses);	
	}

	private void putEmptyExpression(DataBuilder input) throws IOException {
		input.put(ServiceParameters.RESPONSE, IDataFactory.create());
	}

	private void putValueExpressions(DataBuilder input, List<String> valueExpressions, ResourceContext resourceContext) throws IOException {
		IData[] responses = new IData[valueExpressions.size()];
		int i = 0;
		for(String expr: valueExpressions) {
			responses[i++] = JexlExpressionUtil.executeValueExpressions(null, expr, resourceContext);
		}
		input.put(ServiceParameters.RESPONSE, responses);
	}

	private void putIDataFiles(DataBuilder input, List<String> inputs, StoryResourceContext storyResourceContext) throws Exception {
		input.put(ServiceParameters.RESPONSE, storyResourceContext.getAsIDataArray(inputs));
	}
	


}