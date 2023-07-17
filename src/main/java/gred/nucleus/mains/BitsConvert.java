package gred.nucleus.mains;

import gred.nucleus.files.Directory;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;

import java.io.File;


public class BitsConvert {
	
	public static void main(String[] args) {
		Directory directoryInput = new Directory("/media/tridubos/DATA1/DATA/Axel_Bioinformatics/RawDataNucleus");
		directoryInput.listImageFiles("/media/tridubos/DATA1/DATA/Axel_Bioinformatics/RawDataNucleus");
		for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
			File currentFile = directoryInput.getFile(i);
			
			ImagePlus imp = new ImagePlus(currentFile.getAbsolutePath());
			IJ.run(imp, "16-bit", "");
			saveFile(imp, "/media/tridubos/DATA1/DATA/Axel_Bioinformatics/RawDataNucleus_16BITS/" + imp.getTitle());
		}
	}
	
	
	private static void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiffStack(pathFile);
	}
	
}
