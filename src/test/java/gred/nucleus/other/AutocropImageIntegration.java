package gred.nucleus.other;

import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.autocrop.AutocropParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;


public class AutocropImageIntegration {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	static ArrayList<String> test;
	
	
	public static void runAutoCrop(String imageSourceFile, String output, String pathToConfig) {
		AutocropParameters autocropParameters = new AutocropParameters(imageSourceFile, output, pathToConfig);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		autoCrop.runFolder();
	}
	
	
	public static void testStupid(String imageSourceFile, String output) {
		AutocropParameters autocropParameters = new AutocropParameters(imageSourceFile, output);
		AutoCropCalling    autoCrop           = new AutoCropCalling();
		autoCrop.runFolder();
	}
	
	
	public static void runAutoCrop(String imageSourceFile, String output) {
		//AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
		AutocropParameters autocropParameters =
				new AutocropParameters(imageSourceFile, output, 40, 40, 20,30, 0, 20, 0, 1, 1000000000);
		AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
		autoCrop.runFolder();
	}
	
	
	/**
	 * Main function of the package's tests.
	 *
	 * @param args arguments
	 */
	public static void main(String[] args) {
		String pathToTest   = "/home/tridubos/Bureau/IMAGES_TEST_CICD/AUTOCROP_IMAGES/AUTOCROP_RAW";
		String pathToOutput = "/home/tridubos/Bureau/IMAGES_TEST_CICD/AUTOCROP";
		
		
		runAutoCrop(pathToTest + "/RAW_BIOFORMATS",
		            pathToTest + "/AUTOCROP_RESULTS/RAW_BIOFORMATS",
		            pathToTest + "/RAW_BIOFORMATS/config_calibration.txt");
		
		
		runAutoCrop(pathToTest + "/RAW_CZI",
		            pathToTest + "/AUTOCROP_RESULTS/RAW_CZI");
		runAutoCrop(pathToTest + "/RAW_ND",
		            pathToTest + "/AUTOCROP_RESULTS/RAW_ND",
		            pathToTest + "/RAW_ND/config_calibration.txt");
		runAutoCrop(pathToTest + "/RAW_STK",
		            pathToTest + "/AUTOCROP_RESULTS/RAW_STK");
		runAutoCrop(pathToTest + "/RAW_TIF_2D",
		            pathToTest + "/AUTOCROP_RESULTS/RAW_TIF_2D");
		runAutoCrop(pathToTest + "/RAW_TIF_3D",
		            pathToTest + "/AUTOCROP_RESULTS/RAW_TIF_3D");
		
		
		//   String ExpectedResult = "/home/tridubos/Bureau/TEST_AUTOCROP/Results_checked";
		//   String inputOneImageTristan = "/home/tridubos/Bureau/TEST_AUTOCROP/Test_Version";
		
		//String outputTristan = "/home/tridubos/Bureau/TEST_AUTOCROP/out_test_Version";
		
		//OutputFileVerification fw = new OutputFileVerification();
		//fw.GetFileResultExpected(ExpectedResult);
		//fw.GetFilesOutputFolder(outputTristan);
		//testStupid(inputOneImageTristan, outputTristan);
		
		// runAutoCrop("/home/tridubos/Bureau/IMAGES_TEST/Nouveau dossier/Autocrop_name/Raw",
		//     "/home/tridubos/Bureau/IMAGES_TEST/Nouveau dossier/Autocrop_name/Crop");
		
		
		//runAutoCrop("/media/tridubos/DATA1/SPERMATO/Manipe_3_30_images/RawData",
		//       "/media/tridubos/DATA1/SPERMATO/Manipe_3_30_images/Autocrop",
		//     "/media/tridubos/DATA1/SPERMATO/Manipe_3_30_images/config_file_test");


//fw.GetFilesResultingOfAnalysis(outputTristan);
		//fw.CompareAnalysisResult();
		/*
		String inputOneImageTristan = "/home/tridubos/Bureau/TEST_READING_METADATA/";
		ImporterOptions options = new ImporterOptions();
		options.setId(inputOneImageTristan);
		options.setAutoscale(true);
		options.setCrop(true);
		options.setCropRegion(0, new Region(150, 150 ,50, 50));
		options.setColorMode(ImporterOptions.COLOR_MODE_COMPOSITE);
		ImagePlus[] imps = BF.openImagePlus(options);
		ImagePlus sort = new ImagePlus();
		sort = new Duplicator().run(imps[0],1,10);

		saveFile(sort, "/home/tridubos/Bureau/TEST_READING_METADATA/cetruc.tif");
		*/
		//testStupid(inputOneImageTristan, outputTristan);
		LOGGER.info("The program ended normally.");
		
		LOGGER.info("Total memory (bytes): {}",
		            Runtime.getRuntime().totalMemory() * 1e-9);
	}
	
}
