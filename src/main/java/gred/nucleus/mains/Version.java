package gred.nucleus.mains;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Properties;


public class Version {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	private Version() {
		// DO NOTHING
	}
	
	
	public static String get() {
		
		final Properties properties = new Properties();
		String           version    = "undefined";
		try {
			properties.load(Version.class.getClassLoader().getResourceAsStream("nucleusj.properties"));
			version = properties.getProperty("version");
		} catch (IOException e) {
			LOGGER.error("Could not retrieve NucleusJ version.", e);
		}
		
		return version;
	}
	
}
