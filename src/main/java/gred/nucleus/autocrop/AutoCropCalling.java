package gred.nucleus.autocrop;

import fr.igred.omero.Client;
import fr.igred.omero.exception.AccessException;
import fr.igred.omero.exception.ServiceException;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ImageWrapper;
import fr.igred.omero.repository.ProjectWrapper;
import gred.nucleus.files.Directory;
import gred.nucleus.files.FilesNames;
import gred.nucleus.files.OutputTextFile;
import ij.IJ;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.*;


/**
 * Core method calling the autocrop method.
 * <p>This method can be run on only one file or on directory containing multiple tuple file.
 * <p>This class will call AutoCrop class to detect nuclei in the image.
 *
 * @author Tristan Dubos and Axel Poulet
 */
public class AutoCropCalling {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	/** Column names */
	private static final String HEADERS = "FileName\tNumberOfCrop\tOTSUThreshold\tDefaultOTSUThreshold\n";
	/** image prefix name */
	private String             prefix                = "";
	/** Get general information of cropping analyse */
	private String             outputCropGeneralInfo = "#HEADER\n";
	/** Parameters crop analyse */
	private AutocropParameters autocropParameters;

	/** Number of threads to used process images */
	private int executorThreads = 4;
	/** Number of threads used to download images */
	private final int DOWNLOADER_THREADS = 1;


	/** Constructor Create the output directory if it doesn't exist. */
	public AutoCropCalling() {
	}
	
	
	public AutoCropCalling(AutocropParameters autocropParameters) {
		this.autocropParameters = autocropParameters;
		this.outputCropGeneralInfo = autocropParameters.getAnalysisParameters() + HEADERS;
	}

	/**
	 * Setter for the number of threads used to process images
	 * @param threadNumber number of executors threads
	 */
	public void setExecutorThreads(int threadNumber) { this.executorThreads = threadNumber; }


	/**
	 * Run auto crop on image's folder: -If input is a file: open the image with bio-formats plugin to obtain the
	 * metadata then run the auto crop. -If input is directory, listed the file, foreach tif file loaded file with
	 * bio-formats, run the auto crop.
	 */
	public void runFolder() {
		ExecutorService processExecutor = Executors.newFixedThreadPool(executorThreads);
		final ConcurrentHashMap<String, String> outputCropGeneralLines = new ConcurrentHashMap<>();

		Directory directoryInput = new Directory(this.autocropParameters.getInputFolder());
		directoryInput.listImageFiles(this.autocropParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		directoryInput.checkAndActualiseNDFiles();

		List<File> files = directoryInput.listFiles();
		final CountDownLatch latch = new CountDownLatch(files.size());

		class ImageProcessor implements Runnable {

			private final File file;

			public ImageProcessor(File file){
				this.file = file;
			}

			@Override
			public void run() {
				LOGGER.info("Current file: {}", file.getAbsolutePath());
				String     fileImg          = file.toString();
				FilesNames outPutFilesNames = new FilesNames(fileImg);
				String prefix = outPutFilesNames.prefixNameFile();
				try {
					AutoCrop autoCrop = new AutoCrop(file, prefix, autocropParameters);
					autoCrop.thresholdKernels();
					autoCrop.computeConnectedComponent();
					autoCrop.componentBorderFilter();
					autoCrop.componentSizeFilter();
					autoCrop.computeBoxes2();
					autoCrop.addCROPParameter();
					autoCrop.boxIntersection();
					autoCrop.cropKernels2();
					autoCrop.writeAnalyseInfo();
					AnnotateAutoCrop annotate = new AnnotateAutoCrop(autoCrop.getFileCoordinates(),
							file,
							autocropParameters.getOutputFolder() + File.separator,
							prefix,
							autocropParameters);
					annotate.run();

					outputCropGeneralLines.put(file.getName(), autoCrop.getImageCropInfo());

					latch.countDown();
				} catch (Exception e) {
					LOGGER.error("Cannot run autocrop on: " + file.getName(), e);
					IJ.error("Cannot run autocrop on " + file.getName());			}
			}
		}

		for (File currentFile : files) {
			processExecutor.submit(new ImageProcessor(currentFile));
		}
		try { latch.await(); }
		catch (InterruptedException e) { e.printStackTrace(); }
		processExecutor.shutdownNow();

		StringBuilder generalInfoBuilder = new StringBuilder();
		for (File file: files) {
			generalInfoBuilder.append(outputCropGeneralLines.get(file.getName()));
		}
		outputCropGeneralInfo += generalInfoBuilder.toString();

		saveGeneralInfo();
	}


	/**
	 * Run auto crop on one image : -If input is a file: open the image with bio-formats plugin to obtain the metadata
	 * then run the auto crop. -If input is directory, listed the file, foreach tif file loaded file with bio-formats,
	 * run the auto crop.
	 *
	 * @param file
	 */
	public void runFile(String file) {
		File currentFile = new File(file);
		LOGGER.info("Current file: {}", currentFile.getAbsolutePath());
		String     fileImg          = currentFile.toString();
		FilesNames outPutFilesNames = new FilesNames(fileImg);
		this.prefix = outPutFilesNames.prefixNameFile();
		try {
			AutoCrop autoCrop = new AutoCrop(currentFile, this.prefix, this.autocropParameters);
			autoCrop.thresholdKernels();
			autoCrop.computeConnectedComponent();
			autoCrop.componentBorderFilter();
			autoCrop.componentSizeFilter();
			autoCrop.computeBoxes2();
			autoCrop.addCROPParameter();
			autoCrop.boxIntersection();
			autoCrop.cropKernels2();
			LOGGER.info("ENDED CROPPING");
			autoCrop.writeAnalyseInfo();
			AnnotateAutoCrop annotate = new AnnotateAutoCrop(autoCrop.getFileCoordinates(),
			                                                 currentFile,
			                                                 this.autocropParameters.getOutputFolder() + File.separator,
			                                                 this.prefix,
			                                                 this.autocropParameters);
			annotate.run();
			this.outputCropGeneralInfo += autoCrop.getImageCropInfo();
		} catch (Exception e) {
			LOGGER.error("Cannot run autocrop on: " + currentFile.getName(), e);
			IJ.error("Cannot run autocrop on " + currentFile.getName());
		}
	}


	public void saveGeneralInfo() {
		LOGGER.info("{}result_Autocrop_Analyse", this.autocropParameters.getInputFolder());
		OutputTextFile resultFileOutput =
				new OutputTextFile(this.autocropParameters.getOutputFolder() + "result_Autocrop_Analyse.csv");
		resultFileOutput.saveTextFile(this.outputCropGeneralInfo, true);
	}
	
	
	public void runImageOMERO(ImageWrapper image, Long[] outputsDatImages, Client client) throws Exception {
		String fileImg = image.getName();
		LOGGER.info("Current file: {}", fileImg);
		FilesNames outPutFilesNames = new FilesNames(fileImg);
		String prefix = outPutFilesNames.prefixNameFile();
		AutoCrop autoCrop = new AutoCrop(image, autocropParameters, client);
		autoCrop.thresholdKernels();
		autoCrop.computeConnectedComponent();
		autoCrop.componentBorderFilter();
		autoCrop.componentSizeFilter();
		autoCrop.computeBoxes2();
		autoCrop.addCROPParameter();
		autoCrop.boxIntersection();
		autoCrop.cropKernelsOMERO(image, outputsDatImages, client);
		autoCrop.writeAnalyseInfoOMERO(outputsDatImages[autocropParameters.getChannelToComputeThreshold()], client);
		AnnotateAutoCrop annotate = new AnnotateAutoCrop(autoCrop.getFileCoordinates(),
				autoCrop.getRawImage(),
				this.autocropParameters.getOutputFolder() + File.separator,
				prefix,
				this.autocropParameters);
		annotate.run();
		long outputProject = -1;
		// TODO Find a better way to get output project (maybe just pass it as a parameter)
		for (ProjectWrapper p: client.getProjects()) {
			for (DatasetWrapper d : p.getDatasets()) {
				if(d.getId()==outputsDatImages[0]){
					outputProject = p.getId();
					break;
				}
			}
		}
		annotate.saveProjectionOMERO(client, outputProject);
		this.outputCropGeneralInfo += autoCrop.getImageCropInfoOmero(image.getName());
	}
	
	
	public void runSeveralImageOMERO(final List<ImageWrapper> images, final Long[] outputsDatImages, final Client client)
	throws Exception {
		ExecutorService downloadExecutor = Executors.newFixedThreadPool(DOWNLOADER_THREADS);
		final ExecutorService processExecutor = Executors.newFixedThreadPool(executorThreads);
		final ConcurrentHashMap<String, String> outputCropGeneralLines = new ConcurrentHashMap<>();
		final CountDownLatch latch = new CountDownLatch(images.size());

		long outputFound = -1;
		// TODO Find a better way to get output project (maybe just pass it as a parameter)
		for (ProjectWrapper p: client.getProjects()) {
			for (DatasetWrapper d : p.getDatasets()) {
				if(d.getId()==outputsDatImages[0]){
					outputFound = p.getId();
					break;
				}
			}
		}
		final long outputProject = outputFound;

		class ImageProcessor implements Runnable{
			private final AutoCrop autoCrop;
			private final ImageWrapper image;

			ImageProcessor(AutoCrop autoCrop, ImageWrapper image) {
				this.autoCrop = autoCrop;
				this.image = image;
			}

			@Override
			public void run() {
				autoCrop.thresholdKernels();
				autoCrop.computeConnectedComponent();
				autoCrop.componentBorderFilter();
				autoCrop.componentSizeFilter();
				autoCrop.computeBoxes2();
				autoCrop.addCROPParameter();
				autoCrop.boxIntersection();
				try
				{
					autoCrop.cropKernelsOMERO(image, outputsDatImages, client);
					autoCrop.writeAnalyseInfoOMERO(outputsDatImages[autocropParameters.getChannelToComputeThreshold()], client);

					AnnotateAutoCrop annotate = new AnnotateAutoCrop(autoCrop.getFileCoordinates(),
						autoCrop.getRawImage(),
						autocropParameters.getOutputFolder() + File.separator,
						FilenameUtils.removeExtension(image.getName()),
						autocropParameters);
					annotate.run();
					annotate.saveProjectionOMERO(client, outputProject);
				} catch (Exception e) {
					e.printStackTrace();
				}

				outputCropGeneralLines.put(image.getName(), autoCrop.getImageCropInfo());

				latch.countDown();
			}
		}
		class ImageDownloader implements Runnable {

			private final ImageWrapper image;

			public ImageDownloader(ImageWrapper image){
				this.image = image;
			}

			@Override
			public void run() {
				String fileImg = image.getName();
				LOGGER.info("Current file: {}", fileImg);
				AutoCrop autoCrop = null;
				try { autoCrop = new AutoCrop(image, autocropParameters, client); }
				catch (ServiceException | AccessException | ExecutionException e) { e.printStackTrace(); }
				processExecutor.submit(new ImageProcessor(autoCrop, image));
			}
		}

		for (ImageWrapper image : images) {
			downloadExecutor.submit(new ImageDownloader(image));
		}
		latch.await();
		downloadExecutor.shutdownNow();
		processExecutor.shutdownNow();

		StringBuilder generalInfoBuilder = new StringBuilder();
		for (ImageWrapper image : images) {
			generalInfoBuilder.append(outputCropGeneralLines.get(image.getName()));
		}
		outputCropGeneralInfo += generalInfoBuilder.toString();
		
		saveGeneralInfoOmero(client, outputsDatImages);
	}
	
	
	public void saveGeneralInfoOmero(Client client, Long[] outputsDatImages) throws InterruptedException {
		
		String         resultPath       = this.autocropParameters.getOutputFolder() + "result_Autocrop_Analyse.csv";
		File           resultFile       = new File(resultPath);
		OutputTextFile resultFileOutput = new OutputTextFile(resultPath);
		resultFileOutput.saveTextFile(this.outputCropGeneralInfo, false);
		
		try {
			client.getDataset(outputsDatImages[autocropParameters.getChannelToComputeThreshold()])
			      .addFile(client, resultFile);
		} catch (ServiceException se) {
			LOGGER.error("Could not connect to OMERO.", se);
		} catch (AccessException ae) {
			LOGGER.error("Could not access data on OMERO.", ae);
		} catch (ExecutionException e) {
			LOGGER.error("Could not add file to dataset.", e);
		}
		try {
			Files.deleteIfExists(resultFile.toPath());
		} catch (IOException io) {
			LOGGER.error("Problem while deleting file: " + resultPath, io);
		}
	}

}
