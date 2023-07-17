package gred.nucleus.utils;


import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.ChannelSplitter;
import loci.common.DebugTools;
import loci.plugins.BF;

public class Rd2ToTif {
	
	
	private String _pathInput;
	private String _pathOutput;
	
	/**
	 *
	 * @param pathInput
	 * @param pathOutput
	 */
	public Rd2ToTif(String pathInput, String pathOutput){
		_pathInput = pathInput;
		_pathOutput = pathOutput;
	}
	
	public void run(int channel) throws Exception {
		ImagePlus img = this.getImageChannel(channel);
		saveFile(img,_pathOutput);
	}
	
	
	
	/**
	 * Method to get specific channel to compute OTSU threshold
	 *
	 * @param channelNumber : number of channel to compute OTSU for crop
	 * @return image of specific channel
	 */
	private ImagePlus getImageChannel(int channelNumber) throws Exception {
		DebugTools.enableLogging("OFF");    // DEBUG INFO BIOFORMAT OFF
		ImagePlus[] currentImage = BF.openImagePlus(_pathInput);
		ChannelSplitter splitter = new ChannelSplitter();
		currentImage = splitter.split(currentImage[0]);
		return currentImage[channelNumber];
	}
	
	
	/**
	 * Method to get specific channel to compute OTSU threshold
	 *
	 * @param channelNumber : number of channel to compute OTSU for crop
	 * @return image of specific channel
	 */
	public static ImagePlus getImageChannel(int channelNumber, String input) throws Exception {
		DebugTools.enableLogging("OFF");    // DEBUG INFO BIOFORMAT OFF
		ImagePlus[] currentImage = BF.openImagePlus(input);
		ChannelSplitter splitter = new ChannelSplitter();
		currentImage = splitter.split(currentImage[0]);
		return currentImage[channelNumber];
	}
	
	
	/**
	 * Save the image file
	 *
	 * @param imagePlusInput image to save
	 * @param pathFile path to save the image
	 */
	public static void saveFile(ImagePlus imagePlusInput, String pathFile){
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiff(pathFile);
	}
}
