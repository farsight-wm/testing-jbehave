package farsight.testing.jbehave.utils;

import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;

public class AdviceIDManager {
	
	public static final char ADVICE_SEPARATOR = '/';
	
	//prefix used to make test capable of parallel runs!
	private String prefix;
	private HashSet<String> definedAdviceIDs = new HashSet<>();
	
	public AdviceIDManager() {
		reset();
	}
	
	private int countOccurences(String input) {
		int l = input.length(), c = 0;
		for(int i = 0; i < l; i++)
			if(input.charAt(i) == ADVICE_SEPARATOR)
				c++;
		return c;
	}
	
	private String buildAdviceID(String input, String section, String qualifier) {
		input = input.trim(); 
		int separators = countOccurences(input);
		if(qualifier == null) {
			return separators == 0 ? section + ADVICE_SEPARATOR + input : input;
		} else {
			switch (separators) {
			case 0: //only local part
				return section + ADVICE_SEPARATOR + input + ADVICE_SEPARATOR + qualifier;
			case 1: // + qualifier //TODO check if part1 = group?!
				return section + ADVICE_SEPARATOR + input;
			default:
				return input;
			}
		}
	}
	
	public String define(String input, String section) {
		return define(input, section, null);
	}
	
	public String define(String input, String section, String qualifier) {
		String adviceId = buildAdviceID(input, section, qualifier);
		if(!definedAdviceIDs.contains(adviceId)) {
			definedAdviceIDs.add(adviceId);
		}
		return prefix + adviceId;
	}
	
	public String access(String input, String section) {
		return access(input, section, null);
	}
	
	public String access(String input, String section, String qualifier) {
		String adviceId = buildAdviceID(input, section, qualifier);
		Assert.assertTrue("AdviceID was never defined: " + adviceId, definedAdviceIDs.contains(adviceId));
		return prefix + adviceId;
	}
	
	private String randomPrefix(int length) {
		String randNo = StringUtils.leftPad(Long.toHexString(Math.round(Math.random() * Long.MAX_VALUE)), length, "0");
		return randNo.substring(0, length) + ADVICE_SEPARATOR;
	}
	
	public void reset() {
		prefix = randomPrefix(8);
		definedAdviceIDs.clear();
	}
	
	public static void main(String[] args) {
		new AdviceIDManager();
	}
	

}
