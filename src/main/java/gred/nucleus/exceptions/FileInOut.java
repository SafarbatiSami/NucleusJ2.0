package gred.nucleus.exceptions;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


public class FileInOut extends Exception {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	public FileInOut(String fileName) {
		LOGGER.error("File {} already exist ", fileName);
	}
	
}


