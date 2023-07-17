package gred.nucleus.other;

import gred.nucleus.core.ConvexHullSegmentation;
import gred.nucleus.core.Measure3D;
import gred.nucleus.segmentation.SegmentationParameters;
import ij.ImagePlus;
import ij.io.FileSaver;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;


/**
 * Class which calls the convex hull algorithm, perform the algorithm and compare results with others results
 */
public class ConvexHullDetectionTest {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	public static void main(String[] args) throws Exception {
		//run("/home/rongiera/Bureau/DATA_SCAN/DATA_TEST_SCAN/OTSU/","/home/rongiera/Bureau/SCANv2/");
		// MANUAL STEP BEFORE COMPARING : Compute parameters (using compiled jar/fiji) // TODO call compute parameters here
		// Uncomment to compare :
		//List<String> lines =  compareInfoFile(
		//		"/home/rongiera/Bureau/testoutput/OTSU/result_Segmentation_Analyse_OTSU.csv",
		//		"/home/rongiera/Bureau/testoutput/GRAHAM/result_Segmentation_Analyse_GRAHAM.csv"
		//                                     );
		//System.out.println("Extreme values ( < 0.90 OR > 1.10 )");
		//for (String s: lines) System.out.println(s);
	}
	
	
	/**
	 * Run convex hull algorithm (currently graham scan) on a folder
	 * @param inputDir input directory
	 * @param outputDir output directory
	 */
	public static void run(String inputDir, String outputDir) {
		File   file  = new File(inputDir);
		File[] files = file.listFiles();
		
		if (files != null) {
			for (File f : files) {
				String name      = f.getName();
				String extension = FilenameUtils.getExtension(name).toLowerCase(Locale.ROOT);
				if (!extension.equals("tif")) { // Only handle .TIF
					LOGGER.info("File of type {} skipped", extension);
				} else {
					LOGGER.info("Beginning process on: {} ({})", name, f.getAbsolutePath());
					ImagePlus segImp = runGrahamScan(f.getAbsolutePath());
					LOGGER.info("Finished process on: {}", name);
					LOGGER.info("Saving {} result", name);
					
					LOGGER.info("Checking results:");
					saveFile(segImp,outputDir + name);
				}
			}
		}
	}
	
	/**
	 * Run graham scan segmentation on an image
	 * @param pathToImg path to the image
	 */
	public static ImagePlus runGrahamScan(String pathToImg){
		ImagePlus sourceImg = new ImagePlus(pathToImg);
		sourceImg.setTitle("SOURCE");
		
		ConvexHullSegmentation convexHullSegmentation = new ConvexHullSegmentation();
		SegmentationParameters segmentationParameters = new SegmentationParameters(".", ".");
		
		return convexHullSegmentation.convexHullDetection(sourceImg, segmentationParameters);
	}
	
	
	/**
	 * Save ImagePLus as tiff
	 * @param imagePlusInput image plus to save
	 * @param pathFile path to save to
	 */
	static void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiffStack(pathFile);
	}
	
	
	/**
	 * Compare 2 .csv segmentation analysis file
	 * @param pathGift path to result file
	 * @param pathGraham path to result file
	 * @return List of string for the extreme values
	 */
	public static List<String> compareInfoFile(String pathGift, String pathGraham) {
		File resultGift = new File(pathGift);
		File resultGraham = new File(pathGraham);
		List<String> listGift = new ArrayList<>();
		List<String> listGraham = new ArrayList<>();
		try {
			listGift = Files.readAllLines(resultGift.toPath(), Charset.defaultCharset());
			listGraham = Files.readAllLines(resultGraham.toPath(), Charset.defaultCharset());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println(listGift.get(3));
		System.out.println(listGraham.get(3));
		List<String> grahamaIndex = new ArrayList<>();
		for (String s: listGraham) {
			grahamaIndex.add(s.split("\t")[0]);
		}
		List<String> result = new ArrayList<>();
		
		// To handle all result file type (from recompute parameters OR from result after segmentation)
		int firstIndex;
		if(listGift.get(6).split("\t").length==19) firstIndex = 6;
		else firstIndex = 9;
		
		// Extract volume from both files
		System.out.println("[Gift result file line] =\tgraham volume / gift volume" );
		for (String s : listGift.subList(firstIndex ,listGift.size())) { // Not necess
			String name  = s.split("\t")[0];
			double giftVolume = Double.parseDouble(s.split("\t")[1]);
			int index;
			double d = 1;
			String line = null;
			for (String n: grahamaIndex) {
				if(n.equals(name)) {
					index = grahamaIndex.indexOf(n); // Find index in graham list
					double grahamVolume = Double.parseDouble(listGraham.get(index).split("\t")[1]);
					d = grahamVolume / giftVolume;
					line = "["+ (listGift.indexOf(s)+1) +"] =\t" + d;
					System.out.println(line);
				}
			}
			
			// Check when big difference ?
			if(d < 0.90 || d > 1.10 ) {
				result.add(line);
			}
		}
		return result;
	}
}
