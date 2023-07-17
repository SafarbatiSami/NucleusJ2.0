package gred.nucleus.segmentation;

import ij.ImagePlus;
import ij.plugin.ImageCalculator;
import ij.process.StackStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class SegmentationTestChecker {
	public static final String PATH_TO_INFO   = "OTSU/result_Segmentation_Analyse_OTSU.csv";
	public static final String PATH_TO_TARGET = "target/";
	public static final String PATH_TO_RESULT = "OTSU/";
	
	public static final int PERCENT_MASK_OVERLAPPED = 5;
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private SegmentationResult target;
	
	
	public SegmentationTestChecker(String targetPath) {
		File targetFile = new File(SegmentationTest.PATH_TO_INPUT +
		                           SegmentationTestRunner.PATH_TO_SEGMENTATION +
		                           PATH_TO_TARGET +
		                           targetPath + File.separator +
		                           PATH_TO_INFO
		);
		String resultPath = SegmentationTest.PATH_TO_INPUT +
		                    SegmentationTestRunner.PATH_TO_SEGMENTATION +
		                    PATH_TO_TARGET +
		                    targetPath + File.separator +
		                    PATH_TO_RESULT + targetPath;
		
		target = extractGeneralInfo(new SegmentationResult(), targetFile);
		target = extractResult(target, resultPath);
	}
	
	
	public File getInfoFile(File file) {
		return new File(SegmentationTest.PATH_TO_OUTPUT +
		                SegmentationTestRunner.PATH_TO_SEGMENTATION +
		                file.getName() + File.separator +
		                PATH_TO_INFO);
	}
	
	
	public SegmentationResult extractGeneralInfo(SegmentationResult result, File file) {
		List<String> list = new ArrayList<>();
		try {
			list = Files.readAllLines(file.toPath(), Charset.defaultCharset());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		String[] resultLine = list.get(list.size() - 1).split("\t");
		//result.setOtsuThreshold(Long.parseLong(resultLine[16]));
		
		return result;
	}
	
	
	public void checkGeneralValues(SegmentationResult foundResult) {
		// No values to verify currently
	}
	
	
	public String getResultPath(File file) {
		return SegmentationTest.PATH_TO_OUTPUT +
		       SegmentationTestRunner.PATH_TO_SEGMENTATION +
		       file.getName() + File.separator +
		       PATH_TO_RESULT +
		       file.getName();
	}
	
	
	public SegmentationResult extractResult(SegmentationResult result, String path) {
		result.setImage(new ImagePlus(path));
		return result;
	}
	
	
	public void checkResult(SegmentationResult result) {
		ImagePlus imgDiff = new ImageCalculator().run("difference create stack"
		                                        ,target.getImage(),  result.getImage());
		StackStatistics statsTarget      = new StackStatistics(target.getImage());
		long[]          histogramTarget  = statsTarget.getHistogram();
		long            targetMaskPixels = histogramTarget[histogramTarget.length - 1];
		
		StackStatistics statsDiff     = new StackStatistics(imgDiff);
		long[]          histogramDiff = statsDiff.getHistogram();
		long            diffPixels    = histogramDiff[histogramDiff.length - 1];
		
		LOGGER.info("Mask: Target = {} ({}% = {})",
		            targetMaskPixels,
		            PERCENT_MASK_OVERLAPPED,
		            targetMaskPixels * PERCENT_MASK_OVERLAPPED / 100);
		LOGGER.info("Mask difference found = {}", diffPixels);
		assertTrue(targetMaskPixels * PERCENT_MASK_OVERLAPPED / 100 > diffPixels);
	}
	
	
	public void checkValues(File file) {
		SegmentationResult segmentationResult = extractGeneralInfo(new SegmentationResult(), getInfoFile(file));
		segmentationResult = extractResult(segmentationResult, getResultPath(file));
		
		checkGeneralValues(segmentationResult);
		checkResult(segmentationResult);
	}
	
}
