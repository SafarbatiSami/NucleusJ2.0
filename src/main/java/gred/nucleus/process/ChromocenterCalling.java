package gred.nucleus.process;

import fr.igred.omero.Client;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ImageWrapper;
import fr.igred.omero.repository.ProjectWrapper;
import gred.nucleus.files.Directory;
import gred.nucleus.files.FilesNames;
import ij.IJ;
import ij.ImagePlus;
import loci.common.DebugTools;
import loci.plugins.BF;
import gred.nucleus.gui.Progress;
import gred.nucleus.plugins.ChromocenterParameters;
import gred.nucleus.utilsNODeJ.NucleusChromocentersAnalysis;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class ChromocenterCalling {
	ChromocenterParameters chromocenterParameters ;
	
	
	private String _prefix;
	private boolean _is2DImg =false;
	private boolean _isGui=false;
	private Progress _p;
	private File[] tab;
	private ProjectWrapper project;
	private DatasetWrapper outDataset;
	private String segImg;
	private String gradImg;
	
	/**
	 *
	 * @param chromocenterParameters
	 */
	public  ChromocenterCalling(ChromocenterParameters chromocenterParameters ){
		this.chromocenterParameters=chromocenterParameters;
		
	}
	
	public  ChromocenterCalling(ChromocenterParameters chromocenterParameters, boolean gui ){
		this.chromocenterParameters=chromocenterParameters;
		_isGui = gui;
	}
	
	/**
	 *
	 * @throws Exception
	 */
	public void  runSeveralImages2() throws  Exception{
		DebugTools.enableLogging("OFF");
		Directory directoryInput = new Directory(this.chromocenterParameters.getInputFolder());
		directoryInput.listImageFiles(this.chromocenterParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		String rhfChoice = "Volume";
		String diffDir = this.chromocenterParameters.outputFolder+"gradientImage";
		File file = new File (diffDir);
		if(!file.exists()) file.mkdir();
		
		String segCcDir = this.chromocenterParameters.outputFolder+"SegCC";
		file = new File (segCcDir);
		if(!file.exists()) file.mkdir();
		// TODO A REFAIRE C EST MOCHE !!!!
		System.out.println("size: "+ directoryInput.getNumberFiles());
		
		if (this._isGui){
			_p = new Progress("Images Analysis: ",directoryInput.getNumberFiles());
			_p._bar.setValue(0);
		}
		for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
			File currentFile = directoryInput.getFile(i);
			String fileImg = currentFile.toString();
			FilesNames outPutFilesNames = new FilesNames(fileImg);
			FilesNames SegCC = new FilesNames(this.chromocenterParameters._segInputFolder +
			                                  currentFile.separator+currentFile.getName());
			this._prefix = outPutFilesNames.prefixNameFile();
			ImagePlus [] _raw = BF.openImagePlus(currentFile.getAbsolutePath());
			//is2D => change
			imageType(_raw[0]);
			String outputFileName= segCcDir+currentFile.separator+currentFile.getName();
			String gradientFileName= diffDir+currentFile.separator+currentFile.getName();
			
			if(SegCC.fileExists()) {
				ImagePlus [] segNuc =BF.openImagePlus(this.chromocenterParameters._segInputFolder +
				                                      currentFile.separator+currentFile.getName());
				ChromencenterSegmentation chromencenterSegmentation = new ChromencenterSegmentation(
						_raw,
						segNuc,
						outputFileName,
						this.chromocenterParameters);
				chromencenterSegmentation.runCC3D(gradientFileName);
				NucleusChromocentersAnalysis nucleusChromocenterAnalysis = new NucleusChromocentersAnalysis();
				nucleusChromocenterAnalysis.compute3DParameters(
						rhfChoice,
						_raw[0],
						segNuc[0],
						IJ.openImage(outputFileName),
						this.chromocenterParameters);
			}
			else{
				IJ.log(SegCC.getPathFile()+" is missing");
			}
			if(this._isGui){_p._bar.setValue(1 + i);}
			
		}
		if (this._isGui){
			_p.dispose();
		}
	}
	
	
	public void SegmentationOMERO(String inputDirectoryRaw,String inputDirectorySeg,String outputDirectory,Client client)
	throws Exception {
		/** Get  image or Dataset ID */
		String[] param = inputDirectoryRaw.split("/");
		String[] param1 = inputDirectorySeg.split("/");
		
		Long imageID = Long.parseLong(param[1]);
		Long maskID = Long.parseLong(param1[1]);
		
		if (param.length >= 2 && param1.length >= 2) {
			
			if (param[0].equals("Image") && param1[0].equals("Image")){
				runOneImageOMERO(imageID,maskID,outputDirectory,client);
				
			} else if (param[0].equals("Dataset") && param1[0].equals("Dataset")) {
				List<ImageWrapper> images;
				List<ImageWrapper> masks;
				/** get raw images and masks datasets*/
				DatasetWrapper imageDataset = client.getDataset(imageID);
				DatasetWrapper maskDataset = client.getDataset(maskID);
				/** get images List */
				images = imageDataset.getImages(client);
				/** Create Dataset named NodeJOMERO */
				outDataset = new DatasetWrapper("NodeJOMERO", "");
				project  = client.getProject(Long.parseLong(outputDirectory));
				/** Add Dataset To the Project */
				Long datasetId = project.addDataset(client, outDataset).getId();
				outDataset = client.getDataset(datasetId);
				
				for (int i=0; i<images.size(); i++) {
					/** Get Image name */
					String imageName = images.get(i).getName();
					/** Get the mask with the same name */
					masks = maskDataset.getImages(client,imageName);
					/** Run Segmentation */
					runSeveralImagesOMERO(images.get(i), masks.get(0), client);
					/** Import Segmented cc to the Dataset*/
					outDataset.importImages(client, segImg);
					/** Delete the files locally*/
					
					File segImgDelete = new File(segImg);
					File gradImgDelete = new File(gradImg);
					Files.deleteIfExists(segImgDelete.toPath());
					Files.deleteIfExists(gradImgDelete.toPath());
				}
				/** import Result Tabs to the Dataset */
				outDataset.addFile(client, tab[0]);
				outDataset.addFile(client, tab[1]);
				/** Delete the tabs Locally*/
				try {
					Files.deleteIfExists(tab[0].toPath());
					Files.deleteIfExists(tab[1].toPath());
				} catch (IOException e) {
					//LOGGER.error("Could not delete file: {}", outputFileName);
				}
			}
		}
	}
	/** Function For OMERO  */
	public void  runOneImageOMERO(Long inputDirectoryRaw,Long inputDirectorySeg,String outputDirectory,Client client) throws  Exception{
		
		String rhfChoice = "Volume";
		Long imageID = inputDirectoryRaw;
		Long maskID = inputDirectorySeg;
		
		/** Getting the image and mask from omero */
		ImageWrapper image = client.getImage(imageID);
		ImageWrapper mask = client.getImage(maskID);
		
		String imageName = image.getName();
		
		/** image to imagePlus */
		ImagePlus[] RawImage = new ImagePlus[]{image.toImagePlus(client)};
		ImagePlus[] SegImage = new ImagePlus[]{mask.toImagePlus(client)};
		
		String diffDir = this.chromocenterParameters.outputFolder+"gradientImage";
		String segCcDir = this.chromocenterParameters.outputFolder+"SegCC";
		
		FilesNames outPutFilesNames = new FilesNames(imageName);
		this._prefix = outPutFilesNames.prefixNameFile();
		
		String outputFileName= segCcDir+imageName;
		String gradientFileName= diffDir+imageName;
		
		/** Test if Raw image is 2D*/
		//is2D => change
		ImagePlus imp = SegImage[0];
		imageType(imp);
		/** Processing */
		ChromencenterSegmentation chromencenterSegmentation = new ChromencenterSegmentation(
				RawImage,
				SegImage,
				outputFileName,
				this.chromocenterParameters);
		
		chromencenterSegmentation.runCC3D(gradientFileName);
		
		NucleusChromocentersAnalysis nucleusChromocenterAnalysis = new NucleusChromocentersAnalysis();
		File[] Parameters3DTab = nucleusChromocenterAnalysis.compute3DParameters(
				rhfChoice,
				RawImage[0],
				SegImage[0],
				IJ.openImage(outputFileName),
				this.chromocenterParameters);
		
		
		/** Import Segmented image to OMERO */
		project  = client.getProject(Long.parseLong(outputDirectory));
		
		/** Creating a Dataset in the Project */
		outDataset = new DatasetWrapper("NodeJOMERO", "");
		Long datasetId = project.addDataset(client, outDataset).getId();
		outDataset = client.getDataset(datasetId);
		/**Import images and tabs to OMERO */
		outDataset.importImages(client, outputFileName);
		outDataset.addFile(client, Parameters3DTab[0]);
		outDataset.addFile(client, Parameters3DTab[1]);
		File segImgDelete = new File(outputFileName);
		File gradImgDelete = new File(gradientFileName);
		try {
			Files.deleteIfExists(segImgDelete.toPath());
			Files.deleteIfExists(gradImgDelete.toPath());
			Files.deleteIfExists(Parameters3DTab[0].toPath());
			Files.deleteIfExists(Parameters3DTab[1].toPath());
		} catch (IOException e) {
			//LOGGER.error("Could not delete file: {}", outputFileName);
		}
	}
	
	public void runSeveralImagesOMERO(ImageWrapper image,ImageWrapper mask,Client client ) throws  Exception {
		
		String rhfChoice = "Volume";
		String imageName = image.getName();
		
		/** image to imagePlus */
		ImagePlus[] RawImage = new ImagePlus[]{image.toImagePlus(client)};
		ImagePlus[] SegImage = new ImagePlus[]{mask.toImagePlus(client)};
		
		String diffDir  = this.chromocenterParameters.outputFolder + "gradientImage";
		String segCcDir = this.chromocenterParameters.outputFolder + "SegCC";
		FilesNames outPutFilesNames = new FilesNames(imageName);
		
		this._prefix = outPutFilesNames.prefixNameFile();
		String outputFileName   = segCcDir + imageName;
		String gradientFileName = diffDir + imageName;
		
		/** Test if Raw image is 2D*/
		//is2D => change
		ImagePlus imp = RawImage[0];
		imageType(imp);
		/** Processing */
		ChromencenterSegmentation chromencenterSegmentation = new ChromencenterSegmentation(
				RawImage,
				SegImage,
				outputFileName,
				this.chromocenterParameters);
		
		chromencenterSegmentation.runCC3D(gradientFileName);
		
		NucleusChromocentersAnalysis nucleusChromocenterAnalysis = new NucleusChromocentersAnalysis();
		File[] Parameters3DTab = nucleusChromocenterAnalysis.compute3DParameters(
				rhfChoice,
				RawImage[0],
				SegImage[0],
				IJ.openImage(outputFileName),
				this.chromocenterParameters);
		
		tab = Parameters3DTab;
		segImg = outputFileName;
		gradImg = gradientFileName;
		
	}
	
	
	
	/**
	 *
	 * @throws Exception
	 */
	public void  just3D() throws  Exception{
		DebugTools.enableLogging("OFF");
		Directory directoryInput = new Directory(this.chromocenterParameters.getInputFolder());
		directoryInput.listImageFiles(this.chromocenterParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		String rhfChoice = "Volume";
		String nameFileChromocenter = this.chromocenterParameters.outputFolder+"CcParameters.tab";
		
		String segCcDir = this.chromocenterParameters.outputFolder;
		
		// TODO A REFAIRE C EST MOCHE !!!!
		System.out.println("size: "+ directoryInput.getNumberFiles());
		
		for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
			File currentFile = directoryInput.getFile(i);
			String fileImg = currentFile.toString();
			ImagePlus [] _raw = BF.openImagePlus(currentFile.getAbsolutePath());
			imageType(_raw[0]);
			String outputFileName= segCcDir+currentFile.separator+currentFile.getName();
			ImagePlus [] segNuc =BF.openImagePlus(this.chromocenterParameters._segInputFolder+currentFile.separator+currentFile.getName());
			// ChromocenterAnalysis chromocenterAnalysis = new ChromocenterAnalysis(_raw[0],segNuc[0], IJ.openImage(outputFileName));
			// chromocenterAnalysis.computeParametersChromocenter();
			
			NucleusChromocentersAnalysis nucleusChromocenterAnalysis = new NucleusChromocentersAnalysis();
			nucleusChromocenterAnalysis.compute3DParameters(
					rhfChoice,
					_raw[0],
					segNuc[0],
					IJ.openImage(outputFileName),
					this.chromocenterParameters);
		}
	}
	
	/**
	 *
	 * @param ramImage
	 */
	public void imageType(ImagePlus ramImage){
		ramImage.getDimensions();
		if(ramImage.getStackSize()==1){
			this._is2DImg =true;
		}
	}
	
}
