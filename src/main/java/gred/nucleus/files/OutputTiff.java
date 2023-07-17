package gred.nucleus.files;

import ij.ImagePlus;
import ij.io.FileSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;


public class OutputTiff extends FilesNames {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	/** Constructor to create file to output */
	public OutputTiff(String filePath) {
		super(filePath);
	}
	
	
	/**
	 * Method to save file with verification if file already exists
	 * <p> TODO ADD ERROR IN LOG FILE
	 */
	public void saveImage(ImagePlus imageToSave) {
		LOGGER.debug("Saving image: {}", this.fullPathFile);
		try {
			if (!fileExists()) {
				if (imageToSave.getNSlices() > 1) {
					FileSaver fileSaver = new FileSaver(imageToSave);
					fileSaver.saveAsTiffStack(this.fullPathFile);
				} else {
					FileSaver fileSaver = new FileSaver(imageToSave);
					fileSaver.saveAsTiff(this.fullPathFile);
				}
			} else {
				File old = new File(this.fullPathFile);
				if (old.delete()) {
					LOGGER.debug("Deleted old {}", this.fullPathFile);
				}
				if (imageToSave.getNSlices() > 1) {
					FileSaver fileSaver = new FileSaver(imageToSave);
					fileSaver.saveAsTiffStack(this.fullPathFile);
				} else {
					FileSaver fileSaver = new FileSaver(imageToSave);
					fileSaver.saveAsTiff(this.fullPathFile);
				}
			}
		} catch (Exception e) {
			LOGGER.error("An error occurred.", e);
		}
	}
	
}



