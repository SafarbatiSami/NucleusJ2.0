package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


/**
 * Class to construct graphical interface for the chromocenter analysis pipeline in batch
 *
 * @author pouletaxel
 */
public class ChromocentersAnalysisPipelineBatchDialog extends JFrame implements ItemListener {
	private static final long         serialVersionUID        = 1L;
	private final        JTextField   jTextFieldWorkDirectory = new JTextField();
	private final        JTextField   jTextFieldRawData       = new JTextField();
	private final        JRadioButton jRadioButtonRhfV        = new JRadioButton("VolumeRHF");
	private final        JRadioButton jRadioButtonRhfI        = new JRadioButton("IntensityRHF");
	private final        JRadioButton jRadioButtonRhfIV       = new JRadioButton("VolumeRHF and IntensityRHF");
	private final        JRadioButton jRadioButtonNucCc       = new JRadioButton("Nucleus and chromocenter");
	private final        JRadioButton jRadioButtonCc          = new JRadioButton("Chromocenter");
	private final        JRadioButton jRadioButtonNuc         = new JRadioButton("Nucleus");
	private final        JLabel       jLabelUnit              = new JLabel();
	private final        JLabel       jLabelXCalibration      = new JLabel();
	private final        JLabel       jLabelYCalibration      = new JLabel();
	private final        JLabel       jLabelZCalibration      = new JLabel();
	private final        JTextPane    readUnit                = new JTextPane();
	private final        JTextPane    readXCalibration        = new JTextPane();
	private final        JTextPane    readYCalibration        = new JTextPane();
	private final        JTextPane    readZCalibration        = new JTextPane();
	private final        JCheckBox    addCalibrationBox       = new JCheckBox();
	private final        JPanel       calibration;
	private              boolean      start                   = false;
	
	
	/** Architecture of the graphical windows */
	public ChromocentersAnalysisPipelineBatchDialog() {
		final String      font                      = "Albertus";
		final String      boldFont                  = "Albertus Extra Bold (W1)";
		final Container   container                 = getContentPane();
		final JLabel      jLabelWorkDirectory       = new JLabel();
		final JButton     jButtonWorkDirectory      = new JButton("Output Directory");
		final JButton     jButtonStart              = new JButton("Start");
		final JButton     jButtonQuit               = new JButton("Quit");
		final JButton     jButtonRawData            = new JButton("Raw Data");
		final ButtonGroup buttonGroupChoiceAnalysis = new ButtonGroup();
		final ButtonGroup buttonGroupChoiceRhf      = new ButtonGroup();
		JLabel            jLabelAnalysis;
		this.setTitle("Chromocenters Analysis Pipeline (Batch)");
		this.setSize(500, 600);
		this.setLocationRelativeTo(null);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[]{17, 200, 124, 7};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.columnWidths = new int[]{236, 120, 72, 20};
		container.setLayout(gridBagLayout);
		container.add(jLabelWorkDirectory,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(0, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelWorkDirectory.setText("Work directory and data directory choice : ");
		JTextPane jTextPane = new JTextPane();
		jTextPane.setText(
				"The Raw Data directory must contain 3 subdirectories:\n1. for raw nuclei images, named RawDataNucleus. \n2. for segmented nuclei images, named SegmentedDataNucleus.\n3. for segmented images of chromocenters, named SegmentedDataCc.\nPlease keep the same file name during the image processing.");
		jTextPane.setEditable(false);
		container.add(jTextPane,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(20, 20, 0, 0),
		                                     0,
		                                     0));
		container.add(jButtonRawData,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(110, 10, 0, 0),
		                                     0,
		                                     0));
		jButtonRawData.setPreferredSize(new java.awt.Dimension(120, 21));
		jButtonRawData.setFont(new java.awt.Font(font, Font.ITALIC, 10));
		container.add(jTextFieldRawData,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(110, 160, 0, 0),
		                                     0,
		                                     0));
		jTextFieldRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		jTextFieldRawData.setFont(new java.awt.Font(font, Font.ITALIC, 10));
		container.add(jButtonWorkDirectory,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(150, 10, 0, 0),
		                                     0,
		                                     0));
		jButtonWorkDirectory.setPreferredSize(new java.awt.Dimension(120, 21));
		jButtonWorkDirectory.setFont(new java.awt.Font(font, Font.ITALIC, 10));
		container.add(jTextFieldWorkDirectory,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(150, 160, 0, 0),
		                                     0,
		                                     0));
		jTextFieldWorkDirectory.setPreferredSize(new java.awt.Dimension(280, 21));
		jTextFieldWorkDirectory.setFont(new java.awt.Font(font, Font.ITALIC, 10));
		calibration = new JPanel();
		calibration.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		gc.weightx = 2;
		gc.weighty = 5;
		gc.anchor = GridBagConstraints.NORTHWEST;
		gc.ipady = GridBagConstraints.NORTHWEST;
		JLabel calibrationLabel = new JLabel("Calibration:");
		gc.gridx = 0;
		gc.gridy = 0;
		calibrationLabel.setAlignmentX(0);
		calibration.add(calibrationLabel);
		gc.gridx = 1;
		gc.gridy = 0;
		addCalibrationBox.setSelected(false);
		addCalibrationBox.addItemListener(this);
		calibration.add(addCalibrationBox, gc);
		container.add(calibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     2,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(0, 0, 0, 0),
		                                     0,
		                                     0));
		jLabelAnalysis = new JLabel();
		container.add(jLabelAnalysis,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(30, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelAnalysis.setText("Type of Relative Heterochromatin Fraction:");
		buttonGroupChoiceRhf.add(jRadioButtonRhfV);
		buttonGroupChoiceRhf.add(jRadioButtonRhfI);
		buttonGroupChoiceRhf.add(jRadioButtonRhfIV);
		jRadioButtonRhfV.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		jRadioButtonRhfI.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		jRadioButtonRhfIV.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		container.add(jRadioButtonRhfV,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(60, 370, 0, 0),
		                                     0,
		                                     0));
		container.add(jRadioButtonRhfI,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(60, 250, 0, 0),
		                                     0,
		                                     0));
		container.add(jRadioButtonRhfIV,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(60, 20, 0, 0),
		                                     0,
		                                     0));
		jRadioButtonRhfIV.setSelected(true);
		jLabelAnalysis = new JLabel();
		container.add(jLabelAnalysis,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(95, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelAnalysis.setText("Results file of interest: ");
		buttonGroupChoiceAnalysis.add(jRadioButtonNucCc);
		buttonGroupChoiceAnalysis.add(jRadioButtonCc);
		buttonGroupChoiceAnalysis.add(jRadioButtonNuc);
		jRadioButtonNuc.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		jRadioButtonCc.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		jRadioButtonNucCc.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		container.add(jRadioButtonNuc,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(120, 370, 0, 0),
		                                     0,
		                                     0));
		container.add(jRadioButtonCc,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(120, 250, 0, 0),
		                                     0,
		                                     0));
		container.add(jRadioButtonNucCc,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(120, 20, 0, 0),
		                                     0,
		                                     0));
		jRadioButtonNucCc.setSelected(true);
		container.add(jButtonStart,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(190, 140, 0, 0),
		                                     0,
		                                     0));
		jButtonStart.setPreferredSize(new java.awt.Dimension(120, 21));
		container.add(jButtonQuit,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(190, 10, 0, 0),
		                                     0,
		                                     0));
		jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		WorkDirectoryListener wdListener = new WorkDirectoryListener();
		jButtonWorkDirectory.addActionListener(wdListener);
		RawDataDirectoryListener ddListener = new RawDataDirectoryListener();
		jButtonRawData.addActionListener(ddListener);
		QuitListener quitListener = new QuitListener(this);
		jButtonQuit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		jButtonStart.addActionListener(startListener);
		this.setVisible(true);
	}
	
	
	/** @param args arguments */
	public static void main(String[] args) {
		ChromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog =
				new ChromocentersAnalysisPipelineBatchDialog();
		chromocentersAnalysisPipelineBatchDialog.setLocationRelativeTo(null);
	}
	
	
	public double getXCalibration() {
		String xCal = readXCalibration.getText();
		return Double.parseDouble(xCal.replace(",", "."));
	}
	
	
	public double getYCalibration() {
		String yCal = readYCalibration.getText();
		return Double.parseDouble(yCal.replace(",", "."));
	}
	
	
	public double getZCalibration() {
		String zCal = readZCalibration.getText();
		return Double.parseDouble(zCal.replace(",", "."));
	}
	
	
	public boolean getCalibrationStatus() {
		return addCalibrationBox.isSelected();
	}
	
	
	public String getUnit() {
		return readUnit.getText();
	}
	
	
	public String getWorkDirectory() {
		return jTextFieldWorkDirectory.getText();
	}
	
	
	public String getRawDataDirectory() {
		return jTextFieldRawData.getText();
	}
	
	
	public boolean isStart() {
		return start;
	}
	
	
	public boolean isNucAndCcAnalysis() {
		return jRadioButtonNucCc.isSelected();
	}
	
	
	public boolean isNucAnalysis() {
		return jRadioButtonNuc.isSelected();
	}
	
	
	public boolean isCcAnalysis() {
		return jRadioButtonCc.isSelected();
	}
	
	
	public boolean isRHFVolumeAndIntensity() {
		return jRadioButtonRhfIV.isSelected();
	}
	
	
	public boolean isRhfVolume() {
		return jRadioButtonRhfV.isSelected();
	}
	
	
	public boolean isRhfIntensity() {
		return jRadioButtonRhfI.isSelected();
	}
	
	
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == addCalibrationBox) {
			if (addCalibrationBox.isSelected()) {
				
				GridBagConstraints gc = new GridBagConstraints();
				gc.insets = new Insets(0, 0, 5, 0);
				
				jLabelUnit.setText("Unit :");
				gc.gridx = 0;
				gc.gridy = 1;
				calibration.add(jLabelUnit, gc);
				readUnit.setPreferredSize(new Dimension(100, 20));
				readUnit.setText("µm");
				gc.gridx = 1;
				gc.gridy = 1;
				calibration.add(readUnit, gc);
				jLabelUnit.setVisible(true);
				readUnit.setVisible(true);
				
				jLabelXCalibration.setText("X :");
				gc.gridx = 0;
				gc.gridy = 2;
				calibration.add(jLabelXCalibration, gc);
				readXCalibration.setPreferredSize(new Dimension(100, 20));
				readXCalibration.setText("1");
				gc.gridx = 1;
				gc.gridy = 2;
				calibration.add(readXCalibration, gc);
				jLabelXCalibration.setVisible(true);
				readXCalibration.setVisible(true);
				
				jLabelYCalibration.setText("Y :");
				gc.gridx = 0;
				gc.gridy = 3;
				calibration.add(jLabelYCalibration, gc);
				readYCalibration.setPreferredSize(new Dimension(100, 20));
				readYCalibration.setText("1");
				gc.gridx = 1;
				gc.gridy = 3;
				calibration.add(readYCalibration, gc);
				jLabelYCalibration.setVisible(true);
				readYCalibration.setVisible(true);
				
				jLabelZCalibration.setText("Z :");
				gc.gridx = 0;
				gc.gridy = 4;
				calibration.add(jLabelZCalibration, gc);
				readZCalibration.setPreferredSize(new Dimension(100, 20));
				readZCalibration.setText("1");
				gc.gridx = 1;
				gc.gridy = 4;
				calibration.add(readZCalibration, gc);
				jLabelZCalibration.setVisible(true);
				readZCalibration.setVisible(true);
				
				//pack();
				
			} else {
				jLabelXCalibration.setVisible(false);
				jLabelYCalibration.setVisible(false);
				jLabelZCalibration.setVisible(false);
				jLabelUnit.setVisible(false);
				
				readXCalibration.setVisible(false);
				readYCalibration.setVisible(false);
				readZCalibration.setVisible(false);
				readUnit.setVisible(false);
			}
			validate();
			repaint();
		}
	}
	
	
	/**
	 *
	 */
	static class QuitListener implements ActionListener {
		final ChromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog;
		
		
		/** @param chromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog GUI */
		public QuitListener(ChromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog) {
			this.chromocentersAnalysisPipelineBatchDialog = chromocentersAnalysisPipelineBatchDialog;
		}
		
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			chromocentersAnalysisPipelineBatchDialog.dispose();
		}
		
	}
	
	/** Classes listener to interact with the several elements of the window */
	class StartListener implements ActionListener {
		
		final ChromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog;
		
		
		/** @param chromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog GUI */
		public StartListener(ChromocentersAnalysisPipelineBatchDialog chromocentersAnalysisPipelineBatchDialog) {
			this.chromocentersAnalysisPipelineBatchDialog = chromocentersAnalysisPipelineBatchDialog;
		}
		
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			if (jTextFieldWorkDirectory.getText().isEmpty() || jTextFieldRawData.getText().isEmpty()) {
				JOptionPane.showMessageDialog
						(
								null,
								"You did not choose a work directory or the raw data",
								"Error",
								JOptionPane.ERROR_MESSAGE
						);
			} else {
				start = true;
				chromocentersAnalysisPipelineBatchDialog.dispose();
			}
		}
		
	}
	
	/**
	 *
	 */
	class WorkDirectoryListener implements ActionListener {
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				String workDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				jTextFieldWorkDirectory.setText(workDirectory);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	
	/**
	 *
	 */
	class RawDataDirectoryListener implements ActionListener {
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				@SuppressWarnings("unused")
				String run = jFileChooser.getSelectedFile().getName();
				String rawDataDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				jTextFieldRawData.setText(rawDataDirectory);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
}