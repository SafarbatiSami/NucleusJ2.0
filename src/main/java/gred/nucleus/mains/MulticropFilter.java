package gred.nucleus.mains;

import gred.nucleus.autocrop.AnnotateAutoCrop;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.files.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MulticropFilter {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	public static void main(String[] args) throws Exception {
		
		Directory directoryCoordinates =
				new Directory("/home/titus/Bureau/TEST_NJ/AUTOCROP/coordonneeProjection/coordonnee/");
		Directory directoryTIF = new Directory("/home/titus/Bureau/TEST_NJ/AUTOCROP/coordonneeProjection/raw/");
		directoryCoordinates.listAllFiles(directoryCoordinates.getDirPath());
		directoryTIF.listAllFiles(directoryTIF.getDirPath());
		for (short i = 0; i < directoryCoordinates.getNumberFiles(); ++i) {
			File coordinateFile = directoryCoordinates.getFile(i);
			
			// TODO FAIRE UNE FONCTION POUR CHOPER LE FICHIER IMAGE DANS LE DIR PEUT IMPORTE L EXTENSION !
			File tifFile = directoryTIF.searchFileNameWithoutExtension(coordinateFile.getName().split("\\.")[0]);
			if (tifFile != null) {
				LOGGER.debug("Inside");
				
				AutocropParameters autocropParameters =
						new AutocropParameters(tifFile.getParent(), tifFile.getParent());
				List<String> listOfBoxes = readCoordinatesTXT(coordinateFile);
				AnnotateAutoCrop annotateAutoCrop =
						new AnnotateAutoCrop(listOfBoxes, tifFile, tifFile.getAbsolutePath(), autocropParameters);
				
				annotateAutoCrop.run();
			}
		}
	}
	
	
	public static List<String> readCoordinatesTXT(File boxFile) {
		
		List<String> boxLists = new ArrayList<>();
		try (Scanner scanner = new Scanner(boxFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				if ((!(line.matches("^#.*")))
				    && (!(line.matches("^FileName.*")))) {
					String[] splitLine = line.split("\\t");
					int      xMax      = Integer.parseInt(splitLine[3]) + Integer.parseInt(splitLine[6]);
					int      yMax      = Integer.parseInt(splitLine[4]) + Integer.parseInt(splitLine[7]);
					int      zMax      = Integer.parseInt(splitLine[5]) + Integer.parseInt(splitLine[8]);
					boxLists.add(splitLine[0]
					             + "\t" + splitLine[3]
					             + "\t" + xMax
					             + "\t" + splitLine[4]
					             + "\t" + yMax
					             + "\t" + splitLine[5]
					             + "\t" + zMax);
				}
			}
		} catch (FileNotFoundException e) {
			LOGGER.error("An error occurred.", e);
		}
		return boxLists;
	}
	
}

