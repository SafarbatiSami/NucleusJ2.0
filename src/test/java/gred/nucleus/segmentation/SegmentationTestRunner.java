package gred.nucleus.segmentation;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.Locale;


public class SegmentationTestRunner {
	public static final String PATH_TO_SEGMENTATION = "segmentation/";
	public static final String PATH_TO_CONFIG       = SegmentationTest.PATH_TO_INPUT +
	                                                  PATH_TO_SEGMENTATION +
	                                                  "config" + File.separator +
	                                                  "seg.config";
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	public static int getNumberOfImages(String dir) {
		int    nImages = 0;
		File[] files  = new File(dir + PATH_TO_SEGMENTATION).listFiles();
		if (files != null) {
			for (File file : files) {
				String extension = FilenameUtils.getExtension(file.getName())
				                                .toLowerCase(Locale.ROOT);
				if (file.isFile() && extension.equals("tif")) {
					nImages++;
				}
			}
		}
		LOGGER.debug("{} image(s) found in segmentation folder.", nImages);
		return nImages;
	}
	
	
	public static void run(String dir) throws Exception {
		File   file  = new File(dir + PATH_TO_SEGMENTATION);
		File[] files = file.listFiles();
		LOGGER.info("Running test on directory: {}", dir + PATH_TO_SEGMENTATION);
		
		if (files != null) {
			for (File f : files) {
				String name = f.getName();
				
				if (f.isDirectory()) {
					LOGGER.info("Directory skipped: {}", name);
				} else {
					String extension = FilenameUtils.getExtension(name).toLowerCase(Locale.ROOT);
					if (!extension.equals("tif")) {
						LOGGER.info("File of type {} skipped", extension);
					} else {
						LOGGER.info("Beginning process on: {}", name);
						runSegmentation(f.toString(),
						                SegmentationTest.PATH_TO_OUTPUT + PATH_TO_SEGMENTATION + name,
						                PATH_TO_CONFIG);
						LOGGER.info("Finished process on: {}", name);
						
						LOGGER.info("Checking results:");
						SegmentationTestChecker checker = new SegmentationTestChecker(name);
						checker.checkValues(f);
					}
				}
			}
		}
	}
	
	
	private static void runSegmentation(String imageSourceFile, String output, String configFile) throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(imageSourceFile, output, configFile);
		SegmentationCalling    segmentation           = new SegmentationCalling(segmentationParameters);
		segmentation.runOneImage(imageSourceFile);
		segmentation.saveCropGeneralInfo();
	}
	
	
}
