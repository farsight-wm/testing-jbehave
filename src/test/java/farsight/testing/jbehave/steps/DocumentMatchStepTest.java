package farsight.testing.jbehave.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.wm.data.IData;

import farsight.testing.jbehave.ExecutionContext;
import farsight.testing.jbehave.jbehave.StoryResourceContext;
import farsight.testing.jbehave.steps.core.DocumentMatchStep;

public class DocumentMatchStepTest {
	
	private final StoryResourceContext resourceContext = new StoryResourceContext(null);


	IData getIdataFile(String fileName) throws Exception {
		return resourceContext.getAsIData(fileName);
	}

	@Test
	public void shouldMatchIDataSnippet() throws Exception {
		match("data/complexsnippet.xml");
	}

	@Test
	public void shouldMatchIDataDocument() throws Exception {
		match("data/complexdocument.xml");
	}
	
	@Test
	public void shouldMatchXMLdocument() throws Exception {
		match("data/xmldocument.xml");
	}

	@Test
	public void shouldMatchArrayElements() throws Exception {
		match("data/complexarraysnippet.xml");
	}

	@Test
	public void shouldMatchSimple() throws Exception {
		match("data/simpleidata.xml", "data/simple.xml", "document");
	}

	@Test
	public void shouldMatchSimpleXmlSnippet() throws Exception {
		match("data/simpleidata.xml", "data/simplesnippet.xml", "document");
	}

	@Test
	public void shouldMatchSimpleIDataSnippet() throws Exception {
		match("data/simpleidata.xml", "data/simpleidatasnippet.xml", "document");
	}
	
	private void match(String dataToMatch) throws Exception {
		match("data/complex.xml", dataToMatch, "producer");
	}
	
	void match(String source, String dataToMatch, String documentName) throws Exception {
		IData complex = getIdataFile(source);
		DocumentMatchStep step = new DocumentMatchStep(documentName, getIdataFile(dataToMatch), false);
		ExecutionContext executionContext = new ExecutionContext();
		executionContext.setPipeline(complex);
		step.execute(executionContext);
	}
	
	@Test
	public void shouldTrapIncorrectElement() throws Exception {
		IData complex = getIdataFile("data/complex.xml");
		DocumentMatchStep step = new DocumentMatchStep("producer", getIdataFile("data/complexsnippet-element.xml"), false);
		ExecutionContext executionContext = new ExecutionContext();
		executionContext.setPipeline(complex);
		try {
			step.execute(executionContext);
			fail();
		} catch (AssertionError e) {
			assertEquals("Failed to locate element: producer.serviceNotToExecute", e.getMessage());
		}
	}

	@Test
	public void shouldTrapIncorrectValue() throws Exception {
		IData complex = getIdataFile("data/complex.xml");
		DocumentMatchStep step = new DocumentMatchStep("producer", getIdataFile("data/complexsnippet-value.xml"), false);
		ExecutionContext executionContext = new ExecutionContext();
		executionContext.setPipeline(complex);
		try {
			step.execute(executionContext);
			fail();
		} catch (AssertionError e) {
			assertEquals("Failed to locate element: producer.serviceToExecute", e.getMessage());
		}
	}
}
