package farsight.testing.jbehave.junit.model;

public class JUnitModelException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public JUnitModelException(String message) {
		super(message);
	}
	
	public JUnitModelException(Throwable e) {
		super(e);
	}
	
}
