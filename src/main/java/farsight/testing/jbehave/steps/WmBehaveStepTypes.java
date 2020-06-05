package farsight.testing.jbehave.steps;

import java.util.Arrays;
import java.util.List;

import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.junit.Assert;

import com.wm.data.IData;

import farsight.testing.jbehave.Scope;
import farsight.testing.jbehave.TestExecutor;
import farsight.testing.jbehave.jbehave.InterceptPoint;
import farsight.testing.jbehave.jexl.Jexl;
import farsight.testing.jbehave.jexl.WmAssert;
import farsight.testing.jbehave.jexl.Jexl.ScriptEnv;
import farsight.testing.jbehave.steps.wmaop.MockServiceStep;
import farsight.testing.jbehave.steps.wmaop.PipelineCaptureStep;

public class WmBehaveStepTypes extends AbstractWmStepTypes {
	
	
	// --- GIVEN: pipeline ----------------------------------------------------

	/**
	 * Sets pipeline values.
	 * 
	 * @param jexlValueExpression Multiple value expressions (assignments) separated by semicolons. 
	 */
	@Given("pipeline values$jexlScript")
	public void given_pipeline_values(String jexlScript) {
		Jexl.prepareScript(pipeline()).addResources(resources()).execute(jexlScript);
		stepDone();
	}
	
	/**
	 * Load idataFile and replace test pipeline with it.
	 * 
	 * @param idataFile
	 *            The path to an idataFile
	 * @throws Exception
	 *             When file cannot be found or read.
	 */
	@Given("pipeline from file $file")
	public void given_pipeline_from_file(String idataFile) throws Exception {
		executor().withPipelineFromFile(idataFile);
	}
	
	// --- GIVEN: mock --------------------------------------------------------

	/**
	 * Generates a mock service that returns the values provided in round robin
	 * mode.
	 * 
	 * @param serviceName
	 *            The NSName of the mocked service
	 * @param idataFiles
	 *            A comma separated list of (local) IData files
	 */
	@Given(value = "mock $serviceName returning $idataFiles", priority = 0)
	public void given_mock_returning_files(String serviceName, List<String> idataFiles) {
		serviceName = validateServicename(serviceName);
		executor().withMock(
				AopAdvice.create(serviceName).forService(serviceName).build(),
				MockServiceStep.Mode.IDataFiles, idataFiles);
	}

	/**
	 * Generates a mock service that returns no values
	 * 
	 * @param serviceName
	 *            The NSName of the mocked service
	 */
	@Given(value = "mock $serviceName returning nothing", priority = 1)
	public void given_mock_returning_nothing(String serviceName) {
		serviceName = validateServicename(serviceName);
		executor().withMock(
				AopAdvice.create(serviceName).forService(serviceName).build(),
				MockServiceStep.Mode.Nothing, null);
	}

	/**
	 * Generates a mock service that returns the values provided in round robin
	 * mode when the jexl expression evaluates to true.
	 * 
	 * @param serviceName
	 *            The NSName of the mocked service
	 * @param idataFiles
	 *            A comma separated list of (local) IData files
	 * @param jexlExpression
	 *            A boolean jexl expression
	 */
	@Given(value = "mock $serviceName returning $idataFiles when $jexlExpression", priority = 2)
	public void given_mock_returning_files_when(String serviceName, List<String> idataFiles, String jexlExpression) {
		serviceName = validateServicename(serviceName);
		executor().withMock(
				AopAdvice.create(serviceName)
					.forService(serviceName).when(jexlExpression).build(),
				MockServiceStep.Mode.IDataFiles, idataFiles);
	}

	/**
	 * Generates a mock service that returns the values provided as jexl value expression.
	 * 
	 * @param serviceName
	 *            The NSName of the mocked service
	 * @param jexlValues
	 *            Jexl value expressions separated by semicolons
	 */
	@Given(value = "mock $serviceName returning values $jexlValues", priority = 3)
	public void given_mock_returning_values(String serviceName, String jexlValues) {
		serviceName = validateServicename(serviceName);
		executor().withMock(
				AopAdvice.create(serviceName).forService(serviceName).build(),
				MockServiceStep.Mode.JexlValueExpression, Arrays.asList(jexlValues));
	}

	/**
	 * Generates a mock service that returns the values provided as jexl value
	 * expression when the jexl expression evaluates to true.
	 * 
	 * @param serviceName
	 *            The NSName of the mocked service
	 * @param jexlValues
	 *            Jexl value expressions separated by semicolons
	 * @param jexlExpression
	 *            A boolean jexl expression
	 */
	@Given(value = "mock $serviceName returning values $jexlValues when $jexlExpression", priority = 4)
	public void given_mock_returning_values_when(String serviceName, String jexlValues, String jexlExpression) {
		serviceName = validateServicename(serviceName);
		executor().withMock(
				AopAdvice.create(serviceName).forService(serviceName).when(jexlExpression).build(),
				MockServiceStep.Mode.JexlValueExpression, Arrays.asList(jexlValues));
	}
	
	/**
	 * Generates a mock service that when invoked executes the provided jexl
	 * script to generate return values.
	 * 
	 * @param serviceName
	 *            The NSName of the mocked service
	 * @param jexlScript
	 *            The jexl script. Every value modified or (not locally) defined
	 *            will be returnd to the calling service
	 */
	@Given(value = "mock $serviceName as jexl:$jexlScript", priority = 1)
	public void given_jexl_mock(String serviceName, String jexlScript) {
		serviceName = validateServicename(serviceName);
		executor().withMock(
				AopAdvice.create(serviceName).forService(serviceName).build(),
				MockServiceStep.Mode.JexlScript, Arrays.asList(jexlScript));
	}
	
	// --- GIVEN: intercepts --------------------------------------------------

	@Given("max capture-capacity is $value")
	public void given_max_capture_capacity_is(int value) {
		TestExecutor executor = executor();
		executor.setProperty(PipelineCaptureStep.MAX_CAPTURE_CAPACITY_PROPERTY, String.valueOf(value), Scope.Scenario);
		executor.stepDone();
	}

	@Given(value = "$captureId capture pipeline calling service $serviceName", priority = 1)
	public void given_capture_pipeline_calling_service(String captureId, String serviceName) {
		given_capture_pipeline_at_intercept_point_calling_service_with_expression(captureId, InterceptPoint.invoke,
				serviceName, null);
	}
	
	@Given(value = "$captureId capture pipeline calling service $serviceName when $jexlExpression", priority = 2)
	public void given_capture_pipeline_calling_service_with_expression(String captureId, String serviceName,
			String jexlExpression) {
		given_capture_pipeline_at_intercept_point_calling_service_with_expression(captureId, InterceptPoint.invoke,
				serviceName, jexlExpression);
	}

	@Given(value = "$captureId capture pipeline $interceptPoint calling service $serviceName", priority = 3)
	public void given_capture_pipeline_at_intercept_point_calling_service(String captureId,
			InterceptPoint interceptPoint, String serviceName) {
		given_capture_pipeline_at_intercept_point_calling_service_with_expression(captureId, interceptPoint,
				serviceName, null);
	}

	@Given(value = "$captureId capture pipeline $interceptPoint calling service $serviceName when $jexlExpression", priority = 4)
	public void given_capture_pipeline_at_intercept_point_calling_service_with_expression(String captureId,
			InterceptPoint interceptPoint, String serviceName, String jexlExpression) {
		serviceName = validateServicename(serviceName);
		executor().withCapture(AopAdvice.create(captureId).forService(serviceName).at(interceptPoint).when(jexlExpression).noQualifier().build());
	}

	public static final String SERVICES_JMS_SEND = "pub.jms:send";

	@Given("intercept next $capacity JMSMessages")
	public void given_intercept_next_jms_messages(int capacity) {
		executor().withCapture(AopAdvice.create(SERVICES_JMS_SEND).forService(SERVICES_JMS_SEND).build(), capacity);
	}

	@Given("intercept JMSMessages")
	public void given_intercept_jms_messages() {
		given_intercept_next_jms_messages(0);
	}

	// --- GIVEN: assertions --------------------------------------------------

	@Given(value = "$assertionId assertion $interceptPoint service $serviceName", priority = 0)
	public void given_assertion_service(String assertionId, InterceptPoint interceptPoint, String serviceName) {
		serviceName = validateServicename(serviceName);
		AopAdvice advice = AopAdvice.create(assertionId).forService(serviceName).at(interceptPoint).build();
		executor().withAssertion(advice);
	}

	@Given(value = "$assertionId assertion $interceptPoint service $serviceName when $jexlPipelineExpression", priority = 1)
	public void given_assertion_service_when(String assertionId, InterceptPoint interceptPoint, String serviceName,
			String expression) {
		serviceName = validateServicename(serviceName);
		AopAdvice advice = AopAdvice.create(assertionId).forService(serviceName).at(interceptPoint).when(expression).noQualifier().build();
		executor().withAssertion(advice);
	}
	
	// --- GIVEN: exceptions --------------------------------------------------

	@Given(value = "exception $exception thrown calling service $serviceName", priority = 0)
	public void given_exception_thrown_calling_service(String exception, String serviceName) {
		given_exception_thrown_calling_service_with_expression(exception, InterceptPoint.invoke, serviceName, null);
	}
	
	@Given(value = "exception $exception thrown calling service $serviceName when $jexlExpression", priority = 1)
	public void given_exception_thrown_calling_service(String exception, String serviceName, String jexlExpression) {
		given_exception_thrown_calling_service_with_expression(exception, InterceptPoint.invoke, serviceName, jexlExpression);
	}

	@Given(value = "exception $exception thrown $interceptPoint calling service $serviceName", priority = 0)
	public void given_exception_thrown_at_intercept_point_calling_service(String exception, InterceptPoint interceptPoint,
			String serviceName) {
		serviceName = validateServicename(serviceName);
		given_exception_thrown_calling_service_with_expression(exception, interceptPoint, serviceName, null);
	}

	@Given(value = "exception $exception thrown $interceptPoint calling service $serviceName when $jexlExpression", priority = 1)
	public void given_exception_thrown_calling_service_with_expression(String exception, InterceptPoint interceptPoint,
			String serviceName, String jexlExpression) {
		serviceName = validateServicename(serviceName);
		executor().withException(AopAdvice.create(serviceName).forService(serviceName).at(interceptPoint).when(jexlExpression).build(), exception);
	}

	// --- WHEN: invoke -------------------------------------------------------
	
	@When(value = "invoke $serviceName with $idataFile", priority = 1)
	public void when_invoke_service(String serviceName, String idataFile) throws Exception {
		serviceName = validateServicename(serviceName);
		executor().invokeTestService(serviceName, idataFile);
	}

	@When("invoke $serviceName")
	public void when_invoke_service(String serviceName) throws Exception {
		serviceName = validateServicename(serviceName);
		executor().invokeTestService(serviceName, null);
	}
	
	// --- THEN: asserts ------------------------------------------------------
	
	public static final String CTX_NAME_PIPELINE = "$pipeline";
	public static final String CTX_NAME_PARENT = "$parent";

	private ScriptEnv prepareAssertion(IData root, String pathPrefix) {
		return Jexl.prepareAssertionScript(root, pathPrefix).addResources(resources()).addNamespace("assert", WmAssert.INSTANCE);
	}
	
	@Then("assert that$assertScript")
	public void then_assert_that(String assertScript) {
		try {
			prepareAssertion(pipeline(), "/").execute(assertScript);
		} catch(Exception e) {
			//unwrap AssertionError
			if(e.getCause() instanceof AssertionError) {
				throw (AssertionError) e.getCause();
			} else throw e;
		}
	}
	
	@Then("assert for $document that$assertScript")
	public void then_document_has(String document, String assertScript) {
		document = document.trim();
		IData pipeline = pipeline(), doc = CTX_NAME_PIPELINE.equals(document) ? pipeline : read(pipeline, document, IData.class);
		Assert.assertNotNull("Assert for is null: " + document, doc);
		try {
			prepareAssertion(doc, "/" + document + "/").addArgument(CTX_NAME_PIPELINE, pipeline).execute(assertScript);
		} catch(Exception e) {
			//unwrap AssertionError
			if(e.getCause() instanceof AssertionError) {
				throw (AssertionError) e.getCause();
			} else throw e;
		}
	}
	
	@Then("assert foreach $documentList that$assertScript")
	public void then_each_document_has(String documentList, String assertScript) {
		documentList = documentList.trim();
		final IData pipeline = pipeline();
		final IData[] items = pipelineDocumentList(documentList);
		Assert.assertNotNull("Assert foreach is null: " + documentList, items);
		for(int i = 0; i < items.length; i++) {
			try {
				prepareAssertion(items[i], "/" + documentList + "[" + i + "]/")
					.addArgument("$index", i)
					.addArgument(CTX_NAME_PIPELINE, pipeline)
					.addArgument(CTX_NAME_PARENT, items)
					.execute(assertScript);
			} catch(Exception e) {
				//unwrap and enrich AssertionError
				if(e.getCause() instanceof AssertionError) {
					throw new AssertionError(e.getCause().getMessage() + " at index: " + i, e.getCause());
				} else throw e;
			}
		}
	}
	
	// --- THEN: pipeline -----------------------------------------------------

	@Then("pipeline has $jexlPipelineExpression")
	public void then_pipeline_has_expression(String jexlExpression) throws Throwable {
		executor().assertPipelineMatches(jexlExpression);
	}

	@Then(value = "pipeline document $document matches $idataFile", priority = 0)
	public void then_pipeline_matches(String document, String idataFile) throws Throwable {
		executor().assertPipelineMatches(document, idataFile, false);
	}

	@Then(value = "pipeline document $document exactly matches $idataFile", priority = 1)
	public void then_pipeline_exactly_matches(String document, String idataFile) throws Throwable {
		executor().assertPipelineMatches(document, idataFile, true);
	}
	
	// --- THEN: intercepts ---------------------------------------------------
	
	@Then(value = "for capture $captureId $no assert that$assertScript", priority = 0)
	public void then_assert_for_capture_that(String captureId, int no, String assertScript) throws Exception {
		TestExecutor executor = executor();
		String adviceId = executor.access(captureId, AopAdvice.CAPTURE);
		
		IData capture = PipelineCaptureStep.getCapture(adviceId, no);
		if(capture == null)
			Assert.fail("Capture " + no + " of " + captureId + "was not captured");
		
		try {
			prepareAssertion(capture, "/").execute(assertScript);
		} catch(Exception e) {
			//unwrap AssertionError
			if(e.getCause() instanceof AssertionError) {
				throw (AssertionError) e.getCause();
			} else throw e;
		}
	}
	

	@Then(value = "capture $captureId $no matches $idataFile", priority = 0)
	public void then_capture_matches_file(String captureId, int no, String idataFile) throws Exception {
		TestExecutor executor = executor();
		String adviceId = executor.access(captureId, AopAdvice.CAPTURE);
		executor.assertCaptureMatches(adviceId, no, null, idataFile, false);
	}

	@Then(value = "capture $captureId $no has $jexlPipelineExpression", priority = 0)
	public void then_capture_has_expression(String captureId, int no, String expression) {
		TestExecutor executor = executor();
		String adviceId = executor.access(captureId, AopAdvice.CAPTURE);
		executor.assertCaptureMatches(adviceId, no, null, expression);
	}

	@Then(value = "capture $captureId $no document $document matches $idataFile", priority = 1)
	public void then_capture_document_matches_file(String captureId, int no, String document, String idataFile)
			throws Exception {
		TestExecutor executor = executor();
		String adviceId = executor.access(captureId, AopAdvice.CAPTURE);
		executor.assertCaptureMatches(adviceId, no, document, idataFile, false);
	}

	@Then(value = "capture $captureId $no document $document matches exactly $idataFile", priority = 2)
	public void then_capture_document_matches_exactly_file(String captureId, int no, String document, String idataFile)
			throws Exception {
		TestExecutor executor = executor();
		String adviceId = executor.access(captureId, AopAdvice.CAPTURE);
		executor.assertCaptureMatches(adviceId, no, document, idataFile, true);
	}

	@Then(value = "capture $captureId $no document $document has $jexlPipelineExpression", priority = 1)
	public void then_capture_document_has_expression(String captureId, int no, String document, String expression) {
		TestExecutor executor = executor();
		String adviceId = executor.access(captureId, AopAdvice.CAPTURE);
		executor.assertCaptureMatches(adviceId, no, document, expression);
	}

	@Then("JMSMessage $no is sent to $destinationType $destinationName")
	public void then_jms_message_sent_to(int no, String destinationType, String destinationName) {
		TestExecutor executor = executor();
		String adviceId = executor.access(SERVICES_JMS_SEND, AopAdvice.CAPTURE);
		executor.assertCaptureMatches(adviceId, no, null,
				"destinationType =~ \"(?i)" + destinationType + "\" && destinationName == \"" + destinationName + "\"");
	}

	@Then("JMSMessage $no matches $idataFile")
	public void then_jms_message_matches_file(int no, String idataFile) throws Exception {
		TestExecutor executor = executor();
		String adviceId = executor.access(SERVICES_JMS_SEND, AopAdvice.CAPTURE);
		executor.assertCaptureMatches(adviceId, no, "JMSMessage", idataFile, false);
	}

	@Then("JMSMessage $no has $jexlPipelineExpression")
	public void then_jms_message_has_expression(int no, String expression) {
		TestExecutor executor = executor();
		String adviceId = executor.access(SERVICES_JMS_SEND, AopAdvice.CAPTURE);
		executor.assertCaptureMatches(adviceId, no, "JMSMessage", expression);
	}	
	
	// --- THEN: assertions ---------------------------------------------------

	@Then("assertion $assertionId was invoked $invokeCount times")
	@Alias("mock $assertionId was invoked $invokeCount times")
	public void then_assertion_was_invoked_times(String assertionId, int invokeCount) throws Throwable {
		TestExecutor executor = executor();
		String adviceId = executor.access(assertionId, AopAdvice.ASSERTION); 
		executor.assertInvokeCount(adviceId, invokeCount);
	}

	// --- THEN: exceptions ---------------------------------------------------

	@Then("exception $exception was thrown")
	public void then_exception_was_thrown(String exception) {
		executor().assertExceptionWasThrown(exception);
	}
	
	// --- THEN: utilities ----------------------------------------------------

	@Then("dump pipeline") 
	@Alias("show pipeline in console")
	public void then_dump_pipeline() {
		executor().showPipeline();
	}
	
	@Then(value="dump pipeline with filter $filter", priority=1)
	public void then_dump_pipeline_filtered(List<String> filter) {
		executor().showPipeline(filter);
	}

	@Then("show all captures from $captureId in console")
	public void then_show_all_captures(String captureId) throws Exception {
		then_show_capture_filtered(0, captureId, null);
	}

	@Then("show capture $no from $captureId in console")
	public void then_show_capture(int no, String captureId) throws Exception {
		then_show_capture_filtered(no, captureId, null);
	}

	@Then("show all captures from $captureId in console with filter $filter")
	public void then_show_all_captures_filtered(String captureId, List<String> filter) throws Exception {
		then_show_capture_filtered(0, captureId, filter);
	}

	@Then("show capture $no from $captureId in console with filter $filter")
	public void then_show_capture_filtered(int no, String captureId, List<String> filter) throws Exception {
		TestExecutor executor = executor();
		String adviceId = executor.access(captureId, AopAdvice.CAPTURE);
		executor.showCapture(adviceId, filter, no);
	}

	@Then("show JMSMessages $no in console")
	public void then_show_JMSMessges(int no) throws Exception {
		TestExecutor executor = executor();
		String adviceId = executor.access(SERVICES_JMS_SEND, AopAdvice.CAPTURE);
		executor.showCapture(adviceId, PipelineCaptureStep.JMS_FILTER, no);
	}

	@Then("show all JMSMessages in console")
	public void then_show_all_JMSMessges() throws Exception {
		then_show_JMSMessges(0);
	}


}
