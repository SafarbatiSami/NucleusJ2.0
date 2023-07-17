package gred.nucleus.mains;

import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class TestSegmentationMethods {
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
 otsuModif.runSeveralImages2();
 }
 */
	
	
	/**
	 * @param input
	 * @param output
	 */
	public static void testStupidSeveralImages(String input, String output) throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(input, output);
		SegmentationCalling    otsuModified           = new SegmentationCalling(segmentationParameters);
		try {
			String log = otsuModified.runSeveralImages2();
			if (!(log.equals(""))) {
				LOGGER.error("Nuclei which didn't pass the segmentation\n{}", log);
			}
		} catch (IOException e) {
			LOGGER.error("An error occurred.", e);
		}
	}
	
	
	public static void testStupidSeveralImages(String input, String output, String config) throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(input, output, config);
		SegmentationCalling    otsuModified           = new SegmentationCalling(segmentationParameters);
		try {
			String log = otsuModified.runSeveralImages2();
			if (!(log.equals(""))) {
				LOGGER.error("Nuclei which didn't pass the segmentation\n{}", log);
			}
		} catch (IOException e) {
			LOGGER.error("An error occurred.", e);
		}
	}
	
	
	/**
	 * Main function of the package's tests.
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		///home/titus/Bureau/data/Test_Image_Reproducibility/IMAGE_TEST_NJ/AUTOCROP_RAW/RAW_BIOFORMATS
		String timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
		
		
		String expectedResult =
				"/home/tridubos/Bureau/TEST_SEG/Results_checked/Z_Col_cot21&17&22__w11 DAPI SIM_s3/";
		String expectedResultOutOTSU =
				"/home/tridubos/Bureau/TEST_SEG/Results_checked/Z_Col_cot21&17&22__w11 DAPI SIM_s3/OTSU/";
		
		String inputTristan =
				"/media/tridubos/DATA1/SPERMATO/Manip_1_57_images_input/Segmentation/Analyse_Segmentation/NucleusPB";
		String outputTristanGift =
				"/home/tridubos/Bureau/TEST_SEG/Test_analysis/Z_Col_cot21&17&22__w11 DAPI SIM_s3/GIFT/";
		String outputTristanOtsu =
				"/media/tridubos/DATA1/SPERMATO/Manipe_1_57_images_input/Segmentation/Analyse_Segmentation/Segmented";
        /*
        OutputFileVerification fw = new OutputFileVerification(expectedResult,inputTristan);
        fw.GetFileResultExpected(expectedResult);
        fw.GetFilesOutputFolder(inputTristan);
        */
		String pathToTest = "/home/tridubos/Bureau/IMAGES_TEST/AUTOCROP";
		long   maxMemory  = Runtime.getRuntime().maxMemory();
		LOGGER.debug("Maximum memory (bytes) /RAW_CZI: {} {}",
		             (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory * 1e-9),
		             Runtime.getRuntime().freeMemory() * 1e-9);
		/*
		 testStupidSeveralImages("/media/tridubos/DATA1/DATA_ANALYSE/MANIP_MANU_KAKU/ANALYSE_OCTOBRE_2019/images_PROBLEMS",
		 "/media/tridubos/DATA1/DATA_ANALYSE/MANIP_MANU_KAKU/ANALYSE_OCTOBRE_2019/SEG_IMAGE_PB");
		 
		 testStupidSeveralImages("/media/tridubos/DATA1/SPERMATO/Manipe_3_30_images/test_Noyaux_manquant",
		 "/media/tridubos/DATA1/SPERMATO/Manipe_3_30_images/SEG_NOYAUX_MANQUANT");
		 
		 testStupidSeveralImages("/media/tridubos/DATA1/DATA_ANALYSE/SPERMATO/Manipe_3_30_images/Autocrop",
		 "/media/tridubos/DATA1/DATA_ANALYSE/SPERMATO/Manipe_3_30_images/Segmentation");
		 
		 testStupidSeveralImages("/media/tridubos/DATA1/DATA_ANALYSE/MANIP_MANU_KAKU/ANALYSE_OCTOBRE_2019/New_autocrop",
		 "/media/tridubos/DATA1/DATA_ANALYSE/MANIP_MANU_KAKU/ANALYSE_OCTOBRE_2019/New_Segmentation");
		 
		 testStupidSeveralImages("/media/tridubos/DATA1/DATA_ANALYSE/SPERMATO/Manipe_3_30_images/IMAGE_PB/RAW_STRANGE_BAD_CROP",
		 "/media/tridubos/DATA1/DATA_ANALYSE/SPERMATO/Manipe_3_30_images/IMAGE_PB/Segmentation");
		 
		 testStupidSeveralImages("/media/tridubos/DATA1/DATA/Axel_Bioinformatics/RawDataNucleus",
		 "/media/tridubos/DATA1/DATA/Axel_Bioinformatics/New_Segmentation");
		 
		 testStupidSeveralImages("/media/tridubos/DATA1/DATA_ANALYSE/TEST_SPEED_RADIUS/raw",
		 "/media/tridubos/DATA1/DATA_ANALYSE/TEST_SPEED_RADIUS/resu_seg_rayon");
		 
		 testStupidSeveralImages("/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_BILLES_11-2019/test_calib_segmentation/Raw",
		 "/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_BILLES_11-2019/test_calib_segmentation/Segmented");
		 */
		testStupidSeveralImages(
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/TEST_NEW_PARAMETER/RAW",
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/TEST_NEW_PARAMETER/SEG");
		//testStupidSeveralImages("/home/titus/Bureau/TEST_NJ/AUTOCROP/DEBUG/RAWBUG",
		//"/home/titus/Bureau/TEST_NJ/AUTOCROP/DEBUG/SEGBUG");
		
		//media/tridubos/DATA1/DATA_ANALYSE/TEST_SPEED_RADIUS/raw
		// testStupidSeveralImages("/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_BILLES_11-2019/Output_Autocrop/RawOneChannel",
		//       "/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_BILLES_11-2019/Output_Autocrop/SegmentedOneChannel");
		// testStupidSeveralImages("/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_BILLES_11-2019/Output_Autocrop/RawTwoChannels",
		//        "/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_BILLES_11-2019/Output_Autocrop/SegmentedTwoChannels");
		
		
		// testStupidSeveralImages("/home/titus/Bureau/data/Noyaux/Raw", "/home/titus/Bureau/data/Noyaux/output");
		// testStupidSeveralImages(expectedResult, expectedResult, (short)6.0, 300000000,true);
        /*fw.GetFilesResultingOfAnalysis(inputTristan);
        fw.CompareAnalysisResult();
        OutputFileVerification fw = new OutputFileVerification();
        fw.GetFileResultExpected(expectedResult);
        fw.GetFilesOutputFolder(outputTristan);
        fw.GetFilesResultingOfAnalysis(outputTristan);
        fw.CompareAnalysisResult();
        */
		LOGGER.info("The program ended normally.");
		String timestampEnd = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
		
		LOGGER.info("Start: {}", timeStampStart);
		LOGGER.info("End: {}", timestampEnd);
		
	}
	
}
