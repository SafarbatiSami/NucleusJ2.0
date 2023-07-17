package gred.nucleus.autocrop;

import fr.igred.omero.Client;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ImageWrapper;
import fr.igred.omero.repository.ProjectWrapper;
import gred.nucleus.files.Directory;
import gred.nucleus.files.FilesNames;
import ij.ImagePlus;
import ij.io.FileSaver;
import loci.formats.FormatException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;


public class CropFromCoordinates {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private final String pathToCoordinates;
	private final String pathToRaw;
	private final String pathToOutput;
	private int channelToCrop = -1;

	/** Constructor for class used to crop nuclei from given coordinates
	 * @param pathToCoordinates path to directory containing coordinates for one file
	 * @param pathToRawAndChannel path to directory containing raw images
	 * @param pathToOutput path where cropped nuclei will be saved
	 */
	public CropFromCoordinates(String pathToCoordinates, String pathToRawAndChannel, String pathToOutput) {
		String[] splitRawInfo = pathToRawAndChannel.split(Pattern.quote(File.separator));
		String channelNumber = splitRawInfo[splitRawInfo.length-1];
		this.channelToCrop = Integer.parseInt(channelNumber);
		this.pathToRaw = pathToRawAndChannel.replace(File.separator + channelNumber, "");
		this.pathToCoordinates = pathToCoordinates;
		this.pathToOutput = pathToOutput;
	}

	public CropFromCoordinates(String pathToCoordinates){
		this.pathToCoordinates = pathToCoordinates;
		this.pathToRaw = "." + File.separator + "tmp-raw_cropFromCoordinate" + File.separator;
		this.pathToOutput = "." + File.separator + "tmp-cropped_nuclei_cropFromCoordinate" + File.separator;
	}

	private Map<File, File> gatherFilePairs() {
		File coordinateDir = new File(pathToCoordinates);
		File rawDir = new File(pathToRaw);

		Map<File, File> coordinateToRaw = new HashMap<>();
		// Gather all pair of files
		for (File fc : coordinateDir.listFiles()) {
			String coordName = FilenameUtils.removeExtension(fc.getName());
			for (File fr : rawDir.listFiles()) {
				String rawName = FilenameUtils.removeExtension(fr.getName());
				if(rawName.equals(coordName)) coordinateToRaw.put(fc, fr);
			}
		}
		return coordinateToRaw;
	}

	public void run() throws IOException, FormatException {
		Directory output = new Directory(pathToOutput);
		output.checkAndCreateDir();

		Map<File, File> allCoordinateToRaw = this.gatherFilePairs();

		for (Map.Entry<File, File> e : allCoordinateToRaw.entrySet()) {
			File coordinateFile = e.getKey();
			File rawImage       = e.getValue();

			AutocropParameters autocropParameters = new AutocropParameters(pathToRaw, pathToOutput);
			Map<Double, Box> boxes            = readCoordinatesTXT(coordinateFile);
			FilesNames       outPutFilesNames = new FilesNames(e.getValue().getName());
			String           prefix           = outPutFilesNames.prefixNameFile();
			AutoCrop         autoCrop         = new AutoCrop(rawImage, prefix, autocropParameters, boxes);
			autoCrop.cropKernels3(); // TODO PROBLEM : Currently crops only channel 0 for any given channel number === DONE
		}
	}

	public void runFromOMERO(String rawDatasetIDAndChannel, String outputProjectID, Client client) throws Exception {
		String[] splitRawInfo = rawDatasetIDAndChannel.split(File.separator);
		String channelNumber = splitRawInfo[splitRawInfo.length-1];
		this.channelToCrop = Integer.parseInt(channelNumber);
		String rawDatasetID = rawDatasetIDAndChannel.replace(File.separator + channelNumber, "");

		DatasetWrapper rawDataset = client.getDataset(Long.parseLong(rawDatasetID));
		ProjectWrapper outputProject = client.getProject(Long.parseLong(outputProjectID));

		Directory rawDirectory = new Directory(pathToRaw);
		rawDirectory.checkAndCreateDir();
		for (ImageWrapper raw : rawDataset.getImages(client)) {
			saveFile(raw.toImagePlus(client), pathToRaw + File.separator + raw.getName());
		}

		this.run();

		DatasetWrapper outputDataset;
		List<DatasetWrapper> datasets = outputProject.getDatasets("raw_" + "C" + channelToCrop + "_" + rawDataset.getName());
		if (datasets.isEmpty()) outputDataset = outputProject.addDataset(client, "raw_" + "C" + channelToCrop + "_" + rawDataset.getName(), "");
		else outputDataset = datasets.get(0);

		File crops = new File(pathToOutput);
		for (File crop : crops.listFiles()) {
			outputDataset.importImages(client, crop.getPath());
		}

		FileUtils.deleteDirectory(new File(pathToRaw));
		FileUtils.deleteDirectory(new File(pathToOutput));
	}

	public static void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiff(pathFile);
	}

	public Map<Double, Box> readCoordinatesTXT(File boxesFile) {
		
		Map<Double, Box> boxLists = new HashMap<>();
		double               count    = 0;
		try (Scanner scanner = new Scanner(boxesFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				if ((!(line.matches("^#.*")))
				    && (!(line.matches("^FileName.*")))) {
					String[] splitLine = line.split("\\t");
					short xMax = (short) (Integer.parseInt(splitLine[3]) + Integer.parseInt(splitLine[6]));
					short yMax = (short) (Integer.parseInt(splitLine[4]) + Integer.parseInt(splitLine[7]));
					short zMax = (short) (Integer.parseInt(splitLine[5]) + Integer.parseInt(splitLine[8]));
					
					Box box = new Box(Short.parseShort(splitLine[3]),
					                  xMax,
					                  Short.parseShort(splitLine[4]),
					                  yMax,
					                  Short.parseShort(splitLine[5]),
					                  zMax);
					
					boxLists.put(Double.valueOf(splitLine[2]), box);
				}
				count++;
			}
		} catch (FileNotFoundException e) {
			LOGGER.error("An error occurred.", e);
		}
		return boxLists;
	}
	
}
