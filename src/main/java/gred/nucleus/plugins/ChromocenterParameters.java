package gred.nucleus.plugins;

import fr.igred.omero.Client;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ChromocenterParameters extends PluginParameters {
	/** Activation of gaussian filter */
	public boolean _gaussianOnRaw = false;
	/** Factor for gradient thresholding */
	public double _factor =1.5;
	/** Number of neighbour to explore */
	public int _neigh = 3;
	/** Folder containing segmented images */
	public String _segInputFolder;
	
	public Client _omeroClient = null;
	/** Gaussian parameters  */
	public double _gaussianBlurXsigma =1;
	public double _gaussianBlurYsigma =1;
	public double _gaussianBlurZsigma =2;
	/** Filter connected component */
	public boolean _sizeFilterConnectedComponent =false;
	/** Filter connected component */
	public boolean _noChange = false;
	/** Max volume connected component filter*/
	public double _maxSizeConnectedComponent =3;
	/** Min volume connected component filter*/
	public double _minSizeConnectedComponent =0.003;
	
	/** ChromocenterParameters Constructor For OMERO */
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               Client omeroClient){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._omeroClient = omeroClient;
	}
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               Client omeroClient,
	                               double gaussianBlurXsigma,
	                               double gaussianBlurYsigma,
	                               double gaussianBlurZsigma,
	                               double factor,
	                               int neigh,
	                               boolean gaussian,
	                               boolean sizeFilterConnectedComponent,
	                               double maxSizeConnectedComponent,
	                               double minSizeConnectedComponent){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._omeroClient = omeroClient;
		this._gaussianBlurXsigma =gaussianBlurXsigma;
		this._gaussianBlurYsigma =gaussianBlurYsigma;
		this._gaussianBlurZsigma =gaussianBlurZsigma;
		this._factor =factor;
		this._neigh =neigh;
		this._gaussianOnRaw =gaussian;
		this._sizeFilterConnectedComponent =sizeFilterConnectedComponent;
		this._maxSizeConnectedComponent =maxSizeConnectedComponent;
		this._minSizeConnectedComponent =minSizeConnectedComponent;
	}
	public ChromocenterParameters(String inputFolder,
	                              String SegInputFolder,
	                              String outputFolder){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		
	}
	
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               String pathToConfigFile){
		super(inputFolder, outputFolder,pathToConfigFile);
		addProperties(pathToConfigFile);
		this._segInputFolder =SegInputFolder;
	}
	
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               boolean gaussian,boolean sizeFilterConnectedComponent){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._gaussianOnRaw =gaussian;
		this._sizeFilterConnectedComponent =sizeFilterConnectedComponent;
	}
	
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               boolean gaussian ){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._gaussianOnRaw =gaussian;
	}
	
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               double gaussianBlurXsigma,
	                               double gaussianBlurYsigma,
	                               double gaussianBlurZsigma){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._gaussianBlurXsigma =gaussianBlurXsigma;
		this._gaussianBlurYsigma =gaussianBlurYsigma;
		this._gaussianBlurZsigma =gaussianBlurZsigma;
		
	}
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               double factor){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._factor =factor;
	}
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               int neigh){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._neigh =neigh;
	}
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               double factor,
	                               int neigh){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._factor =factor;
		this._neigh =neigh;
	}
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               double factor,int neigh,
	                               boolean gaussian){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._factor =factor;
		this._neigh =neigh;
		this._gaussianOnRaw =gaussian;
	}
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               double gaussianBlurXsigma,
	                               double gaussianBlurYsigma,
	                               double gaussianBlurZsigma,
	                               double factor,
	                               int neigh,
	                               boolean gaussian){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._gaussianBlurXsigma =gaussianBlurXsigma;
		this._gaussianBlurYsigma =gaussianBlurYsigma;
		this._gaussianBlurZsigma =gaussianBlurZsigma;
		this._gaussianOnRaw =true;
		this._factor =factor;
		this._neigh =neigh;
		this._gaussianOnRaw =gaussian;
	}
	
	
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               boolean sizeFilterConnectedComponent,
	                               double maxSizeConnectedComponent,
	                               double minSizeConnectedComponent){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._sizeFilterConnectedComponent =sizeFilterConnectedComponent;
		this._maxSizeConnectedComponent =maxSizeConnectedComponent;
		this._minSizeConnectedComponent =minSizeConnectedComponent;
	}
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               boolean sizeFilterConnectedComponent,
	                               double maxSizeConnectedComponent,
	                               double minSizeConnectedComponent,
	                               double factor,
	                               int neigh){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._sizeFilterConnectedComponent =sizeFilterConnectedComponent;
		this._maxSizeConnectedComponent =maxSizeConnectedComponent;
		this._minSizeConnectedComponent =minSizeConnectedComponent;
		this._factor =factor;
		this._neigh =neigh;
	}
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               double gaussianBlurXsigma,
	                               double gaussianBlurYsigma,
	                               double gaussianBlurZsigma,
	                               double factor,
	                               int neigh,
	                               boolean gaussian,
	                               boolean sizeFilterConnectedComponent,
	                               double maxSizeConnectedComponent,
	                               double minSizeConnectedComponent){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._gaussianBlurXsigma =gaussianBlurXsigma;
		this._gaussianBlurYsigma =gaussianBlurYsigma;
		this._gaussianBlurZsigma =gaussianBlurZsigma;
		this._factor =factor;
		this._neigh =neigh;
		this._gaussianOnRaw =gaussian;
		this._sizeFilterConnectedComponent =sizeFilterConnectedComponent;
		this._maxSizeConnectedComponent =maxSizeConnectedComponent;
		this._minSizeConnectedComponent =minSizeConnectedComponent;
	}
	
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               boolean noChange,
	                               double factor,
	                               int neigh){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._factor =factor;
		this._neigh =neigh;
		this._noChange = noChange;
		
	}
	
	
	public ChromocenterParameters (String inputFolder,
	                               String SegInputFolder,
	                               String outputFolder,
	                               double gaussianBlurXsigma,
	                               double gaussianBlurYsigma,
	                               double gaussianBlurZsigma,
	                               boolean gaussian,
	                               boolean sizeFilterConnectedComponent,
	                               double maxSizeConnectedComponent,
	                               double minSizeConnectedComponent){
		super(inputFolder, outputFolder);
		this._segInputFolder =SegInputFolder;
		this._gaussianBlurXsigma =gaussianBlurXsigma;
		this._gaussianBlurYsigma =gaussianBlurYsigma;
		this._gaussianBlurZsigma =gaussianBlurZsigma;
		this._gaussianOnRaw =gaussian;
		this._sizeFilterConnectedComponent =sizeFilterConnectedComponent;
		this._maxSizeConnectedComponent =maxSizeConnectedComponent;
		this._minSizeConnectedComponent =minSizeConnectedComponent;
		
	}
	
	public void addProperties (String pathToConfigFile) {
		Properties prop = new Properties();
		String fileName = pathToConfigFile;
		InputStream is = null;
		try {
			is = new FileInputStream(fileName);
		} catch (FileNotFoundException ex) {
			System.err.println(pathToConfigFile + " : can't find the config file !");
			System.exit(-1);
		}
		try {
			prop.load(is);
		} catch (IOException ex) {
			System.err.println(pathToConfigFile + " : can't load the config file !");
			System.exit(-1);
		}
		for (String idProp : prop.stringPropertyNames()) {
			if (idProp.equals("_neigh")) {
				this._neigh =
						Integer.valueOf(prop.getProperty("_neigh"));
			}
			if (idProp.equals("_factor")) {
				this._factor =
						Integer.valueOf(prop.getProperty("_factor"));
			}
			if (idProp.equals("_gaussianOnRaw")) {
				this._gaussianOnRaw =
						Boolean.valueOf(prop.getProperty("_gaussianOnRaw"));
			}
			if (idProp.equals("_gaussianBlurXsigma;")) {
				this._gaussianBlurXsigma =
						Double.valueOf(prop.getProperty("_gaussianBlurXsigma"));
			}
			if (idProp.equals("_gaussianBlurYsigma;")) {
				this._gaussianBlurXsigma =
						Double.valueOf(prop.getProperty("_gaussianBlurYsigma"));
			}
			if (idProp.equals("_gaussianBlurXsigma;")) {
				this._gaussianBlurXsigma =
						Double.valueOf(prop.getProperty("_gaussianBlurYsigma"));
			}
			if (idProp.equals("_sizeFilterConnectedComponent")) {
				this._sizeFilterConnectedComponent =
						Boolean.valueOf(prop.getProperty("_sizeFilterConnectedComponent"));
			}
			if (idProp.equals("_maxSizeConnectedComponent;")) {
				this._maxSizeConnectedComponent =
						Double.valueOf(prop.getProperty("_maxSizeConnectedComponent"));
			}
			if (idProp.equals("_minSizeConnectedComponent;")) {
				this._minSizeConnectedComponent =
						Double.valueOf(prop.getProperty("_minSizeConnectedComponent"));
			}
			
		}
	}
}