package gred.nucleus.files;

import org.apache.commons.io.FilenameUtils;

import java.io.File;


public class FilesNames {
	/** Path file input */
	String  pathFile     = "";
	/** File name */
	String  fileName     = "";
	/** Complete pathFile */
	String  fullPathFile = "";
	boolean fileExists   = true;
	
	
	public FilesNames() {
	}
	
	
	/** Constructor to create file object */
	public FilesNames(String filePath) {
		this.fullPathFile = filePath;
		File file = new File(filePath);
		this.pathFile = file.getParent() + File.separator;
		this.fileName = file.getName();
		checkFileExists();
	}
	
	
	public String prefixNameFile() {
		return FilenameUtils.removeExtension(this.fileName);
	}
	
	
	/** Method to check if file exists */
	public void checkFileExists() {
		File file = new File(this.fullPathFile);
		if (!file.exists()) this.fileExists = false;
	}
	
	
	/** @return boolean true for existing file */
	public boolean fileExists() {
		return fileExists;
	}
	
	
	/** @return path to file */
	public String getPathFile() {
		return this.pathFile;
	}
	
	
	public void setFullPathFile(String fileName) {
		this.fullPathFile = pathFile + fileName;
	}
	
}
