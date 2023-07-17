package gred.nucleus.autocrop;

import fr.igred.omero.Client;
import gred.nucleus.files.Directory;
import gred.nucleus.files.FilesNames;
import loci.formats.FormatException;
import omero.client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.regex.Pattern;



public class GenerateProjectionFromCoordinates {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	String pathToConvexHullSeg;
	String pathToZProjection;
	String pathToCoordinates;
	String pathToRaw;
	
	
	/**
	 * Constructor
	 *
	 * @param pathToConvexHullSeg     path to segmented image's folder
	 * @param pathToZProjection path to Zprojection image's from autocrop
	 * @param pathToCoordinates path to coordinates files from autocrop
	 */
	public GenerateProjectionFromCoordinates(String pathToCoordinates, String pathToConvexHullSeg, String pathToZProjection) {
		this.pathToConvexHullSeg = pathToConvexHullSeg;
		this.pathToZProjection = pathToZProjection;
		this.pathToCoordinates = pathToCoordinates;
	}
	
	
	/**
	 * Constructor
	 *
	 * @param pathToCoordinates path to segmented image's folder
	 * @param pathToRaw         path to raw image
	 */
	public GenerateProjectionFromCoordinates(String pathToCoordinates, String pathToRaw) {
		this.pathToCoordinates = pathToCoordinates;
		this.pathToRaw = pathToRaw;
	}
	
	
	/**
	 * Compute list of boxes from coordinates file.
	 *
	 * @param boxFile coordinates file
	 *
	 * @return list of boxes file to draw in red
	 */
	public static Map<String, String> readCoordinatesTXT(File boxFile) {
		
		Map<String, String> boxLists = new HashMap<>();
		try (Scanner scanner = new Scanner(boxFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if(Pattern.matches("^((.*(\\\\|/))+)[^\\t]*(\\t\\d*)+\\d+$", line)){
					String[] splitLine = line.split("\\t");
					String[] fileName  = splitLine[0].split(Pattern.quote(File.separator));
					int      xMax      = Integer.parseInt(splitLine[3]) + Integer.parseInt(splitLine[6]);
					int      yMax      = Integer.parseInt(splitLine[4]) + Integer.parseInt(splitLine[7]);
					int      zMax      = Integer.parseInt(splitLine[5]) + Integer.parseInt(splitLine[8]);
					boxLists.put(fileName[fileName.length - 1], splitLine[0] + "\t"
					                                            + splitLine[3] + "\t"
					                                            + xMax + "\t"
					                                            + splitLine[4] + "\t"
					                                            + yMax + "\t"
					                                            + splitLine[5] + "\t"
					                                            + zMax);
					LOGGER.debug("Box {} value {}\t{}\t{}\t{}\t{}\t{}\t{}",
					             fileName[fileName.length - 1],
					             splitLine[0],
					             splitLine[3],
					             xMax,
					             splitLine[4],
					             yMax,
					             splitLine[5],
					             zMax);
				}
			}
		} catch (FileNotFoundException e) {
			LOGGER.error("File not found.", e);
		}
		return boxLists;
	}
	
	
	/**
	 * Run new annotation of Zprojection, color in red nuclei which were filtered (in case of convex hull algorithm color in red
	 * nuclei which doesn't pass the segmentation most of case Z truncated )
	 *
	 * @throws IOException
	 * @throws FormatException
	 */
	public void generateProjectionFiltered() throws IOException, FormatException {
		Directory convexHullSegImages = new Directory(this.pathToConvexHullSeg);
		convexHullSegImages.listImageFiles(this.pathToConvexHullSeg);
		convexHullSegImages.checkIfEmpty();
		Directory zProjection = new Directory(this.pathToZProjection);
		zProjection.listImageFiles(this.pathToZProjection);
		zProjection.checkIfEmpty();
		Directory coordinates = new Directory(this.pathToCoordinates);
		coordinates.listAllFiles(this.pathToCoordinates);
		coordinates.checkIfEmpty();
		for (short i = 0; i < coordinates.getNumberFiles(); ++i) {
			File                coordinateFile        = coordinates.getFile(i);
			Map<String, String> listOfBoxes           = readCoordinatesTXT(coordinateFile);
			List<String>        boxListsNucleiNotPass = new ArrayList<>();
			Map<String, String> sortedMap             = new TreeMap<>(listOfBoxes);
			for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
				if (!(convexHullSegImages.checkIfFileExists(entry.getKey()))) {
					boxListsNucleiNotPass.add(entry.getValue());
					LOGGER.info("add {}", entry.getValue());
				}
			}
			File currentZProjection = zProjection.searchFileNameWithoutExtension(coordinateFile.getName()
			                                                                                   .substring(0,
			                                                                                              coordinateFile
					                                                                                              .getName()
					                                                                                              .lastIndexOf(
							                                                                                              '.')) +
			                                                                     "_Zprojection");
			AutocropParameters autocropParameters = new AutocropParameters(currentZProjection.getParent(),
			                                                               currentZProjection.getParent() +
			                                                               zProjection.getSeparator());
			AnnotateAutoCrop annotateAutoCrop = new AnnotateAutoCrop(boxListsNucleiNotPass,
			                                                         currentZProjection,
			                                                         currentZProjection.getParent() +
			                                                         zProjection.getSeparator() +
			                                                         currentZProjection.getName()
			                                                                           .substring(0,
			                                                                                      currentZProjection.getName()
			                                                                                                        .lastIndexOf(
					                                                                                                        '.')),
			                                                         autocropParameters);
			annotateAutoCrop.runAddBadCrop();
		}
	}
	
	
	public void generateProjection() throws IOException, FormatException {
		Directory rawImage = new Directory(this.pathToRaw);
		rawImage.listImageFiles(this.pathToRaw);
		rawImage.checkIfEmpty();
		Directory coordinates = new Directory(this.pathToCoordinates);
		coordinates.listAllFiles(this.pathToCoordinates);
		coordinates.checkIfEmpty();
		
		for (short i = 0; i < coordinates.getNumberFiles(); ++i) {
			File                coordinateFile        = coordinates.getFile(i);
			Map<String, String> listOfBoxes           = readCoordinatesTXT(coordinateFile);
			List<String>        boxListsNucleiNotPass = new ArrayList<>();
			for (Map.Entry<String, String> entry : listOfBoxes.entrySet()) {
				boxListsNucleiNotPass.add(entry.getValue());
			}
			LOGGER.info(coordinateFile.getName());
			
			File currentRaw = rawImage.searchFileNameWithoutExtension(coordinateFile.getName()
			                                                                        .substring(0,
			                                                                                   coordinateFile.getName()
			                                                                                                 .lastIndexOf(
					                                                                                                 '.')));
			FilesNames outPutFilesNames = new FilesNames(currentRaw.toString());
			String     prefix           = outPutFilesNames.prefixNameFile();
			LOGGER.info("current raw: {}", currentRaw.getName());
			AutocropParameters autocropParameters = new AutocropParameters(currentRaw.getParent(),
			                                                               currentRaw.getParent() +
			                                                               rawImage.getSeparator());
			AnnotateAutoCrop annotateAutoCrop = new AnnotateAutoCrop(boxListsNucleiNotPass,
			                                                         currentRaw,
			                                                         currentRaw.getParent() + rawImage.getSeparator(),
			                                                         prefix,
			                                                         autocropParameters);
			annotateAutoCrop.run();
		}
	}

}
