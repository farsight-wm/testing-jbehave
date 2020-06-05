package farsight.testing.jbehave.testcase.additionalSteps;

import org.jbehave.core.annotations.When;

public class AdditionalStepsClass {

	@When("some step has $someParam")
	public void some_step(String someParam) {
		System.out.println("AdditionalSteps.some_step: someParam=" +someParam);
	}
	
}
