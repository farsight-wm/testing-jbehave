package farsight.testing.jbehave.utils;

import static org.junit.Assert.fail;

import com.wm.data.IData;
import com.wm.data.IDataCursor;
import com.wm.data.IDataUtil;

public class IDataMatchTool {
	

	public static void assertMatches(IData document, IData potential, boolean exactly) {
		if(exactly)
			assertExactlyMatches(document, potential);
		else
			assertMatches(document, potential);
	}

	public static void assertMatches(IData document, IData potential, boolean exactly, String path) {
		if(exactly)
			assertExactlyMatches(document, potential, path);
		else
			assertMatches(document, potential, path);
	}
	
	public static void assertMatches(IData document, IData potential) {
		assertMatches(document, potential, null);
	}

	public static void assertMatches(IData document, IData potential, String path) {
		matches(document, potential, true, path);
	}
	
	public static void assertExactlyMatches(IData document, IData potential) {
		assertExactlyMatches(document, potential, null);
	}
	
	public static void assertExactlyMatches(IData document, IData potential, String path) {
		exactlyMatches(document, potential, true, path);
	}
	
	public static boolean matches(IData document, IData potential) {
		return matches(document, potential, false, null);
	}
	
	public static boolean exactlyMatches(IData document, IData potential) {
		return exactlyMatches(document, potential, false, null);
	}
	
	
	public static boolean matches(IData document, IData potential, boolean reportFail) {
		return matches(document, potential, reportFail, null);
	}
	
	public static boolean matches(IData document, IData potential, boolean reportFail, String path) {
		IDataCursor pc = potential.getCursor();
		IDataCursor dc = document.getCursor();
		while (pc.next()) {
			String key = pc.getKey();
			String subpath = path == null ? key : (path + "." + key);
			Object docObj = IDataUtil.get(dc, key);
			Object potObj = pc.getValue();
			if (docObj instanceof IData && potObj instanceof IData) {
				if (!isIDataMatch(subpath, reportFail, key, docObj, potObj)) {
					return false;
				}
			} else if (docObj instanceof IData[]) {
				if (!isIDataArrayMatch(subpath, key, docObj, potObj)) {
					return false;
				}
			} else {
				if (!isObjectMatch(subpath, reportFail, key, docObj, potObj)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean exactlyMatches(IData document, IData potential, boolean reportFail, String path) {
		IDataCursor pc = potential.getCursor();
		IDataCursor dc = IDataUtil.clone(document).getCursor();
		while (pc.next()) {
			String key = pc.getKey();
			String subpath = path == null ? key : (path + "/" + key);
			if(!dc.first(key)) {
				//key missing
				fail("Failed to locate element: " + subpath);
			}
			Object docObj = dc.getValue();
			Object potObj = pc.getValue();
			//remove from (cloned) dc!
			dc.delete();
			if (docObj instanceof IData && potObj instanceof IData) {
				if (!exactlyMatches((IData)docObj, (IData)potObj, reportFail, subpath)) {
					return false;
				}
			} else if (docObj instanceof IData[]) {
				if (!isExactIDataArrayMatch(docObj, potObj, reportFail, subpath)) {
					return false;
				}
			} else {
				if (!isObjectMatch(subpath, reportFail, key, docObj, potObj)) {
					return false;
				}
			}
		}
		if(dc.first()) {
			//still data that is unmatched
			String subpath = path == null ? dc.getKey() : (path + "/" + dc.getKey()); 
			fail("Failed to locate exising elemint in expected document: " + subpath);
		}
		
		return true;
	}


	private static boolean isObjectMatch(String path, boolean reportFail, String key, Object docObj, Object potObj) {
		if (docObj == null && potObj == null) {
			return true;
		}
		if (docObj == null || potObj == null) {
			if (reportFail) {
				fail("Failed to locate element: " + path);
			}
			return false;
		}
		Object docVal;
		if (docObj instanceof IData) {
			IDataCursor cursor = ((IData) docObj).getCursor();
			docVal = IDataUtil.get(cursor, "*body");
			cursor.destroy();
		} else {
			docVal = docObj;
		}
		// Bug fix: Accomodate if strings have different line breaks (\n\r and \n)
		if(docVal instanceof String[][] && potObj instanceof String[][]){
			for(String[] a: (String[][]) potObj){
				for(String b: a){
					b = b.replaceAll("\r", "");
				}
			}
			for(String[] a: (String[][]) docVal){
				for(String b: a){
					b = b.replaceAll("\r", "");
				}
			}
		}
		else if(docVal instanceof String[] && potObj instanceof String[]){
			for(String a: (String[]) potObj){
				a = a.replaceAll("\r", "");
			}
			for(String a: (String[]) docVal){
				a = a.replaceAll("\r","");
			}
		}
		else if(docVal instanceof String && potObj instanceof String){
			potObj = (Object) ((String) potObj).replaceAll("\r", "");
			docVal = (Object) ((String) docVal).replaceAll("\r", "");
		}
		//End bug fix
		if (!docVal.equals(potObj)) {
			if (reportFail) {
				fail("Element " + path + " has actual value of '" + docObj + "' but test value of '"
						+ potObj + "'");
			}
			return false;
		}
		return true;
	}
	
	private static void assertEqualSizes(Object[] docArr, Object[] potArr, String path) {
		if(docArr == null || potArr == null)
			fail("Failed to match " + path + "; Array cannot be null");
		if(docArr.length != potArr.length)
			fail("Array " + path + " has a size of " + docArr.length + "but test as a length of " + potArr.length);
	}
	
	private static boolean isExactIDataArrayMatch(Object docObj, Object potObj, boolean reportFail, String path) {
		IData[] docArr = (IData[]) docObj;
		if(!(potObj instanceof IData[]))
			fail("Failed to match " + path + "; field is not a document list");
		IData[] potArr = (IData[]) potObj;
		assertEqualSizes(docArr, potArr, path);
		
		for(int i = 0; i < docArr.length; i++) {
			IData doc = docArr[i], pot = potArr[i];
			String elPath =  path + "[" + i + "]";
			if(doc == null ^ pot == null)
				if(reportFail)
					fail("Failed to locate element " + elPath);
				else
					return false;
			if(doc != null && pot != null)
				if(!exactlyMatches(doc, pot, true, elPath))
					return false;
		}
		return true;
	}

	private static boolean isIDataArrayMatch(String path, String key, Object docObj, Object potObj) {
		IData[] potArr = (potObj instanceof IData[]) ? ((IData[]) potObj) : new IData[] { (IData) potObj };
		for (IData pot : potArr) {
			boolean potMatch = false;
			for (IData doc : (IData[]) docObj) {
				if ((doc == null && pot == null) || (pot != null && matches(doc, pot, false, path))) {
					potMatch = true;
					break;
				}
			}
			if (!potMatch) {
				fail("Failed to match " + path + " no mating element found.");
				return false;
			}
		}
		return true;
	}

	private static boolean isIDataMatch(String path, boolean reportFail, String key, Object docObj, Object potObj) {
		if (!matches((IData) docObj, (IData) potObj, reportFail, path)) {
			if (reportFail) {
				fail("Failed to match values for " + path);
			}
			return false;
		}
		return true;
	}
}
