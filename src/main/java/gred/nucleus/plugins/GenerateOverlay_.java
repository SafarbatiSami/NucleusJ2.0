package gred.nucleus.plugins;

import fr.igred.omero.Client;
import fr.igred.omero.exception.AccessException;
import fr.igred.omero.exception.ServiceException;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ImageWrapper;
import gred.nucleus.dialogs.GenerateOverlayDialog;
import gred.nucleus.autocrop.GenerateOverlay;
import gred.nucleus.dialogs.IDialogListener;
import ij.IJ;
import ij.Prefs;
import ij.plugin.PlugIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class GenerateOverlay_ implements PlugIn, IDialogListener {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	GenerateOverlay generateOverlay = new GenerateOverlay();
	DatasetWrapper DICDataset;
	GenerateOverlayDialog GenerateOverlayDialog;
	
	
	@Override
	public void run(String arg) {
		if (IJ.versionLessThan("1.32c")) {
			return;
		}
		GenerateOverlayDialog = new GenerateOverlayDialog();
	}
	
	@Override
	public void OnStart() throws AccessException, ServiceException, ExecutionException {
		if (GenerateOverlayDialog.isOmeroEnabled()) {
			runOMERO();
		} else {
			runLocal();
		}
	}
	
	
	void runLocal(){
		String DICfile = GenerateOverlayDialog.getDICInput();
		String zProjectionFile = GenerateOverlayDialog.getZprojectionInput();
		if (DICfile == null || DICfile.equals("") || zProjectionFile == null || zProjectionFile.equals("")) {
			IJ.error("Input file or directory is missing");
		} else {
			try {
				LOGGER.info("Begin Overlay process ");
				GenerateOverlay generateOverlay1 = new GenerateOverlay(zProjectionFile,DICfile);
				generateOverlay1.run(); // Run Overlay process
				
				LOGGER.info("Overlay  process has ended successfully");
			} catch (Exception e) {
				LOGGER.info("Overlay process has failed");
				LOGGER.error("An error occurred.", e);
			}
		}
}
	
	public void runOMERO() {
		// Check connection
		String hostname = GenerateOverlayDialog.getHostname();
		String port     = GenerateOverlayDialog.getPort();
		String username = GenerateOverlayDialog.getUsername();
		String password = GenerateOverlayDialog.getPassword();
		String group    = GenerateOverlayDialog.getGroup();
		String output   = GenerateOverlayDialog.getOutputProject();
		// Set user prefs
		Prefs.set("omero.host", hostname);
		Prefs.set("omero.port", port);
		Prefs.set("omero.user", username);
		// Connect to OMERO
		Client client   = checkOMEROConnection(hostname, port, username, password.toCharArray(), group);
		// Handle the source according to the type given
		String ZprojectiondataType = GenerateOverlayDialog.getZprojectionDataType();
		String DICDataType = GenerateOverlayDialog.getDICDataType();
		//Get Datasets IDs
		String zProjectionID  = GenerateOverlayDialog.getSourceID();
		String DICID  = GenerateOverlayDialog.getzProjectionID();
		
		try {
			if (DICDataType.equals("Dataset") && ZprojectiondataType.equals("Dataset")) {
				try {
					LOGGER.info("Begin Overlay process ");
					DICDataset = client.getDataset(Long.parseLong(DICID));
					List<ImageWrapper> DICimages = DICDataset.getImages(client);
					if(!DICimages.isEmpty()){
						generateOverlay.runFromOMERO(zProjectionID,DICID,output,client); // Run Overlay process
					}
					LOGGER.info("Overlay process has ended successfully");
				} catch (Exception e) {
					LOGGER.info("Overlay process has failed");
					LOGGER.error("An error occurred.", e);
				}
			}
		} catch (Exception e) {
			LOGGER.error("An error occurred.", e);
		}
		client.disconnect();
	}
	
	public Client checkOMEROConnection(String hostname,
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
