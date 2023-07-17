package gred.nucleus.mains;

import gred.nucleus.core.NucleusSegmentation;
import gred.nucleus.files.Directory;
import gred.nucleus.files.FilesNames;
import gred.nucleus.segmentation.SegmentationParameters;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.ChannelSplitter;
import loci.plugins.BF;

import java.io.File;


public class ChromocenterTransformation {
	
	public static void main(String[] args) throws Exception {
		String input  = "/home/tridubos/Bureau/IMAGES_TEST_CICD/TEST_CC/SegmentedDataCc";
		String output = "/home/tridubos/Bureau/IMAGES_TEST_CICD/TEST_CC/out";
		
		
		SegmentationParameters segmentationParameters = new SegmentationParameters(input, output);
		
		
		Directory directoryInput = new Directory(input);
		directoryInput.listImageFiles(input);
		directoryInput.checkIfEmpty();
		for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
			File       currentFile      = directoryInput.getFile(i);
			String     fileImg          = currentFile.toString();
			FilesNames outPutFilesNames = new FilesNames(fileImg);
			String     prefix           = outPutFilesNames.prefixNameFile();
			NucleusSegmentation nucleusSegmentation =
					new NucleusSegmentation(currentFile, prefix, segmentationParameters);
			ImagePlus[] currentImage = BF.openImagePlus(currentFile.getAbsolutePath());
			
			currentImage = ChannelSplitter.split(currentImage[0]);
			ImagePlus toto = currentImage[0];
			ImagePlus out  = nucleusSegmentation.generateSegmentedImage(toto, 0);
			saveFile(out, output + File.separator + prefix);
			
			
		}
	}
	
	
	public static void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiffStack(pathFile);
	}
	
}
