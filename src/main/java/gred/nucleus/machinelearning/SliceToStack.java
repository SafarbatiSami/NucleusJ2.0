package gred.nucleus.machinelearning;

import gred.nucleus.files.Directory;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.Concatenator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;


public class SliceToStack {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	String pathToSliceDir;
	String pathToOutputDir;
	
	
	/**
	 * Constructor
	 *
	 * @param pathToSliceDir  path to slice directory to merge to stack
	 * @param pathToOutputDir path to stack image output
	 */
	public SliceToStack(String pathToSliceDir, String pathToOutputDir) {
		this.pathToSliceDir = pathToSliceDir;
		this.pathToOutputDir = pathToOutputDir;
	}
	
	
	/**
	 * Save output file
	 *
	 * @param imagePlusInput image to save
	 * @param pathFile       path to save image
	 */
	public static void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiff(pathFile);
	}
	
	
	/**
	 * Merge slice to stack : - images shall have this file name format : CommonNameOfImageToMerge_NumberOfSlice
	 */
	public void run() {
		Map<String, Integer> test = new HashMap<>();
		
		Directory directoryOutput = new Directory(this.pathToOutputDir);
		Directory directoryInput  = new Directory(this.pathToSliceDir);
		directoryInput.listImageFiles(this.pathToSliceDir);
		// Iterate over images from directory
		for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
			String tm = directoryInput.getFile(i).getName();
			tm = tm.substring(0, tm.lastIndexOf("_"));
			tm = tm.substring(0, tm.lastIndexOf("_"));
			if (test.get(tm) != null) {
				test.put(tm, test.get(tm) + 1);
			} else {
				test.put(tm, 1);
			}
		}
		
		for (Map.Entry<String, Integer> entry : test.entrySet()) {
			int         size  = entry.getValue();
			ImagePlus[] image = new ImagePlus[size];
			LOGGER.info("image: {}", entry.getKey());
			for (short i = 0; i < image.length; ++i) {
				//image= BF.openImagePlus((directoryInput.dirPath
				image[i] = IJ.openImage((directoryInput.getDirPath() +
				                         File.separator +
				                         entry.getKey() +
				                         "_" +
				                         i +
				                         "_MLprediction.tif"));
				IJ.run(image[i], "8-bit", "");
				//
			}
			ImagePlus imp3 = new Concatenator().concatenate(image, false);
			saveFile(imp3, directoryOutput.getDirPath() + directoryOutput.getSeparator()
			               + entry.getKey() + ".tif");
		}
		
	}
	
}
