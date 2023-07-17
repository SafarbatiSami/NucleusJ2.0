package gred.nucleus.other;

import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import loci.formats.FormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;


public class SegmentationImagesIntegration {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	/*
	 * @param img
	 * @param vMin
	 * @param vMax
	 * @param outputImgString
	 */
/*
 public static void testStupid(ImagePlus img, short vMin, int vMax, String outputImgString ) throws FormatException {
 SegmentationParameters segmentationParameters = new SegmentationParameters();
 SegmentationCalling otsuModified = new SegmentationCalling(img, vMin, vMax, outputImgString);
 otsuModified.runSeveralImages2();
 }
 */
	
	
	/**
	 * @param input
	 * @param output
	 */
	public static void testStupidSeveralImages(String input, String output) {
		
		SegmentationParameters segmentationParameters = new SegmentationParameters(input, output);
		SegmentationCalling    otsuModified           = new SegmentationCalling(segmentationParameters);
		try {
			String log = otsuModified.runSeveralImages2();
			if (!(log.equals(""))) {
				LOGGER.error("Nuclei which didn't pass the segmentation\n{}", log);
			}
		} catch (IOException e) {
			LOGGER.error("I/O exception.", e);
		} catch (FormatException e) {
			LOGGER.error("Format exception", e);
		}
	}
	
	
	public static void testStupidSeveralImages(String input, String output, String config) {
		
		SegmentationParameters segmentationParameters = new SegmentationParameters(input, output, config);
		SegmentationCalling    otsuModified           = new SegmentationCalling(segmentationParameters);
		try {
			String log = otsuModified.runSeveralImages2();
			if (!(log.equals(""))) {
				LOGGER.error("Nuclei which didn't pass the segmentation\n{}", log);
			}
		} catch (IOException e) {
			LOGGER.error("I/O exception.", e);
		} catch (FormatException e) {
			LOGGER.error("Format exception", e);
		}
	}
	
	
	/**
	 * Main function of the package's tests.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		String pathToTest   = "/home/tridubos/Bureau/IMAGES_TEST/";
		String pathToOutput = "/home/tridubos/Bureau/IMAGES_TEST/AUTOCROP";
		
		testStupidSeveralImages(pathToTest + "/SEGMENTATION/Gros_Nucleols",
		                        pathToTest + "/SEGMENTATION/SEGMENTATION_RESULTS/Gros_Nucleols");
		testStupidSeveralImages(pathToTest + "/SEGMENTATION/Noyaux_Calib_1_1_1",
		                        pathToTest + "/SEGMENTATION/SEGMENTATION_RESULTS/Noyaux_Calib_1_1_1",
		                        pathToTest + "/SEGMENTATION/Noyaux_Calib_1_1_1/config_calibration.txt");
		testStupidSeveralImages(pathToTest + "/SEGMENTATION/PB_RADIUS_CONVEXHULL",
		                        pathToTest + "/SEGMENTATION/SEGMENTATION_RESULTS/PB_RADIUS_CONVEXHULL");
		
		// testStupidSeveralImages(ExpectedResult, ExpectedResult, (short)6.0, 300000000,true);
        /*fw.GetFilesResultingOfAnalysis(inputTristan);
        fw.CompareAnalysisResult();
        OutputFileVerification fw = new OutputFileVerification();
        fw.GetFileResultExpected(ExpectedResult);
        fw.GetFilesOutputFolder(outputTristan);
        fw.GetFilesResultingOfAnalysis(outputTristan);
        fw.CompareAnalysisResult();
        */
		LOGGER.info("The program ended normally.");
	}
	
}
