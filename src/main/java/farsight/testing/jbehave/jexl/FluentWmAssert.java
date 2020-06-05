package farsight.testing.jbehave.jexl;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractDateAssert;
import org.assertj.core.api.AbstractDoubleAssert;
import org.assertj.core.api.AbstractFloatAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractLongAssert;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Condition;
import org.assertj.core.api.Descriptable;
import org.assertj.core.api.ObjectArrayAssert;
import org.assertj.core.error.BasicErrorMessageFactory;
import org.assertj.core.error.ErrorMessageFactory;
import org.assertj.core.error.ShouldBeInstance;

import com.wm.data.IData;

//public class FluentWmAssert extends AbstractAssert<FluentWmAssert, Object> {
public class FluentWmAssert extends AbstractObjectAssert<FluentWmAssert, Object> {

	public static class ShouldBeLikeType extends BasicErrorMessageFactory {

		private ShouldBeLikeType(Object actual, String likeWhat) {
			super("%nExpecting:%n <%s>%nto be like " + likeWhat + "%n", actual);
		}

		public static ErrorMessageFactory shouldBeLike(Object actual, String likeWhat) {
			return new ShouldBeLikeType(actual, likeWhat);
		}
	}

	public static enum Type {
		Boolean(Boolean.class, "true|false"),
		Integer(Integer.class, "-?\\d+"),
		Long(Long.class, "-?\\d+"),
		Float(Float.class, "(+|-)?\\d*(\\.\\d*)?"),
		Double(Double.class, "(+|-)?\\d*(\\.\\d*)?"),
		Date(Date.class, ".*"); //FIXME

		public final Class<?> type;
		public final String expression;

		private Type(Class<?> type, String expression) {
			this.type = type;
			this.expression = expression;
		}
	}
	
	
	//some helpful conditions
	
	public static final Condition<Object> IS_NULL = new Condition<>(m -> m == null, "isNull");
	public static final Condition<Object> IS_EMPTY = new Condition<>(m -> "".equals(m), "isEmpty");
	

	protected final String string;
	protected final boolean isString;

	public FluentWmAssert(Object actual) {
		super(actual, FluentWmAssert.class);
		isString = actual instanceof String;
		string = isString ? (String) actual : null;
	}

	@SuppressWarnings("unchecked")
	private <T extends Descriptable<?>> T describe(T assertion) {
		return (T) assertion.as(info.description());
	}
	
	private boolean checkString(Type type) {
		if (isString) {
			if (!string.matches(type.expression))
				failWithMessage(ShouldBeLikeType.shouldBeLike(actual, type.type.getSimpleName()).create());
		} else {
			if(actual != null)
				isInstanceOf(type.type);
		}
		return isString;
	}
	
	private Object forceType(Type type) {
		//is there a way to cast based on a type stored in enum?! I guess not, but at least this is chainable
		return forceType(type.type);
	}
	
	private Object forceType(Class<?> type) {
		if(actual != null)
			isInstanceOf(type);
		return actual;
	}
	
	// utils
	
	public FluentWmAssert dump() {
		System.out.println("== FluentWmAssert =============================================");
		System.out.println("class(actual): " + (actual == null ? "null" : actual.getClass().getCanonicalName()));
		System.out.println("---------------------------------------------------------------");
		System.out.println(actual);
		System.out.println("===============================================================");
		return this;
	}
	
	public FluentWmAssert isNullOrEmpty() {
		return is(anyOf(IS_NULL, IS_EMPTY));
	}
	
	public FluentWmAssert isNotNullOrEmpty() {
		return is(not(anyOf(IS_NULL, IS_EMPTY)));
	}
	
	
	// type assuming

	public AbstractStringAssert<?> isString() {
		isNotNull();
		isInstanceOf(String.class);
		return describe(assertThat((String) actual));
	}

	public AbstractBooleanAssert<?> isBoolean() {
		return describe(assertThat((Boolean) forceType(Type.Boolean)));
	}

	public AbstractBooleanAssert<?> asBoolean() {
		return describe(assertThat(checkString(Type.Boolean) ? Boolean.parseBoolean(string) : (Boolean) actual));
	}
	
	public AbstractBooleanAssert<?> asBool() {
		return asBoolean();
	}
	
	public AbstractIntegerAssert<?> isInteger() {
		return describe(assertThat((Integer) forceType(Type.Integer)));
	}

	public AbstractIntegerAssert<?> asInteger() {
		return describe(assertThat(checkString(Type.Integer) ? Integer.parseInt(string, 10) : (Integer) actual));
	}
	
	public AbstractIntegerAssert<?> asInt() {
		return asInteger();
	}

	public AbstractLongAssert<?> isLong() {
		return describe(assertThat((Long) forceType(Type.Long)));
	}

	public AbstractLongAssert<?> asLong() {
		return describe(assertThat(checkString(Type.Long) ? Integer.parseInt(string, 10) : (Long) actual));
	}
	
	public AbstractFloatAssert<?> isFloat() {
		return describe(assertThat((Float) forceType(Type.Float)));
	}
	
	public AbstractFloatAssert<?> asFloat() {
		return describe(assertThat(checkString(Type.Float) ? Float.parseFloat(string) : (Float) actual));
	}
	
	public AbstractDoubleAssert<?> isDouble() {
		return describe(assertThat((Double) forceType(Type.Double)));
	}
	
	public AbstractDoubleAssert<?> asDouble() {
		return describe(assertThat(checkString(Type.Double) ? Float.parseFloat(string) : (Double) actual));
	}
	
	public AbstractDateAssert<?> isDate() {
		return describe(assertThat((Date) forceType(Type.Date)));
	}
	
	public AbstractDateAssert<?> asDate() {
		//FIXME implemnt String parsing of typical WM String format?!
		return isDate();
	}
	
	public FluentWmAssert asObject() {
		return this;
	}
	
	public FluentWmAssert isObject() {
		return this;
	}
	
	public ObjectArrayAssert<String> isStringArray() {
		if(actual == null)
			return assertThat((String[])null);
		forceType(String[].class);
		return assertThat((String[])actual);
	}
	
	public ObjectArrayAssert<String> asStringArray() {
		return isStringArray();
	}
	
	public ObjectArrayAssert<Object> isArray() {
		if(actual == null)
			return assertThat((Object[])null);
		return assertThat((Object[])actual);
	}
	
	public ObjectArrayAssert<Object> asArray() {
		return isArray();
	}
	
	

	
//	FIXME ArrayTypes? only for String?!
	
	
	private void isAssignableFrom(Class<?> type) {
		if(actual != null && !type.isAssignableFrom(actual.getClass())) {
			failWithMessage(ShouldBeInstance.shouldBeInstance(actual, type).create());
		}
	}
	
	public IDataAssert isIData() {
		isAssignableFrom(IData.class);
		return describe(new IDataAssert((IData) actual));
	}
	
	public IDataAssert asIData() {
		//no string representation!
		return isIData();
	}
	
	public IDataAssert asDocument() {
		//shortcut
		return isIData();
	}

	public IDataAssert asDoc() {
		//shortcut
		return isIData();
	}
	
	


}
