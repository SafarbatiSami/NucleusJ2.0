package gred.nucleus.mains;

import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.autocrop.CropFromCoordinates;
import loci.formats.FormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;


/**
 * Class dedicated to examples and test of methods in the package.
 *
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */
public class TestAutoCrop {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	/**
	 * Test for labeling connected components of a binarized image. Only connected components with no voxel on the
	 * image's boundary are kept in the filtering process.
	 * <p>
	 * Connected components with a volume below some threshold are also removed.
	 * <p>
	 * a constant random gray level is set on each connected component.
	 *
	 * @param imageSourceFile the input image file on disk
	 */
	static ArrayList<String> test;
	
	
	public static void runAutoCropFolder(String imageSourceFile, String output, String pathToConfig) {
		AutocropParameters autocropParameters = new AutocropParameters(imageSourceFile, output, pathToConfig);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		autoCrop.runFolder();
	}
	
	
	public static void runAutoCropFolder(String imageSourceFile, String output) {
		//AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
		AutocropParameters autocropParameters = new AutocropParameters(imageSourceFile, output);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		autoCrop.runFolder();
	}
	
	
	public static void runAutoCropFile(String imageSourceFile, String output) {
		//AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
		AutocropParameters autocropParameters = new AutocropParameters(imageSourceFile, output);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		autoCrop.runFile(imageSourceFile);
	}
	
	
	public static void runCropFromCoordinates(String coordinateDir) throws IOException, FormatException {
		//CropFromCoordinates test = new CropFromCoordinates(coordinateDir);
		//test.runCropFromCoordinate();
	}
	
	
	/**
	 * Main function of the package's tests.
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		LOGGER.info("Start program");
		long maxMemory = Runtime.getRuntime().maxMemory();
		/* Maximum amount of memory the JVM will attempt to use */
		LOGGER.info("Maximum memory (bytes): {}",
		            (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory * 1e-9));

//		runAutoCropFolder("C:/Users/Martin/Documents/IMAGE_TEST_NJ/AUTOCROP/RAW_ND/","C:/Users/Martin/Documents/IMAGE_TEST_NJ/AUTOCROP/RESULTS/RAW_ND/");
		runCropFromCoordinates("C:/Users/Martin/Documents/IMAGE_TEST_NJ/AUTOCROP/RESULTS/TIF_3D/tab_file.txt");
		
		LOGGER.info("The program ended normally.");
		
		LOGGER.info("Total memory (bytes): {}",
		            Runtime.getRuntime().totalMemory() * 1e-9);
	}
	
}
