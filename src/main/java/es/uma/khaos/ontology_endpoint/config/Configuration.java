package es.uma.khaos.ontology_endpoint.config;

import java.util.Properties;

public final class Configuration {
	
	private Properties properties = null;
	private static Configuration instance = null;
	
	private Configuration() {
		this.properties = new Properties();
		try {
			properties.load(
					Thread.currentThread().getContextClassLoader()
							.getResourceAsStream(Constants.APP_PROP_PATH));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private synchronized static void createInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
	}
	
	public static String getProperty(String key) {
		String result = null;
		if (instance == null) {
			createInstance();
		}
		if (key != null && !key.trim().isEmpty()) {
			result = instance.properties.getProperty(key);
		}
		return result;
	}
	
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

}
