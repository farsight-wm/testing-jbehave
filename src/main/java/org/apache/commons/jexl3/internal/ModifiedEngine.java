package org.apache.commons.jexl3.internal;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlContext;

public class ModifiedEngine extends Engine {

	public ModifiedEngine(JexlBuilder conf) {
		super(conf);
	}

	protected Interpreter createInterpreter(JexlContext context, Scope.Frame frame) {
		return new ModifiedInterpreter(this, context, frame);
	}
}
