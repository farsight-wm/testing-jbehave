package farsight.testing.jbehave.junit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

public class ConfigProperties {
	
	private static final Logger logger = LogManager.getLogger(ConfigProperties.class);
	private Properties props;
	
	private static ConfigProperties INSTANCE = null;
	
	
	private ConfigProperties(Properties props) {
		this.props = props;
	}
	
	public String get(String key, String defaultValue) {
		return props.getProperty(key, defaultValue);
	}
	
	public String get(String key) {
		return props.getProperty(key);
	}
	
	public Properties properties() {
		return props;
	}
	
	public static ConfigProperties load(String filename) {
		Properties props = loadProperties(filename);
		return new ConfigProperties(props);
	}
	
	public static ConfigProperties instance() {
		if(INSTANCE == null)
			INSTANCE = load(System.getProperty("wm.config.filename", "config.properties"));
		return INSTANCE;
	}
	
	private static Properties loadProperties(String propertiesFilename) {
		String jasyptPassword = System.getProperty("wmaopkey");
		Properties props;
		if (jasyptPassword == null) {
			logger.info("Property password environment variable 'wmaopkey' not found.  Properties will not be decrypted.");
			props = new Properties();
		} else {
			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(jasyptPassword);
			props = new EncryptableProperties(encryptor);
		}

		boolean loaded = false;
		
		//filesystem
		loaded = loaded || loadFromFilesystem(props, propertiesFilename);
		
		//classpath
		loaded = loaded  || loadFromClasspath(props, propertiesFilename);

		// System properties override config file properties
		props.putAll(System.getProperties());
		return props;
		
	}
	
	private static boolean loadFromClasspath(Properties props, String propertiesFilename) {	
		// Attempt to load config.properties file
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try (InputStream resourceStream = loader.getResourceAsStream(propertiesFilename)) {
			logger.info("Read " + propertiesFilename + " file from classpath");
			props.load(resourceStream);
		} catch (IOException e) {
			logger.error("Failed to read " + propertiesFilename + " file from classpath: " + e.toString());
			return false;
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	private static boolean loadFromFilesystem(Properties props, String propertiesFilename) {
		Path path = Paths.get(propertiesFilename);
		if(Files.exists(path) && Files.isRegularFile(path)) {
			try (InputStream resourceStream = Files.newInputStream(path)) {
				logger.info("Read " + propertiesFilename + " file from filysystem");
				props.load(resourceStream);
			} catch (IOException e) {
				logger.error("Failed to read " + propertiesFilename + " file from filysystem: " + e.toString());
				return false;
			} catch (Exception e) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}
	

}
