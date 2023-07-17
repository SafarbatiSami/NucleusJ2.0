package gred.nucleus.autocrop;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.Locale;


public class AutocropTestRunner {
	public static final String PATH_TO_AUTOCROP = "autocrop/";
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	public static int getNumberOfImages(String dir) {
		int nImages = 0;
		File[] files = new File(dir + PATH_TO_AUTOCROP).listFiles();
		if(files != null) {
			for (File file : files) {
				String extension = FilenameUtils.getExtension(file.getName())
				                                .toLowerCase(Locale.ROOT);
				if (file.isFile() && extension.equals("tif")) {
					nImages++;
				}
			}
		}
		LOGGER.debug("{} image(s) found in autocrop folder.", nImages);
		return nImages;
	}
	
	
	public static void run(String dir) {
		File   file  = new File(dir + PATH_TO_AUTOCROP);
		File[] files = file.listFiles();
		LOGGER.info("Running test on directory: {}", dir + PATH_TO_AUTOCROP);
		
		if (files != null) {
			for (File f : files) {
				String name = f.getName();
				
				if (f.isDirectory()) {
					LOGGER.info("Directory skipped: {}", name);
				} else {
					String extension = FilenameUtils.getExtension(name).toLowerCase(Locale.ROOT);
					if (!extension.equals("tif")) {
						LOGGER.info("File of type " + extension + " skipped");
					} else {
						LOGGER.info("Beginning process on: {}", name);
						runAutoCrop(f.toString(), AutoCropTest.PATH_TO_OUTPUT
						                          + PATH_TO_AUTOCROP
						                          + name);
						LOGGER.info("Finished process on: {}", name);
						LOGGER.info("Checking results:");
						AutocropTestChecker checker = new AutocropTestChecker(name);
						checker.checkValues(f);
					}
				}
			}
		}
	}
	
	
	private static void runAutoCrop(String imageSourceFile, String output) {
		AutocropParameters autocropParameters = new AutocropParameters(imageSourceFile, output);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		autoCrop.runFile(imageSourceFile);
		autoCrop.saveGeneralInfo();
	}
	
}
