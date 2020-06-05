package farsight.testing.jbehave.jexl;

import java.util.Map;

import org.assertj.core.api.AbstractAssert;

import com.wm.data.IData;

import farsight.testing.jbehave.utils.IDataMatchTool;
import farsight.testing.utils.jexl.context.WmIDataContext;
import farsight.utils.idata.DataBuilder;

public class IDataAssert extends AbstractAssert<IDataAssert, IData> {

	protected final DataBuilder builder;
	
	public IDataAssert(IData actual) {
		this(actual, IDataAssert.class);
	}
	
	public IDataAssert(IData actual, Class<?> selfType) {
		super(actual, selfType);
		builder = DataBuilder.wrap(actual);
	}
	
	public IDataAssert containsKey(String key) {
		if(!builder.containsKey(key)) {
			failWithMessage("Should contain the key <%s>", key);
		}
		return this;
	}
	
	public IDataAssert isNotEmpty() {
		if(builder.isEmpty()) {
			failWithMessage("Should not be empty");
		}
		return this;
	}
	
	public IDataAssert isEmpty() {
		if(!builder.isEmpty()) {
			failWithMessage("Should be empty");
		}
		return this;
	}
	
	public IDataAssert matches(IData other) {
		isNotNull();
		try {
			IDataMatchTool.assertMatches(actual, other);
		} catch (AssertionError e) {
			//wrap message
			failWithMessage(e.getMessage());
		}
		return this;
	}
	
	public IDataAssert matches(Map<Object, Object> map) {
		return matches(WmIDataContext.transform(map));
	}
	
	public IDataAssert matchesExactly(IData other) {
		isNotNull();
		try {
			IDataMatchTool.assertExactlyMatches(actual, other);
		} catch (AssertionError e) {
			//wrap message
			failWithMessage(e.getMessage());
		}
		return this;
	}
	
	public IDataAssert matchesExactly(Map<Object, Object> map) {
		return matchesExactly(WmIDataContext.transform(map));
	}
	
	public FluentWmAssert path(String path) {
		return new FluentWmAssert(builder.read(path)).as(info.description() + " + /" + path);
	}
	


}
