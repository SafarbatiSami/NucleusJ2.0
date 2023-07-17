package gred.nucleus.autocrop;

import org.apache.commons.io.FilenameUtils;
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


public class AutocropTestChecker {
	public static final String PATH_TO_INFO   = "result_Autocrop_Analyse.csv";
	public static final String PATH_TO_TARGET = "target/";
	
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public final String PATH_TO_COORDINATES;
	
	public final int    VALID_CROP_NUMBER_RANGE = 10;
	public final double VALID_CROP_PERCENTAGE   = 60;
	
	
	private AutocropResult target = new AutocropResult();
	
	
	public AutocropTestChecker(String targetPath) {
		PATH_TO_COORDINATES = "coordinates/" + FilenameUtils.removeExtension(targetPath) + ".txt";
		
		File targetInfoFile = new File(AutoCropTest.PATH_TO_INPUT +
		                               AutocropTestRunner.PATH_TO_AUTOCROP +
		                               PATH_TO_TARGET +
		                               targetPath + File.separator +
		                               PATH_TO_INFO
		);
		
		File targetCoordinatesFile = new File(AutoCropTest.PATH_TO_INPUT +
		                                      AutocropTestRunner.PATH_TO_AUTOCROP +
		                                      PATH_TO_TARGET +
		                                      targetPath + File.separator +
		                                      PATH_TO_COORDINATES
		);
		
		target = extractGeneralInfo(target, targetInfoFile);
		target = extractCoordinates(target, targetCoordinatesFile);
	}
	
	
	public void checkValues(File file) {
		AutocropResult autocropResult = new AutocropResult();
		autocropResult = extractGeneralInfo(autocropResult, getInfoFile(file));
		autocropResult = extractCoordinates(autocropResult, getCoordinatesFile(file));
		
		checkGeneralValues(autocropResult);
		checkCoordinates(autocropResult);
	}
	
	
	private File getInfoFile(File file) {
		return new File(AutoCropTest.PATH_TO_OUTPUT +
		                AutocropTestRunner.PATH_TO_AUTOCROP +
		                file.getName() + File.separator +
		                PATH_TO_INFO);
	}
	
	
	private File getCoordinatesFile(File file) {
		return new File(AutoCropTest.PATH_TO_OUTPUT +
		                AutocropTestRunner.PATH_TO_AUTOCROP +
		                file.getName() + File.separator +
		                PATH_TO_COORDINATES);
	}
	
	
	public AutocropResult extractGeneralInfo(AutocropResult result, File file) {
		LOGGER.debug("Extracting info from file: {}", file);
		List<String> resultList = new ArrayList<>();
		try {
			resultList = Files.readAllLines(file.toPath(), Charset.defaultCharset());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		String[] resultLine = resultList.get(resultList.size() - 1).split("\t");
		result.setCropNb(Integer.parseInt(resultLine[1]));
		
		return result;
	}
	
	
	public AutocropResult extractCoordinates(AutocropResult result, File file) {
		List<String> fileList = new ArrayList<>();
		try {
			fileList = Files.readAllLines(file.toPath(), Charset.defaultCharset());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		List<String> coordinateList = fileList.subList(17, fileList.size());
		
		List<CropResult> coordinates = new ArrayList<>();
		for (String line : coordinateList) {
			String[] resultLine = line.split("\t");
			coordinates.add(new CropResult(Integer.parseInt(resultLine[2]),
			                               Integer.parseInt(resultLine[1]),
			                               Integer.parseInt(resultLine[3]),
			                               Integer.parseInt(resultLine[4]),
			                               Integer.parseInt(resultLine[5]),
			                               Integer.parseInt(resultLine[6]),
			                               Integer.parseInt(resultLine[7]),
			                               Integer.parseInt(resultLine[8])
			
			));
		}
		result.setCoordinates(coordinates);
		return result;
	}
	
	
	public void checkGeneralValues(AutocropResult foundResult) {
		LOGGER.info("Crop(s): (target) {} / {} (found)", target.getCropNb(), foundResult.getCropNb());
		assertTrue(target.getCropNb() + VALID_CROP_NUMBER_RANGE >= foundResult.getCropNb()
		           && target.getCropNb() - VALID_CROP_NUMBER_RANGE <= foundResult.getCropNb());
	}
	
	
	public void checkCoordinates(AutocropResult foundResult) {
		int overlappingCrops = getNbOfOverlappingCrops(foundResult);
		LOGGER.info("Crops found overlapping (at least 80% overlapped) with targeted ones (={}) = {}",
		             target.getCropNb(), overlappingCrops);
		/* To change: valid if 90% of the crops found */
		assertTrue(overlappingCrops >= target.getCropNb() * VALID_CROP_PERCENTAGE / 100);
	}
	
	
	/**
	 * Checks whether the crops are overlapping with the wanted crops from the target
	 *
	 * @param autocropResult the autocrop result
	 *
	 * @return counts the valid crops found (corresponding to the targeted ones)
	 */
	public int getNbOfOverlappingCrops(AutocropResult autocropResult) {
		int validCrops = 0, cropCounter;
		
		for (CropResult tCrop : target.getCoordinates()) {
			LOGGER.debug("> TARGET: {}", tCrop.getCropNumber());
			
			cropCounter = 0;
			for (CropResult rCrop : autocropResult.getCoordinates()) {
				double percent = boxesPercentOverlapping(tCrop.getBox(), rCrop.getBox());
				LOGGER.debug("\t> FOUND: {} / Overlapping: {}", rCrop.getCropNumber(), percent);
				if (percent >= 80) cropCounter++; // If more than one there's probably some bad crops
			}
			if (cropCounter == 1) validCrops++;
			
		}
		return validCrops;
	}
	
	
	/**
	 * Calculate the intersection area between two 3D boxes which are aligned/non-rotated and return the percentage of
	 * the area of the crop whose the most overlapped
	 *
	 * @param a 3D box coordinates
	 * @param b 3D box coordinates
	 *
	 * @return the percent of area overlapping according the sum of the volumes of each box
	 */
	private double boxesPercentOverlapping(Box a, Box b) {
		int aLeft  = a.getXMin(), aRight = a.getXMax();
		int aTop   = a.getYMin(), aBottom = a.getYMax();
		int aFront = a.getZMin(), aBack = a.getZMax();
		
		int bLeft  = b.getXMin(), bRight = b.getXMax();
		int bTop   = b.getYMin(), bBottom = b.getYMax();
		int bFront = b.getZMin(), bBack = b.getZMax();
		
		int    x_overlap   = Math.max(0, Math.min(aRight, bRight) - Math.max(aLeft, bLeft));
		int    y_overlap   = Math.max(0, Math.min(aBottom, bBottom) - Math.max(aTop, bTop));
		int    z_overlap   = Math.max(0, Math.min(aBack, bBack) - Math.max(aFront, bFront));
		double overlapArea = x_overlap * y_overlap * z_overlap;
		
		double aVol = (a.getXMax() - a.getXMin()) * (a.getYMax() - a.getYMin()) * (a.getZMax() - a.getZMin());
		double bVol = (b.getXMax() - b.getXMin()) * (b.getYMax() - b.getYMin()) * (b.getZMax() - b.getZMin());
		
		double aOverlappedPercent = 100 * overlapArea / aVol;
		double bOverlappedPercent = 100 * overlapArea / bVol;
		
		return Math.min(aOverlappedPercent, bOverlappedPercent);
	}
	
}