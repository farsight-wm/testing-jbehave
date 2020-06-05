package org.apache.commons.jexl3.internal;

import org.apache.commons.jexl3.JexlBuilder;
import org.apache.commons.jexl3.JexlEngine;

public class ModifiedBuilder extends JexlBuilder {
	
	@Override
	public JexlEngine create() {
		return new ModifiedEngine(this);
	}

}
