package farsight.testing.jbehave.jexl;


import com.wm.data.IData;

import farsight.testing.utils.jexl.IDataJexlContext;
import farsight.testing.utils.jexl.context.WmObject;

public class WmAssertJContext extends IDataJexlContext {
	
	
	private String pathPrefix;
	

	public WmAssertJContext(IData idata) {
		this(idata, "/");
	}

	public WmAssertJContext(IData idata, String pathPrefix) {
		super(idata);
		this.pathPrefix = pathPrefix; 
	}

	
	// generic fluent entry
	
	public void setPrefix(String prefix) {
		pathPrefix = prefix;
	}
	
	public FluentWmAssert then(Object actual) {
		//Strip Context
		if(actual instanceof WmObject)
			actual = ((WmObject)actual).getWmObject();
		return new FluentWmAssert(actual);
	}
	
	public FluentWmAssert path(String path) {
		return then(builder.read(path)).as(pathPrefix + path);
	}
	
	public <T> T dump(T object) {
		System.out.println("== WmAssertJContext = DUMP - Object ===========================");
		System.out.println("class(object): " + (object == null ? "null" : object.getClass().getCanonicalName()));
		System.out.println("---------------------------------------------------------------");
		System.out.println(object);
		System.out.println("===============================================================");
		return object;
	}
	
	// basic types
	



	

}
