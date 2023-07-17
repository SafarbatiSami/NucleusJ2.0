package gred.nucleus.autocrop;

import fr.igred.omero.Client;
import fr.igred.omero.exception.AccessException;
import fr.igred.omero.exception.ServiceException;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ImageWrapper;
import fr.igred.omero.repository.ProjectWrapper;
import gred.nucleus.files.Directory;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageRoi;
import ij.io.FileSaver;
import ij.plugin.ContrastEnhancer;
import ij.plugin.Duplicator;
import ij.plugin.LutLoader;
import ij.plugin.filter.LutApplier;
import ij.process.ImageConverter;
import ij.process.LUT;
import omero.cmd.Duplicate;
import omero.gateway.model.ProjectData;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class GenerateOverlay {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final String pathToProjection;
	private final String pathToDic;
	private final String pathToOutput;

	private final boolean isOMEROEnable;

	public GenerateOverlay() {
		isOMEROEnable = true;
		pathToProjection = "." + File.separator +"tmp-zprojection-GenerateOverlay";
		pathToDic = "." + File.separator + "tmp-dic-GenerateOverlay";
		pathToOutput = pathToProjection + File.separator + "overlay";
	}

	public GenerateOverlay(String pathToProjection, String pathToDic){
		isOMEROEnable = false;
		this.pathToProjection = pathToProjection;
		this.pathToDic = pathToDic;
		this.pathToOutput = pathToProjection + File.separator + "overlay";
	}

	private Map<File, File> gatherFilePairs(){
		File projectionDir = new File(pathToProjection);
		File dicDir = new File(pathToDic);

		Map<File, File> allDicToProj = new HashMap<>();
		// Gather all pair of files
		for (File fp : projectionDir.listFiles()) {
			String projName = FilenameUtils.removeExtension(fp.getName());
			String[] pNameTab = projName.split("_");
			projName = projName.replace(pNameTab[pNameTab.length-1],"");

			for (File fd: dicDir.listFiles()) {
				String dicName = FilenameUtils.removeExtension(fd.getName());
				String[] dNameTab = dicName.split("_");
				dicName = dicName.replace(dNameTab[dNameTab.length-1],"");
				if(dicName.equals(projName)) allDicToProj.put(fd, fp);
			}
		}
		return allDicToProj;
	}

	private LUT getNucleiLUT() throws IOException {
		InputStream in = getClass().getResourceAsStream("/overlay/LUT.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		BufferedWriter bw = new BufferedWriter(new FileWriter("./tmp-LUT.txt"));
		String l;
		while((l=br.readLine())!=null) bw.write(l + "\n");
		bw.close();
		LUT fire = LutLoader.openLut("./tmp-LUT.txt");
		Files.delete(Paths.get("./tmp-LUT.txt"));
		return fire;
	}

	public void run() throws IOException {
		Directory output = new Directory(pathToOutput);
		output.checkAndCreateDir();

		Map<File, File> allDicToProj = this.gatherFilePairs();

		LUT fire = this.getNucleiLUT();

		// Process all pairs
		for (Map.Entry<File, File> e : allDicToProj.entrySet()) {
			String name = FilenameUtils.removeExtension(e.getKey().getName());
			String[] nameTab = name.split("_");
			name = name.replace(nameTab[nameTab.length-1],"");

			ImagePlus proj = new ImagePlus(e.getValue().getPath());
			ImagePlus overlay = new Duplicator().run(proj);
			ImagePlus dic = new ImagePlus(e.getKey().getPath());

			new ImageConverter(overlay).convertToGray16();

			for (int i=0;i<overlay.getNSlices();i++) {
				overlay.setSlice(i);
				overlay.getProcessor().invertLut();
				overlay.getProcessor().setLut(fire);
			}

			ImageRoi overlayRoi = new ImageRoi(0,0, dic.getProcessor());
			overlayRoi.setOpacity(0.5);
			overlay.setRoi(overlayRoi);

			if(isOMEROEnable) {
				saveFile(overlay.flatten(), pathToOutput + File.separator + name + "_Overlay.tif");
			}
			else {
				saveFile(overlay, pathToOutput + File.separator + name + "_Overlay.tif");
			}
		}
	}

	public void runFromOMERO(String projectionDatasetID, String dicDatasetID, String outputProjectID, Client client) throws Exception {
		DatasetWrapper projectionDataset = client.getDataset(Long.parseLong(projectionDatasetID));
		DatasetWrapper dicDataset = client.getDataset(Long.parseLong(dicDatasetID));
		ProjectWrapper outputProject = client.getProject(Long.parseLong(outputProjectID));

		Directory projectionDir = new Directory(pathToProjection);
		projectionDir.checkAndCreateDir();
		Directory dicDir = new Directory(pathToDic);
		dicDir.checkAndCreateDir();
		for (ImageWrapper projection : projectionDataset.getImages(client)) {
			saveFile(projection.toImagePlus(client), pathToProjection + File.separator + projection.getName());
		}
		for (ImageWrapper dic : dicDataset.getImages(client)) {
			saveFile(dic.toImagePlus(client), pathToDic + File.separator + dic.getName());
		}

		this.run();

		DatasetWrapper outputDataset;
		List<DatasetWrapper> datasets = outputProject.getDatasets("Overlay");
		if (datasets.isEmpty()) outputDataset = outputProject.addDataset(client, "Overlay", "");
		else outputDataset = datasets.get(0);

		File overlays = new File(pathToOutput);
		for (File overlayFile : overlays.listFiles()) {
			outputDataset.importImage(client, overlayFile.getPath());
		}
		FileUtils.deleteDirectory(new File(pathToProjection));
		FileUtils.deleteDirectory(new File(pathToDic));
		FileUtils.deleteDirectory(new File(pathToOutput));
	}


	/**
	 * Save output file in tiff format for OMERO
	 *
	 * @param imagePlusInput image to save
	 * @param pathFile       path to save image
	 */
	public static void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiff(pathFile);
	}

}
