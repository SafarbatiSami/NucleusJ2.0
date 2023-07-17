package gred.nucleus.gui;


import fr.igred.omero.exception.AccessException;
import fr.igred.omero.exception.ServiceException;
import gred.nucleus.dialogs.IDialogListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.border.Border;


/**
 * GUI for SIP program
 *
 * @author poulet axel
 *
 */

public class GuiAnalysis extends JFrame implements ItemListener, IDialogListener {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private final IDialogListener dialogListener;
	/**
	 *
	 */
	private JPanel _container = new JPanel();
	private JPanel _parameters = new JPanel();
	/**
	 *
	 */
	private JButton _jbOutputDir = new JButton("Output directory");
	/**
	 *
	 */
	private JButton _jbInputDir = new JButton("Raw Nuclei");
	
	/**
	 *
	 */
	private JButton _jbInputSeg = new JButton("Seg. Nuclei");
	
	/**
	 *
	 */
	private JCheckBox _jCbIsGauss = new JCheckBox("Apply Gaussian filter on raw images ?");
	
	/**
	 *
	 */
	private JCheckBox _jCbIs2D = new JCheckBox("Is it 2D images ?");
	
	/**
	 *
	 */
	private JCheckBox _jCbIsFilter = new JCheckBox("Filter connected components?");
	
	/**
	 *
	 */
	private JTextField _jtfWorkDir = new JTextField();
	/**
	 *
	 */
	private JTextField _jtfRawData = new JTextField();
	private JTextField _jtfRawSeg = new JTextField();
	
	
	private JButton _jbStart = new JButton("Start");
	private JButton _jbQuit = new JButton("Quit");
	
	/**
	 *
	 */
	private boolean _start = false;
	
	private JFormattedTextField _jtfGX = new JFormattedTextField(Number.class);
	private JFormattedTextField _jtfGY = new JFormattedTextField(Number.class);
	private JFormattedTextField _jtfGZ = new JFormattedTextField(Number.class);
	private JFormattedTextField _jtfMin = new JFormattedTextField(Number.class);
	private JFormattedTextField _jtfMax = new JFormattedTextField(Number.class);
	private JFormattedTextField _jtfFactor = new JFormattedTextField(Number.class);
	private JFormattedTextField _jtfNeigh = new JFormattedTextField(Number.class);
	
	private final        JRadioButton      omeroYesButton          = new JRadioButton("Yes");
	private final        JRadioButton      omeroNoButton           = new JRadioButton("No");
	private              Container         container;
	private final        JPanel            localModeLayout         = new JPanel();
	
	private final        JPanel            omeroModeLayout         = new JPanel();
	private final        JTextField        jTextFieldHostname      = new JTextField();
	private final        JTextField        jTextFieldPort          = new JTextField();
	private final        JTextField        jTextFieldUsername      = new JTextField();
	private final        JPasswordField    jPasswordField          = new JPasswordField();
	private final        JTextField        jTextFieldGroup         = new JTextField();
	private final        String[]          dataTypes               = {"Dataset", "Image"};
	private final        JComboBox<String> jComboBoxDataType       = new JComboBox<>(dataTypes);
	private final        JComboBox<String> jComboBoxDataTypeSegmented       = new JComboBox<>(dataTypes);
	private final        JTextField        jTextFieldSourceID      = new JTextField();
	private final        JTextField        segmentedNucleiTextField      = new JTextField();
	private final        JTextField        jTextFieldOutputProject = new JTextField();
	private              boolean           useOMERO                = false;
	/**
	 * java.trax.gui main2DAnalysis
	 *
	 * @param args
	 */
	public void main(String[] args) {
		GuiAnalysis gui = new GuiAnalysis(this);
		gui.setLocationRelativeTo(null);
	}
	
	
	/**
	 * GUI Architecture
	 */
	
	public GuiAnalysis(IDialogListener dialogListener) {
		this.dialogListener = dialogListener;
		///////////////////////////////////////////// Global parameter of the JFram and def of the gridBaglayout
		this.setTitle("NODeJ");
		this.setSize(550, 720);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setLocationByPlatform(true);
		this.setBackground(Color.LIGHT_GRAY);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		
		//this._container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[]{17, 500, 124, 7};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.columnWidths = new int[]{270, 120, 72, 20};
		_parameters.setLayout(gridBagLayout);
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
		radioOmeroPanel.setBorder(BorderFactory.createLineBorder(Color.black, 1));
		container.add(radioOmeroPanel, 0);
		
		
		//////////////////////////////////////// First case of the grid bag layout
		// Local mode layout
		localModeLayout.setLayout(new BoxLayout(localModeLayout, BoxLayout.Y_AXIS));
		
		this._container.setLayout(new GridBagLayout());
		
		JLabel label = new JLabel();
		label.setText("Input and Output directories: ");
		label.setFont(new java.awt.Font("arial", 1, 12));
		this._container.add(label, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0
		));
		
		this._jbInputDir.setPreferredSize(new java.awt.Dimension(150, 21));
		this._jbInputDir.setFont(new java.awt.Font("arial", 2, 10));
		this._container.add(this._jbInputDir, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(35, 20, 0, 0), 0, 0
		));
		
		this._jtfRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		this._jtfRawData.setFont(new java.awt.Font("arial", 2, 10));
		this._container.add(this._jtfRawData, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(35, 190, 0, 0), 0, 0
		));
		
		this._jbInputSeg.setPreferredSize(new java.awt.Dimension(150, 21));
		this._jbInputSeg.setFont(new java.awt.Font("arial", 2, 10));
		this._container.add(this._jbInputSeg, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(65, 20, 0, 0), 0, 0
		));
		
		this._jtfRawSeg.setPreferredSize(new java.awt.Dimension(280, 21));
		this._jtfRawSeg.setFont(new java.awt.Font("arial", 2, 10));
		this._container.add(this._jtfRawSeg, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(65, 190, 0, 0), 0, 0
		));
		
		
		
		this._jbOutputDir.setPreferredSize(new java.awt.Dimension(150, 21));
		this._jbOutputDir.setFont(new java.awt.Font("arial", 2, 10));
		this._container.add(this._jbOutputDir, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(95, 20, 0, 0), 0, 0
		));
		
		this._jtfWorkDir.setPreferredSize(new java.awt.Dimension(280, 21));
		this._jtfWorkDir.setFont(new java.awt.Font("arial", 2, 10));
		this._container.add(this._jtfWorkDir, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(95, 190, 0, 0), 0, 0
		));
		
		
		/////////////////////// group of radio button to choose the input type file
		label = new JLabel();
		label.setFont(new java.awt.Font("arial", 1, 12));
		label.setText("Parameters:");
		this._parameters.add(label, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(5, 10, 0, 0), 0, 0
		));
		
		this._jCbIs2D.setFont(new java.awt.Font("arial", 1, 12));
		this._parameters.add(this._jCbIs2D, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(25, 20, 0, 0), 0, 0
		));
		
		label = new JLabel();
		label.setText("Size of the neighborhood:");
		label.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( label, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(55, 20, 0, 0), 0, 0
		));
		
		this._jtfNeigh.setText("3");
		this._jtfNeigh.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfNeigh.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( this._jtfNeigh, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(52, 205, 0, 0), 0, 0
		));
		
		label = new JLabel();
		label.setText("Factor for the threshold value:");
		label.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( label, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(80, 20, 0, 0), 0, 0
		));
		
		this._jtfFactor.setText("1.5");
		this._jtfFactor.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfFactor.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( this._jtfFactor, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(78, 205, 0, 0), 0, 0
		));
		
		
		this._jCbIsGauss.setFont(new java.awt.Font("arial", 1, 12));
		this._parameters.add(this._jCbIsGauss, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(105, 20, 0, 0), 0, 0
		));
		
		
		label = new JLabel();
		label.setText("Gaussian Blur X sigma:");
		label.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( label, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(135, 20, 0, 0), 0, 0
		));
		
		this._jtfGX.setText("1");
		this._jtfGX.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfGX.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( this._jtfGX, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(132, 175, 0, 0), 0, 0
		));
		
		label = new JLabel();
		label.setText("Gaussian Blur Y sigma:");
		label.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( label, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(160, 20, 0, 0), 0, 0
		));
		
		this._jtfGY.setText("1");
		this._jtfGY.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfGY.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( this._jtfGY, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(158, 175, 0, 0), 0, 0
		));
		
		label = new JLabel();
		label.setText("Gaussian Blur Z sigma:");
		label.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( label, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(185, 20, 0, 0), 0, 0
		));
		
		this._jtfGZ.setText("2");
		this._jtfGZ.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfGZ.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( this._jtfGZ, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(182, 175, 0, 0), 0, 0
		));
		
		
		label = new JLabel();
		label.setText("Connected component filtering parameters: ");
		label.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( label, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(215, 10, 0, 0), 0, 0
		));
		
		this._jCbIsFilter.setFont(new java.awt.Font("arial", 1, 12));
		this._parameters.add(this._jCbIsFilter, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(240, 20, 0, 0), 0, 0
		));
		
		label = new JLabel();
		label.setText("Min volume:");
		label.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( label, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(275, 20, 0, 0), 0, 0
		));
		
		this._jtfMin.setText("0.003");
		this._jtfMin.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfMin.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( this._jtfMin, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(272, 175, 0, 0), 0, 0
		));
		
		label = new JLabel();
		label.setText("Max volume:");
		label.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( label, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(305, 20, 0, 0), 0, 0
		));
		
		this._jtfMax.setText("3");
		this._jtfMax.setPreferredSize(new java.awt.Dimension(60, 21));
		this._jtfMax.setFont(new java.awt.Font("arial",1,12));
		this._parameters.add( this._jtfMax, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0,  GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(302, 175, 0, 0), 0, 0
		));
		
		_jtfMax.setEnabled(false);
		_jtfMin.setEnabled(false);

/////////////////////////////////////////////////////////////////////////
		
		
		//////////////////////////////////////
		
		
		
		
		this._jbStart.setPreferredSize(new java.awt.Dimension(120, 21));
		this._jbQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		
		this._parameters.add(this._jbStart, new GridBagConstraints(
				0, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(330, 140, 0, 0), 0, 0
		));
		this._parameters.add(this._jbQuit, new GridBagConstraints(
				1, 1, 0, 0, 0.0, 0.0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(330, 10, 0, 0), 0, 0
		));
		
		localModeLayout.add(this._container);
		container.add(localModeLayout, 1);
		container.add(_parameters,2);
		
		// Omero mode layout
		omeroModeLayout.setLayout(new BoxLayout(omeroModeLayout, BoxLayout.Y_AXIS));
		
		JPanel        omeroPanel  = new JPanel();
		GridBagLayout omeroLayout = new GridBagLayout();
		omeroLayout.columnWeights = new double[]{0.1, 0.1, 2};
		omeroPanel.setLayout(omeroLayout);
		GridBagConstraints c = new GridBagConstraints();
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
		JLabel jLabelSource = new JLabel("Raw Nuclei :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelSource, c);
		c.gridx = 1;
		omeroPanel.add(jComboBoxDataType, c);
		c.gridx = 2;
		omeroPanel.add(jTextFieldSourceID, c);
		jTextFieldSourceID.setMaximumSize(new Dimension(10000, 20));
		
		c.gridy = 6;
		JLabel jLabelToCrop = new JLabel("Segmented Nuclei  :");
		c.gridx = 0;
		c.gridwidth = 1;
		omeroPanel.add(jLabelToCrop, c);
		c.gridx = 1;
		omeroPanel.add(jComboBoxDataTypeSegmented, c);
		c.gridx = 2;
		omeroPanel.add(segmentedNucleiTextField, c);
		segmentedNucleiTextField.setMaximumSize(new Dimension(20000, 20));
		
		
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


//////////////////////////////////////////////////////////////////
		RBDeconvListener analysis = new RBDeconvListener(this);
		this._jCbIs2D.addActionListener(analysis);
		this._jCbIsFilter.addActionListener(analysis);
		
		Listener wdListener = new Listener(this, _jtfWorkDir,false);
		this._jbOutputDir.addActionListener(wdListener);
		Listener rawListener = new Listener(this, this._jtfRawData, false);
		this._jbInputDir.addActionListener(rawListener);
		Listener segListener = new Listener(this, this._jtfRawSeg, false);
		this._jbInputSeg.addActionListener(segListener);
		
		
		QuitListener quitListener = new QuitListener(this);
		this._jbQuit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		this._jbStart.addActionListener(startListener);
		this.setVisible(true);
		
		
		// DEFAULT VALUES FOR TESTING :
		jTextFieldHostname.setText("omero.igred.fr");
		jTextFieldPort.setText("4064");
		
		jTextFieldUsername.setText("demo");
		jPasswordField.setText("Isim@42");
		jTextFieldGroup.setText("553");
		
		
		jTextFieldSourceID.setText("19699");
		segmentedNucleiTextField.setText("27343");
		
		jTextFieldOutputProject.setText("12788");
	}
	
	
	/**
	 * /**
	 * getter of the workdir path
	 *
	 * @return String workdir path
	 */
	public String getOutputDir() {
		return this._jtfWorkDir.getText();
	}
	
	
	/**
	 *
	 *
	 * @return String input path
	 */
	public String getInputRaw() {
		return this._jtfRawData.getText();
	}
	public String getInputSeg() { return this._jtfRawSeg.getText(); }
	
	
	public double getMin(){
		String x = this._jtfMin.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	public double getFactor(){
		String x = this._jtfFactor.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	public int getNeigh(){
		String x = this._jtfNeigh.getText();
		return Integer.parseInt(x.replaceAll(",", "."));
	}
	
	public double getMax(){
		String x = this._jtfMax.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	public double getGaussianX(){
		String x = this._jtfGX.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	public double getGaussianY(){
		String x = this._jtfGY.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	public double getGaussianZ(){
		String x = this._jtfGZ.getText();
		return Double.parseDouble(x.replaceAll(",", "."));
	}
	
	public boolean isStart() {
		return this._start;
	}
	
	public boolean is2D() {
		return this._jCbIs2D.isSelected();
	}
	public boolean isGaussian() {
		return this._jCbIsGauss.isSelected();
	}
	public boolean isFilter() {
		return this._jCbIsFilter.isSelected();
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
	
	public String getSegmentedNucleiID() {
		return segmentedNucleiTextField.getText();
	}
	public String getDataType() {
		return (String) jComboBoxDataType.getSelectedItem();
	}
	public String getDataTypeSegmented() {
		return (String) jComboBoxDataTypeSegmented.getSelectedItem();
	}
	
	public String getUsername() {
		return jTextFieldUsername.getText();
	}
	
	
	public char[] getPassword() {
		return jPasswordField.getPassword();
	}
	
	
	public String getGroup() {
		return jTextFieldGroup.getText();
	}
	
	
	public String getOutputProject() {
		return jTextFieldOutputProject.getText();
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
	
	
	@Override
	public void OnStart() {
	
	}
	
	
	/********************************************************************************************************************************************
	 * 	Classes listener to interact with the several element of the window
	 */
	/********************************************************************************************************************************************
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************
	 /********************************************************************************************************************************************/
	
	
	/**
	 * Radio button listener, manage teh access of the different button box etc on function of the parameters choose
	 *
	 * @author axel poulet
	 */
	class RBDeconvListener implements ActionListener {
		/**
		 *
		 */
		GuiAnalysis _gui;
		
		/**
		 * @param gui
		 */
		public RBDeconvListener(GuiAnalysis gui) {
			_gui = gui;
		}
		
		/**
		 * manage the access of the different java.trax.gui element on function of the paramter choose
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			if (_gui.is2D()) {
				_jtfGY.setEnabled(false);
				_jtfGZ.setEnabled(false);
			} else if (_gui.is2D() == false) {
				_jtfGY.setEnabled(true);
				_jtfGZ.setEnabled(true);
			}
			
			if (_gui.isFilter()) {
				_jtfMax.setEnabled(true);
				_jtfMin.setEnabled(true);
			}else if (_gui.isFilter() == false) {
				_jtfMax.setEnabled(false);
				_jtfMin.setEnabled(false);
			}
		}
	}
	
	
	
	
	/**
	 * @author axel poulet
	 * Listerner for the start button
	 */
	
	class StartListener implements ActionListener {
		/** */
		GuiAnalysis _gui;
		
		/**
		 * @param gui
		 */
		public StartListener(GuiAnalysis gui) {
			_gui = gui;
		}
		
		/**
		 * Test all the box, condition etc before to allow the program to run and dispose the java.trax.gui
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			if (useOMERO==false){
				if (_jtfWorkDir.getText().isEmpty() || _jtfRawData.getText().isEmpty() || _jtfRawSeg.getText().isEmpty() ) {
					JOptionPane.showMessageDialog(
							null, "You did not choose an input/output directory",
							"Error", JOptionPane.ERROR_MESSAGE
					                             );
				} else {
					_start = true;
					_gui.dispose();
				}
			} else {
				_start = true;
				_gui.dispose();
				
			}
			try {
				dialogListener.OnStart();
			} catch (AccessException e) {
				throw new RuntimeException(e);
			} catch (ServiceException e) {
				throw new RuntimeException(e);
			} catch (ExecutionException e) {
				throw new RuntimeException(e);
			}
			
		}
	}
	
	/**
	 * Quit button listener
	 *
	 * @author axel poulet
	 */
	class QuitListener implements ActionListener {
		/** */
		GuiAnalysis _gui;
		
		/**
		 * @param gui
		 */
		public QuitListener(GuiAnalysis gui) {
			_gui = gui;
		}
		
		/**
		 * dipose the java.trax.gui and quit the program
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			_gui.dispose();
			//System.exit(0);
		}
	}
	
	
	/**
	 *
	 */
	class Listener implements ActionListener {
		/**    */
		GuiAnalysis _gui;
		/** */
		JTextField _jtf;
		/** */
		boolean _file;
		
		/**
		 *
		 * @param gui
		 * @param jtf
		 * @param file
		 */
		public Listener(GuiAnalysis gui, JTextField jtf, boolean file) {
			_gui = gui;
			_jtf = jtf;
			_file = file;
		}
		
		/**         */
		public void actionPerformed(ActionEvent actionEvent) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (_file)
				jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				String text = jFileChooser.getSelectedFile().getAbsolutePath();
				_jtf.setText(text);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
}