package farsight.testing.jbehave.junit.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;

public class DescriptionBuilder {

	private static final int MAX_STEP_NAME_LENGHT = 80;
	private final Description root;
	private final Class<?> testClass;
	private final DescriptionBuilder parent;
	
	private static long serialNo = 0;
	
	public DescriptionBuilder(Class<?> testClass, String name) {
		this(null, testClass, name);
	}
	
	private DescriptionBuilder(DescriptionBuilder parent, Class<?> testClass, String name) {
		root = Description.createSuiteDescription(name, uniqueId());
		this.testClass = testClass;
		this.parent = parent;
	}
	
	private static Long uniqueId() {
		return serialNo++;
	}

	public Description build() {
		return root;
	}

	public DescriptionBuilder addGroup(String name) {
		DescriptionBuilder child = new DescriptionBuilder(this, testClass, name);
		root.addChild(child.root);
		return child;
	}
	
	public DescriptionBuilder add(String name) {
		// since we may have steps with identical names, we need to use the
		// suite description, that can have a unique id!
		root.addChild(Description.createSuiteDescription(name, uniqueId()));
		return this;
	}
	
	public Description append(String name) {
		// since we may have steps with identical names, we need to use the
		// suite description, that can have a unique id!
		Description child = Description.createSuiteDescription(name, uniqueId());
		root.addChild(child);
		return child;
	}
	
	public DescriptionBuilder parent() {
		return parent;
	}
	
	public static DescriptionBuilder create(Class<?> testClass, String name) {
		return new DescriptionBuilder(testClass, name);
	}
	
	public static String junitSaveName(String step) {
		// replace all whitespace by single space
		step = step.replaceAll("\\s+", " ");
		//replace round brackets, because they are not displayed in eclipse junit tree 
		step = step.replace('(', '\uff08');
		step = step.replace(')', '\uff09');

		if (step.length() > MAX_STEP_NAME_LENGHT)
			step = step.substring(0, MAX_STEP_NAME_LENGHT - 6) + " [...]";

		return step;
	}

	public static List<Description> getLeafs(Description description) {
		ArrayList<Description> leafs = new ArrayList<>();
		getLeafs(description, leafs);
		return leafs;
	}
	
	private static void getLeafs(Description description, List<Description> leafs) {
		//DFS search for leafs
		for(Description child: description.getChildren()) {
			if(child.isTest())
				leafs.add(child);
			else
				getLeafs(child, leafs);
		}
	}
}
