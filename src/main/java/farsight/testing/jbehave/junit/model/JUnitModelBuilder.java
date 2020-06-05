package farsight.testing.jbehave.junit.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.model.Story;

import farsight.testing.jbehave.junit.model.LazyBuilder.LazyScenarioBuilder;

public class JUnitModelBuilder {
	
	private static final Logger logger = LogManager.getLogger(JUnitModelBuilder.class);
	private static final int MAX_DEPTH = 10;
	
	private final Configuration configuration;
	
	private HashMap<String, JUnitStoryBuilder> storyBuilderMap = new HashMap<>();

	public JUnitModelBuilder(Configuration configuration) {
		this.configuration = configuration;
	}
	
	// public API
	
	public JUnitStory parseStory(String storyPath, DescriptionBuilder builder) {
		return getStoryBuilder(storyPath).build(builder, MAX_DEPTH);
	}
	
	public List<Story> parseStories(List<String> storyPaths, DescriptionBuilder builder) {
		logger.debug("parseStores storyPaths=" + storyPaths);
		ArrayList<Story> stories = new ArrayList<>();
		for(String storyPath: storyPaths) {
			stories.add(parseStory(storyPath, builder));
		}
		return stories;
	}
	
	protected JUnitStoryBuilder getStoryBuilder(String storyPath) {
		//already loaded?
		JUnitStoryBuilder result = storyBuilderMap.get(storyPath);
		if(result == null) {
			try {
				logger.info("Loading story: " + storyPath);
				String storyAsText = configuration.storyLoader().loadStoryAsText(storyPath);
				Story rawStory = configuration.storyParser().parseStory(storyAsText, storyPath);
				result = new JUnitStoryBuilder(this, rawStory);
				storyBuilderMap.put(storyPath, result);
			} catch(Exception e) {
				logger.error("Unable to load story: " + storyPath, e);
				throw e;
			}
		}
		return result;
	}
	
	// API for builders

	protected LazyScenarioBuilder findReference(String origin, String file, String scenario) {
		String storyPath, path = null;
		if (file.equals("#")) {
			// self reference
			storyPath = origin;			
		} else if (file.startsWith("@")) {
			//include -- pass through
			if (!file.endsWith(".story")) {
				file += ".inc.story";
			}
			storyPath = file;
		} else {
			int pos = file.lastIndexOf('/');
			if (pos > 0) {
				path = file.substring(0, pos);
				file = file.substring(pos + 1);
			} else {
				pos = origin.lastIndexOf('/');
				if (pos > 0)
					path = origin.substring(0, pos);
			}
			// check ending
			if (!file.endsWith(".story")) {
				file += ".inc.story";
			}
			storyPath = (path == null ? "" : path + "/") + file;
		}
		try {
			return getStoryBuilder(storyPath).getScenarioBuilder(scenario);
		} catch (Exception e) {
			logger.error("Unable to load scenario: " + scenario + " from: " + storyPath , e);
			throw e;
		}
	}

}
