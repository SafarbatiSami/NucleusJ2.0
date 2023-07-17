package gred.nucleus.dialogs;

import ij.Prefs;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;


public class CropFromCoodinateDialog extends JFrame implements ActionListener, ItemListener {
	private static final long              serialVersionUID        = 1L;
	private final        JTextField        jImageChooser           = new JTextField();
	private final        JTextField        jCoordFileChooser       = new JTextField();
	private final        JFileChooser      fc                      = new JFileChooser();
	private              boolean           start                   = false;
	private final        JRadioButton      omeroYesButton          = new JRadioButton("Yes");
	private final        JRadioButton      omeroNoButton           = new JRadioButton("No");
	private final        JPanel            omeroModeLayout         = new JPanel();
	private final        JPanel            localModeLayout         = new JPanel();
	private final        JTextField        jTextFieldHostname      = new JTextField();
	private final        JTextField        jTextFieldPort          = new JTextField();
	private final        JTextField        jTextFieldUsername      = new JTextField();
	private final        JPasswordField    jPasswordField          = new JPasswordField();
	private final        JTextField        jTextFieldGroup         = new JTextField();
	private final        String[]          dataTypes               = {"Project", "Dataset", "Tag", "Image"};
	private final        JComboBox<String> jComboBoxDataType       = new JComboBox<>(dataTypes);
	private final        JComboBox<String> jComboBoxDataTypeToCrop       = new JComboBox<>(dataTypes);
	private final        JTextField        jTextFieldSourceID      = new JTextField();
	private final        JTextField        jTextFieldToCropID      = new JTextField();
	private final        JTextField        jTextFieldOutputProject = new JTextField();
	private final        JTextField        jTextFieldChannelToCrop = new JTextField();
	private              Container         container;
	private              boolean           useOMERO                = false;
	private static final String            INPUT_CHOOSER           = "inputChooser";
	private static final String  OUTPUT_CHOOSER = "outputChooser";
	private final        JTextField        jInputFileChooser       = new JTextField();
	
	
	public CropFromCoodinateDialog() {
		
		String host = Prefs.get("omero.host", "omero.gred-clermont.fr");
		long port = Prefs.getInt("omero.port", 4);
		String username = Prefs.get("omero.user", "");
		
		
		JButton jButtonStart = new JButton("Start");
		jButtonStart.setBackground(new Color(0x2dce98));
		jButtonStart.setForeground(Color.white);
		JButton jButtonQuit = new JButton("Quit");
		jButtonQuit.setBackground(Color.red);
		jButtonQuit.setForeground(Color.white);
		this.setTitle("Crop From Coordinate - NucleusJ2");
		this.setMinimumSize(new Dimension(500, 410));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		container = getContentPane();
		BoxLayout mainBoxLayout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
		container.setLayout(mainBoxLayout);
		
		Border padding = BorderFactory.createEmptyBorder(10, 10, 10, 10);
		
		
		// Use Omero ?
		ButtonGroup bGroupOmeroMode = new ButtonGroup();
		bGroupOmeroMode.add(omeroYesButton);
		omeroYesButton.addItemListener(this);
		bGroupOmeroMode.add(omeroNoButton);
		omeroNoButton.setSelected(true);
		omeroNoButton.addItemListener(this);
		
		JPanel radioOmeroPanel = new JPanel();
		radioOmeroPanel.setLayout(new BoxLayout(radioOmeroPanel, BoxLayout.X_AXIS));
		
		
		JLabel jLabelOmero = new JLabel("Select from omero :");
		radioOmeroPanel.add(jLabelOmero);
		radioOmeroPanel.add(omeroYesButton);
		radioOmeroPanel.add(omeroNoButton);
		//radioOmeroPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		container.add(radioOmeroPanel, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                      GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                      new Insets(60, 30, 0, 0), 0, 0));
		
		
		// Local mode layout
		localModeLayout.setLayout(new BoxLayout(localModeLayout, BoxLayout.Y_AXIS));
		
		JPanel        localPanel  = new JPanel();
		GridBagLayout localLayout = new GridBagLayout();
		localLayout.columnWeights = new double[]{1, 5, 0.5};
		localPanel.setLayout(localLayout);
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		
		JLabel jLabelInput = new JLabel("Path to coordinate file:");
		localPanel.add(jLabelInput, c);
		c.gridx = 1;
		c.insets = new Insets(0, 0, 0, 20);
		
		localPanel.add(jInputFileChooser, c);
		jInputFileChooser.setMaximumSize(new Dimension(10000, 20));
		jInputFileChooser.setSize(new Dimension(180, 20));
		jInputFileChooser.setText("path\\coordinate file_tab_path\\image");
		
		JButton sourceButton = new JButton("...");
		sourceButton.setSize(new Dimension(20, 20));
		sourceButton.addActionListener(this);
		sourceButton.setName(INPUT_CHOOSER);
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 2;
		
		localPanel.add(sourceButton, c);
		
		localPanel.setBorder(padding);
		localModeLayout.add(localPanel);
		container.add(localModeLayout, 1);
		
		
		// Omero mode layout
		omeroModeLayout.setLayout(new BoxLayout(omeroModeLayout, BoxLayout.Y_AXIS));
		
		JPanel        omeroPanel  = new JPanel();
		GridBagLayout omeroLayout = new GridBagLayout();
		omeroLayout.columnWeights = new double[]{0.1, 0.1, 2};
		omeroPanel.setLayout(omeroLayout);
		c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 0, 5, 20);
		
		c.gridy = 0;
		JLabel jLabelHostname = new JLabel("Hostname :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelHostname, c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jTextFieldHostname, c);
		jTextFieldHostname.setMaximumSize(new Dimension(10000, 20));
		
		c.gridy = 1;
		JLabel jLabelPort = new JLabel("Port :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelPort, c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jTextFieldPort, c);
		jTextFieldPort.setMaximumSize(new Dimension(10000, 20));
		
		c.gridy = 2;
		JLabel jLabelUsername = new JLabel("Username :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelUsername, c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jTextFieldUsername, c);
		jTextFieldUsername.setMaximumSize(new Dimension(10000, 20));
		
		c.gridy = 3;
		JLabel jLabelPassword = new JLabel("Password :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelPassword, c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jPasswordField, c);
		jPasswordField.setMaximumSize(new Dimension(10000, 20));
		
		c.gridy = 4;
		JLabel jLabelGroup = new JLabel("Group ID :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelGroup, c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jTextFieldGroup, c);
		jTextFieldGroup.setMaximumSize(new Dimension(10000, 20));
		
		c.gridy = 5;
		JLabel jLabelSource = new JLabel("Image Source :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelSource, c);
		c.gridx = 1;
		omeroPanel.add(jComboBoxDataType, c);
		c.gridx = 2;
		omeroPanel.add(jTextFieldSourceID, c);
		jTextFieldSourceID.setMaximumSize(new Dimension(10000, 20));
		
		c.gridy = 6;
		JLabel jLabelToCrop = new JLabel("Image To Crop :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelToCrop, c);
		c.gridx = 1;
		omeroPanel.add(jComboBoxDataTypeToCrop, c);
		c.gridx = 2;
		omeroPanel.add(jTextFieldToCropID, c);
		jTextFieldToCropID.setMaximumSize(new Dimension(20000, 20));
		
		c.gridy=7;
		JLabel JchannelToCrop = new JLabel("Channel To Crop :");
		c.gridx=0;
		omeroPanel.add(JchannelToCrop,c);
		c.gridx=1;
		jTextFieldToCropID.setMaximumSize(new Dimension(20, 20));
		omeroPanel.add(jTextFieldChannelToCrop,c);
		
		c.gridy = 8;
		JLabel jLabelOutputProject = new JLabel("Output Dataset :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelOutputProject, c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jTextFieldOutputProject, c);
		jTextFieldOutputProject.setMaximumSize(new Dimension(10000, 20));
		
		omeroPanel.setBorder(padding);
		omeroModeLayout.add(omeroPanel);


        /*/\*\
        ------------------------------ Coordinate file -----------------------------------------
        \*\/*/

        /*/\*\
        ------------------------------ Image + coordinates -----------------------------------------
        \*\/*/


        /*
        JLabel imageFileLabel = new JLabel();
        container.add(imageFileLabel, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(10, 10, 0, 0), 0, 0));
        imageFileLabel.setText("Path to image:");

        container.add(jImageChooser, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 10, 0, 0), 0, 0));
        jImageChooser.setPreferredSize(new java.awt.Dimension(300, 20));
        jImageChooser.setMinimumSize(new java.awt.Dimension(300, 20));

        imageButton = new JButton("...");
        container.add(linkFileButton, new GridBagConstraints(0, 1, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 330, 0, 0), 0, 0));
        imageButton.addActionListener(this);
        imageButton.setName(imageChooserName);

        jLabelCoord = new JLabel();
        container.add(jLabelCoord, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(10, 10, 0, 0), 0, 0));
        jLabelCoord.setText("Path to coordinates:");

        container.add(jCoordFileChooser, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 10, 0, 0), 0, 0));
        jCoordFileChooser.setPreferredSize(new java.awt.Dimension(300, 20));
        jCoordFileChooser.setMinimumSize(new java.awt.Dimension(300, 20));

        coordButton = new JButton("...");
        container.add(coordButton, new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                new Insets(30, 330, 0, 0), 0, 0));
        coordButton.addActionListener(this);
        coordButton.setName(coordChooserName);
        */

        /*/\*\
        ------------------------------ Buttons -----------------------------------------
        \*\/*/
		
		// Start/Quit buttons
		
		Border padding2 = BorderFactory.createEmptyBorder(10, 120, 10, 120);
		
		JPanel startQuitPanel = new JPanel();
		startQuitPanel.setLayout(new GridLayout(1, 2, 30, 10));
		startQuitPanel.add(jButtonStart);
		startQuitPanel.add(jButtonQuit);
		startQuitPanel.setBorder(padding2);
		container.add(startQuitPanel, 2);
		
		
		CropFromCoodinateDialog.QuitListener quitListener = new QuitListener(this);
		jButtonQuit.addActionListener(quitListener);
		CropFromCoodinateDialog.StartListener startListener = new CropFromCoodinateDialog.StartListener(this);
		jButtonStart.addActionListener(startListener);
		this.setVisible(true);
		
		
		// DEFAULT VALUES FOR TESTING :
		jTextFieldHostname.setText(host);
		jTextFieldPort.setText(String.valueOf(port));
		
		jTextFieldUsername.setText(username);
		jTextFieldGroup.setText("553");
		jPasswordField.setText("");
		jComboBoxDataType.setSelectedIndex(3);
		jComboBoxDataTypeToCrop.setSelectedIndex(3);
		jTextFieldSourceID.setText("1012649");
		jTextFieldToCropID.setText("1012649");
		jTextFieldOutputProject.setText("27229");
	}
	
	
	public boolean isStart() {
		return start;
	}
	
	
	public String getLink() {
		return jInputFileChooser.getText();
	}
	
	
	public String getImage() {
		return jImageChooser.getText();
	}
	
	
	public String getCoord() {
		return jCoordFileChooser.getText();
	}
	
	
	public String getInput() {
		return jInputFileChooser.getText();
	}
	
	
	public boolean isOmeroEnabled() {
		return useOMERO;
	}
	
	
	public String getHostname() {
		return jTextFieldHostname.getText();
	}
	
	
	public String getPort() {
		return jTextFieldPort.getText();
	}
	
	
	public String getSourceID() {
		return jTextFieldSourceID.getText();
	}
	
	public String getToCropID() {
		return jTextFieldToCropID.getText();
	}
	public String getChannelToCrop(){ return jTextFieldChannelToCrop.getText(); }
	
	
	public String getDataType() {
		return (String) jComboBoxDataType.getSelectedItem();
	}
	public String getDataTypeToCrop() {
		return (String) jComboBoxDataTypeToCrop.getSelectedItem();
	}
	
	
	public String getUsername() {
		return jTextFieldUsername.getText();
	}
	
	
	public String getPassword() {
		return String.valueOf(jPasswordField.getPassword());
	}
	
	
	public String getGroup() {
		return jTextFieldGroup.getText();
	}
	
	
	public String getOutputProject() {
		return jTextFieldOutputProject.getText();
	}
	
	
	public void actionPerformed(ActionEvent e) {
		
		if (((JButton) e.getSource()).getName().equals(INPUT_CHOOSER)) {
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}
		fc.setAcceptAllFileFilterUsed(false);
		
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			switch (((JButton) e.getSource()).getName()) {
				case INPUT_CHOOSER:
					File selectedInput = fc.getSelectedFile();
					jInputFileChooser.setText(selectedInput.getPath());
					break;
				case OUTPUT_CHOOSER:
					File selectedOutput = fc.getSelectedFile();
					jTextFieldOutputProject.setText(selectedOutput.getPath());
					break;
			}
		}
		fc.setSelectedFile(null);
	}
	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getSource();
		
		if (source == omeroNoButton) {
			container.remove(1);
			container.add(localModeLayout, 1);
			useOMERO = false;
		} else if (source == omeroYesButton) {
			container.remove(1);
			container.add(omeroModeLayout, 1);
			useOMERO = true;
		} else {
			container.remove(3);
		}
		
		validate();
		repaint();
	}
	
	
	static class QuitListener implements ActionListener {
		CropFromCoodinateDialog autocropDialog;
		
		
		/** @param autocropDialog  */
		public QuitListener(CropFromCoodinateDialog autocropDialog) {
			this.autocropDialog = autocropDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			autocropDialog.dispose();
		}
		
	}
	
	/** Classes listener to interact with the several elements of the window */
	class StartListener implements ActionListener {
		CropFromCoodinateDialog autocropDialog;
		
		
		/** @param autocropDialog  */
		public StartListener(CropFromCoodinateDialog autocropDialog) {
			this.autocropDialog = autocropDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			start = true;
			autocropDialog.dispose();
		}
		
	}
	
	
}
