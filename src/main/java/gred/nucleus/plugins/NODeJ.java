package gred.nucleus.plugins;



import fr.igred.omero.Client;
import gred.nucleus.dialogs.IDialogListener;
import gred.nucleus.gui.GuiAnalysis;
import ij.plugin.PlugIn;
import ij.IJ;

import gred.nucleus.process.ChromocenterCalling;

/**
 * Method to detect the chromocenters on bam
 *
 * @author Tristan Dubos and Axel Poulet
 *
 */
public class NODeJ implements PlugIn, IDialogListener {
	GuiAnalysis gui;
	/**
	 *
	 * @param arg
	 */
	
	
	
	
	@Override
	public void run(String arg) {
		if (IJ.versionLessThan("1.32c")) {
			return;
		}
		 gui = new GuiAnalysis(this);
	}
	
	
	@Override
	public void OnStart() {
		if (gui.isOmeroEnabled()) {
			runOmero();
			
		} else {
			runLocal();
		}
	}
	
	void runOmero(){
		// Check connection
		String hostname = gui.getHostname();
		String port     = gui.getPort();
		String username = gui.getUsername();
		char[] password = gui.getPassword();
		String group  = gui.getGroup();
		Client client = checkOMEROConnection(hostname, port, username, password, group);
		// get IDs
		String sourceID;
		String segmentedID;
		String outputID = gui.getOutputProject();
		// check datatype
		String dataType = gui.getDataType();
		String dataTypeSegmented = gui.getDataTypeSegmented();
		
		ChromocenterParameters CCAnalyseParameters = new ChromocenterParameters(".",".",".",client,gui.getGaussianX(),
		                                                                        gui.getGaussianY(), gui.getGaussianZ(),
		                                                                        gui.getFactor(),gui.getNeigh(),
		                                                                        gui.isGaussian(), gui.isFilter(),
		                                                                        gui.getMax(), gui.getMin());
		
		ChromocenterCalling CCAnalyse = new ChromocenterCalling(CCAnalyseParameters,true);
		
		try {
			if (dataType.equals("Image") & dataTypeSegmented.equals("Image")){
				sourceID ="Image/"+gui.getSourceID();
				segmentedID = "Image/" + gui.getSegmentedNucleiID();
				CCAnalyse.SegmentationOMERO(sourceID,segmentedID,outputID,client);
			} else if (dataType.equals("Dataset") & dataTypeSegmented.equals("Dataset")) {
				sourceID ="Dataset/"+gui.getSourceID();
				segmentedID = "Dataset/" + gui.getSegmentedNucleiID();
				CCAnalyse.SegmentationOMERO(sourceID,segmentedID,outputID,client);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		client.disconnect();
	}
	
	void runLocal(){
		ChromocenterParameters CCAnalyseParameters = new  ChromocenterParameters(
				gui.getInputRaw(),
				gui.getInputSeg(),
				gui.getOutputDir(),
				gui.getGaussianX(),
				gui.getGaussianY(),
				gui.getGaussianZ(),
				gui.isGaussian(),
				gui.isFilter(),
				gui.getMax(),
				gui.getMin());
		
		ChromocenterCalling CCAnalyse= new ChromocenterCalling(CCAnalyseParameters);
		try {
			CCAnalyse.runSeveralImages2();
		} catch (Exception e) { e.printStackTrace(); }
		IJ.log("End of the chromocenter segmentation , the results are in ");
	}
	
	
	public static Client checkOMEROConnection(String hostname,
	                                          String port,
	                                          String username,
	                                          char[] password,
	                                          String group) {
		Client client = new Client();
		try {
			client.connect(hostname,
			               Integer.parseInt(port),
			               username,
			               password,
			               Long.valueOf(group));
		} catch (Exception exp) {
			IJ.error("Invalid connection values");
			return null;
		}
		return client;
	}
}
