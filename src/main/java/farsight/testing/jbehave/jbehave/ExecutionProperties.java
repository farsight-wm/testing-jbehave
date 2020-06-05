package farsight.testing.jbehave.jbehave;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import farsight.testing.jbehave.Scope;

public class ExecutionProperties {
	
	private static final Logger logger = LogManager.getLogger(ExecutionProperties.class);
	
	private EnumMap<Scope, Properties> scopeProps = new EnumMap<>(Scope.class);
	
	public ExecutionProperties() {
		this(loadProperties());
	}
	
	public ExecutionProperties(Properties testProperties) {
		scopeProps.put(Scope.Test, testProperties);
		reset(Scope.Story);
	}
	
	public void reset(Scope scope) {
		switch (scope) {
		case Test:
		//	scopeProps.put(Scope.Test, loadProperties()); //never reset test properties; this are loaded only once!
		case Story:
			scopeProps.put(Scope.Story, new Properties());
		case Scenario:
			scopeProps.put(Scope.Scenario, new Properties());
		}
	}
	
	private static Properties loadProperties() {
		String jasyptPassword = System.getProperty("wmaopkey");
		Properties props;
		if (jasyptPassword == null) {
			//logger.info("Property password environment variable 'wmaopkey' not found.  Properties will not be decrypted.");
			props = new Properties();
		} else {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(jasyptPassword);
			props = new EncryptableProperties(encryptor);
		}

		// Attempt to load config.properties file
		String propertiesFilename = System.getProperty("wm.config.filename", "config.properties");
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try (InputStream resourceStream = loader.getResourceAsStream(propertiesFilename)) {
			props.load(resourceStream);
		} catch (IOException e) {
			logger.error("Failed to read " + propertiesFilename + " file: " + e.toString());
		} catch (Exception e) {
			logger.warn("Could not find " + propertiesFilename + " file. Using system properties and default values.");
		}

		// System properties override config file properties
		props.putAll(System.getProperties());
		return props;
	}
	
	public Properties getProperties(Scope scope) {
		return scopeProps.get(scope);
	}
	
	public String get(String key) {
		return get(key, null);
	}
	
	public String get(String key, String defaultValue) {
		for(Scope scope: Scope.values()) {
			String value = scopeProps.get(scope).getProperty(key);
			if(value != null)
				return value;
		}
		return defaultValue;
	}
	
	public void set(String key, String value, Scope scope) {
		scopeProps.get(scope).setProperty(key, value);
	}
	
	public void set(String key, String value) {
		set(key, value, Scope.Scenario);
	}


}
