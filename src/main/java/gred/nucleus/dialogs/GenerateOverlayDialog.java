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


public class GenerateOverlayDialog extends JFrame implements ActionListener, ItemListener {
	private static final long              serialVersionUID        = 1L;

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
	private final        String[]          dataTypes               = {"Dataset"};
	private final        JComboBox<String> jComboBoxDataType       = new JComboBox<>(dataTypes);
	private final        JComboBox<String> jComboBoxDataTypeToCrop       = new JComboBox<>(dataTypes);
	private final        JTextField        jTextFieldSourceID      = new JTextField();
	private final        JTextField        zProjectionTextField      = new JTextField();
	private final        JTextField        jTextFieldOutputProject = new JTextField();
	private              Container         container;
	private              boolean           useOMERO                = false;
	private static final String            INPUT_CHOOSER           = "inputChooser";
	private static final String            INPUT_CHOOSER2           = "inputChooser2";

	private static final String  OUTPUT_CHOOSER = "outputChooser";
	private final        JTextField        DICFileChooser       = new JTextField();
	private final        JTextField        ZprojectionFileChooser       = new JTextField();
	
	public GenerateOverlayDialog() {
		
		String host = Prefs.get("omero.host", "omero.igred.fr");
		long port = Prefs.getInt("omero.port", 4064);
		String username = Prefs.get("omero.user", "");
		
		
		JButton jButtonStart = new JButton("Start");
		jButtonStart.setBackground(new Color(0x2dce98));
		jButtonStart.setForeground(Color.white);
		JButton jButtonQuit = new JButton("Quit");
		jButtonQuit.setBackground(Color.red);
		jButtonQuit.setForeground(Color.white);
		this.setTitle("Generate Overlay - NucleusJ2");
		this.setMinimumSize(new Dimension(500, 390));
		
		
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
		
		JLabel jLabelInput = new JLabel("Path to DIC file:");
		localPanel.add(jLabelInput, c);
		c.gridx = 1;
		c.insets = new Insets(0, 0, 0, 20);
		
		localPanel.add(DICFileChooser, c);
		DICFileChooser.setMaximumSize(new Dimension(10000, 20));
		DICFileChooser.setSize(new Dimension(180, 20));
		DICFileChooser.setText("path\\DIC Folder\\");
		
		JButton sourceButton = new JButton("....");
		sourceButton.setSize(new Dimension(20, 18));
		sourceButton.addActionListener(this);
		sourceButton.setName(INPUT_CHOOSER);
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 2;
		
		localPanel.add(sourceButton, c);
		
		JLabel jLabelInput2 = new JLabel("Path to Z projection file:");
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 0, 20);
		localPanel.add(jLabelInput2, c);
		c.gridx = 1;
		c.gridy = 3;
		localPanel.add(ZprojectionFileChooser, c);
		ZprojectionFileChooser.setMaximumSize(new Dimension(10000, 20));
		ZprojectionFileChooser.setSize(new Dimension(180, 20));
		ZprojectionFileChooser.setText("path\\Z projection Folder\\");
		
		JButton sourceButton2 = new JButton("...");
		sourceButton2.setSize(new Dimension(20, 20));
		sourceButton2.addActionListener(this);
		sourceButton2.setName(INPUT_CHOOSER2);
		c.insets = new Insets(0, 0, 0, 0);
		c.gridx = 2;
		c.gridy = 3;
		
		localPanel.add(sourceButton2, c);
		
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
		JLabel jLabelSource = new JLabel("Z Projection :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelSource, c);
		c.gridx = 1;
		omeroPanel.add(jComboBoxDataType, c);
		c.gridx = 2;
		omeroPanel.add(jTextFieldSourceID, c);
		jTextFieldSourceID.setMaximumSize(new Dimension(10000, 20));
		
		c.gridy = 6;
		JLabel jLabelToCrop = new JLabel("DIC  :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelToCrop, c);
		c.gridx = 1;
		omeroPanel.add(jComboBoxDataTypeToCrop, c);
		c.gridx = 2;
		omeroPanel.add(zProjectionTextField, c);
		zProjectionTextField.setMaximumSize(new Dimension(20000, 20));
		
		
		c.gridy = 7;
		JLabel jLabelOutputProject = new JLabel("Output Project :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelOutputProject, c);
		c.gridx = 1;
		c.gridwidth = 2;
		omeroPanel.add(jTextFieldOutputProject, c);
		jTextFieldOutputProject.setMaximumSize(new Dimension(10000, 20));
		
		omeroPanel.setBorder(padding);
		omeroModeLayout.add(omeroPanel);


  
		// Start/Quit buttons
		
		Border padding2 = BorderFactory.createEmptyBorder(10, 120, 10, 120);
		
		JPanel startQuitPanel = new JPanel();
		startQuitPanel.setLayout(new GridLayout(1, 2, 30, 10));
		startQuitPanel.add(jButtonStart);
		startQuitPanel.add(jButtonQuit);
		startQuitPanel.setBorder(padding2);
		container.add(startQuitPanel, 2);
		
		
		GenerateOverlayDialog.QuitListener quitListener = new QuitListener(this);
		jButtonQuit.addActionListener(quitListener);
		GenerateOverlayDialog.StartListener startListener = new GenerateOverlayDialog.StartListener(this);
		jButtonStart.addActionListener(startListener);
		this.setVisible(true);
		
		
		// DEFAULT VALUES FOR TESTING :
		jTextFieldHostname.setText(host);
		jTextFieldPort.setText(String.valueOf(port));
		jTextFieldUsername.setText(username);
		jTextFieldGroup.setText("553");
		jPasswordField.setText("");
		jTextFieldSourceID.setText("21001"); //dic
		zProjectionTextField.setText("21002");
		jTextFieldOutputProject.setText("11251");
	}
	
	
	public boolean isStart() {
		return start;
	}
	
	public String getDICInput() {
		return DICFileChooser.getText();
	}
	
	public String getZprojectionInput() {
		return ZprojectionFileChooser.getText();
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
	
	public String getzProjectionID() {
		return zProjectionTextField.getText();
	}
	
	public String getDICDataType() {
		return (String) jComboBoxDataType.getSelectedItem();
	}
	
	public String getZprojectionDataType() {
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
		if (((JButton) e.getSource()).getName().equals(INPUT_CHOOSER2)) {
			fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		}
		fc.setAcceptAllFileFilterUsed(false);
		
		if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			switch (((JButton) e.getSource()).getName()) {
				case INPUT_CHOOSER:
					File selectedInput = fc.getSelectedFile();
					DICFileChooser.setText(selectedInput.getPath());
					break;
				case INPUT_CHOOSER2:
					File selectedInput2 = fc.getSelectedFile();
					ZprojectionFileChooser.setText(selectedInput2.getPath());
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
		final GenerateOverlayDialog generateOverlayDialog;
		
		/** @param generateOverlayDialog  */
		public QuitListener(GenerateOverlayDialog generateOverlayDialog) {
			this.generateOverlayDialog = generateOverlayDialog;
		}
		public void actionPerformed(ActionEvent actionEvent) {
			generateOverlayDialog.dispose();
		}
	}
	
	/** Classes listener to interact with the several elements of the window */
	class StartListener implements ActionListener {
		final GenerateOverlayDialog generateOverlayDialog;
		
		/** @param generateOverlayDialog  */
		public StartListener(GenerateOverlayDialog generateOverlayDialog) {
			this.generateOverlayDialog = generateOverlayDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			start = true;
			generateOverlayDialog.dispose();
		}
		
	}
	
	
}
