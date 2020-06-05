package farsight.testing.jbehave.jexl;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;

import com.wm.data.IData;

import farsight.testing.jbehave.utils.IDataMatchTool;

public class WmAssert extends Assert {
	
	private static class TypeMatcher extends BaseMatcher<Object> {
		
		private final Class<?> type;
		
		public TypeMatcher(Class<?> type) {
			this.type = type;
		}

		@Override
		public boolean matches(Object object) {
			return type.isInstance(object);
		}

		@Override
		public void describeTo(Description description) {
			//FIXME IDATA[] will not be pretty!
			description.appendText("Object is not an instance of type: " + type.getCanonicalName());
		}
		
	}
	
	private static final TypeMatcher DOCUMENT_MATCHER = new TypeMatcher(IData.class);
	private static final TypeMatcher DOCUMENT_LIST_MATCHER = new TypeMatcher(IData[].class);
	
	public static final WmAssert INSTANCE = new WmAssert();
	
	
	//shortcuts
	
	public static void equals(Object expected, Object actual) {
		assertEquals(expected, actual);
	}
	
	public static void equals(String message, Object expected, Object actual) {
		assertEquals(message, expected, actual);
	}
	
	public static void notEquals(Object expected, Object actual) {
		assertNotEquals(expected, actual);
	}
	
	public static void notEquals(String message, Object expected, Object actual) {
		assertNotEquals(message, expected, actual);
	}
	
	public static void isTrue(Boolean condition) {
		assertTrue(condition);
	}
	
	public static void isTrue(String message, Boolean condition) {
		assertTrue(message, condition);
	}
	
	public static void isTrue(String value) {
		assertEquals("true", value);
	}

	public static void isTrue(String message, String value) {
		assertEquals(message, "true", value);
	}

	public static void isFalse(Boolean condition) {
		assertFalse(condition);
	}
	
	public static void isFalse(String message, Boolean condition) {
		assertFalse(message, condition);
	}

	public static void isFalse(String value) {
		assertEquals("false", value);
	}

	public static void isFalse(String message, String value) {
		assertEquals(message, "false", value);
	}
	
	public static void isNull(Object object) {
		assertNull(object);
	}
	
	public static void isNull(String message, Object object) {
		assertNull(message, object);
	}

	public static void isNotNull(Object object) {
		assertNotNull(object);
	}
	
	public static void isNotNull(String message, Object object) {
		assertNotNull(message, object);
	}
	
	
	// convenience
	
	public static void isDocument(Object object) {
		assertThat(object, DOCUMENT_MATCHER); 
	}
	
	public static void isDocument(String message, Object object) {
		assertThat(message, object, DOCUMENT_MATCHER); 
	}

	public static void isDocumentList(Object object) {
		assertThat(object, DOCUMENT_LIST_MATCHER); 
	}

	public static void isDocumentList(String message, Object object) {
		assertThat(message, object, DOCUMENT_LIST_MATCHER); 
	}
	
	public static <T> void contains(T[] array, T value) {
		contains("Element does not exist in array", array, value);
	}
	
	public static <T> void contains(String message, T[] array, T value) {
		boolean found = false;
		for(T item: array) {
			if((value == null && item == null) || (value != null && value.equals(item))) {
				found = true;
				break;
			}
		}
		if(!found)
			fail(message);
	}
	
	public static void matches(IData expected, IData actual) {
		IDataMatchTool.assertMatches(actual, expected);
	}
	
	public static void matches(String message, IData expected, IData actual) {
		try {
			IDataMatchTool.assertMatches(actual, expected);
		} catch(AssertionError e) {
			throw new AssertionError(message, e);
		}
	}
	
	public static void exactlyMatches(IData expecetd, IData actural) {
		IDataMatchTool.assertExactlyMatches(actural, expecetd);
	}
	
	public static void exactlyMatches(String message, IData expecetd, IData actural) {
		try {
			IDataMatchTool.assertExactlyMatches(actural, expecetd);
		} catch(AssertionError e) {
			throw new AssertionError(message, e);
		}
	}
			
}
