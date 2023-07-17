package gred.nucleus.dialogs;

import ij.measure.Calibration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Class to construct graphical interface for the nucleus segmentation and analysis pipeline
 *
 * @author Poulet Axel
 */
public class NucleusSegmentationAndAnalysisDialog extends JFrame {
	
	private static final long                serialVersionUID       = 1L;
	private final        JFormattedTextField jTextFieldXCalibration = new JFormattedTextField(Number.class);
	private final        JFormattedTextField jTextFieldYCalibration = new JFormattedTextField(Number.class);
	private final        JFormattedTextField jTextFieldZCalibration = new JFormattedTextField(Number.class);
	private final        JFormattedTextField jTextFieldMax          = new JFormattedTextField(Number.class);
	private final        JFormattedTextField jTextFieldMin          = new JFormattedTextField(Number.class);
	private final        JTextField          jTextFieldUnit         = new JTextField();
	private final        JRadioButton        jRadioButton2D         = new JRadioButton("2D");
	private final        JRadioButton        jRadioButton3D         = new JRadioButton("3D");
	private final        JRadioButton        jRadioButton2D3D       = new JRadioButton("2D and 3D");
	private              boolean             start                  = false;
	
	
	/** Architecture of the graphical windows */
	public NucleusSegmentationAndAnalysisDialog(Calibration cal) {
		final String      boldFont                  = "Albertus Extra Bold (W1)";
		final JButton     jButtonStart              = new JButton("Start");
		final JButton     jButtonQuit               = new JButton("Quit");
		final Container   container;
		final JLabel      jLabelXCalibration;
		final JLabel      jLabelYCalibration;
		final JLabel      jLabelZCalibration;
		final JLabel      jLabelUnit;
		final JLabel      jLabelSegmentation;
		final JLabel      jLabelVolumeMin;
		final JLabel      jLabelVolumeMax;
		final JLabel      jLabelAnalysis;
		final JLabel      jLabelCalibration;
		JLabel            jLabelUnitText;
		final ButtonGroup buttonGroupChoiceAnalysis = new ButtonGroup();
		this.setTitle("Nucleus segmentation & analysis");
		this.setSize(500, 350);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[]{17, 100, 124, 7};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.columnWidths = new int[]{236, 109, 72, 20};
		container.setLayout(gridBagLayout);
		
		jLabelCalibration = new JLabel();
		container.add(jLabelCalibration,
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
		jLabelCalibration.setText("Voxel Calibration:");
		
		container.setLayout(gridBagLayout);
		jLabelXCalibration = new JLabel();
		container.add(jLabelXCalibration,
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
		jLabelXCalibration.setText("x :");
		jLabelXCalibration.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		container.add(jTextFieldXCalibration,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(20, 60, 0, 0),
		                                     0,
		                                     0));
		jTextFieldXCalibration.setText(String.valueOf(cal.pixelWidth));
		jTextFieldXCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		
		jLabelYCalibration = new JLabel();
		container.add(jLabelYCalibration,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(45, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelYCalibration.setText("y :");
		jLabelYCalibration.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		container.add(jTextFieldYCalibration,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(45, 60, 0, 0),
		                                     0,
		                                     0));
		jTextFieldYCalibration.setText(String.valueOf(cal.pixelWidth));
		jTextFieldYCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		
		jLabelZCalibration = new JLabel();
		container.add(jLabelZCalibration,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(70, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelZCalibration.setText("z :");
		jLabelZCalibration.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		container.add(jTextFieldZCalibration,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(70, 60, 0, 0),
		                                     0,
		                                     0));
		jTextFieldZCalibration.setText(String.valueOf(cal.pixelDepth));
		jTextFieldZCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		
		jLabelUnit = new JLabel();
		container.add(jLabelUnit,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(95, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelUnit.setText("unit :");
		jLabelUnit.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		container.add(jTextFieldUnit,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(95, 60, 0, 0),
		                                     0,
		                                     0));
		jTextFieldUnit.setText(cal.getUnit());
		jTextFieldUnit.setPreferredSize(new java.awt.Dimension(60, 21));
		
		
		jLabelSegmentation = new JLabel();
		container.add(jLabelSegmentation,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(20, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelSegmentation.setText("Choose the min and max volumes of the nucleus:");
		
		jLabelVolumeMin = new JLabel();
		container.add(jLabelVolumeMin,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(40, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelVolumeMin.setText("Minimum volume of the segmented nucleus :");
		jLabelVolumeMin.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		container.add(jTextFieldMin,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(40, 320, 0, 0),
		                                     0,
		                                     0));
		jTextFieldMin.setText("15");
		jTextFieldMin.setPreferredSize(new java.awt.Dimension(60, 21));
		
		jLabelUnitText = new JLabel();
		container.add(jLabelUnitText,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(40, 410, 0, 0),
		                                     0,
		                                     0));
		jLabelUnitText.setText("unit^3");
		jLabelUnitText.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		
		jLabelVolumeMax = new JLabel();
		container.add(jLabelVolumeMax,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(70, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelVolumeMax.setText("Maximum volume of the segmented nucleus :");
		jLabelVolumeMax.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		container.add(jTextFieldMax,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(70, 320, 0, 0),
		                                     0,
		                                     0));
		jTextFieldMax.setText("2000");
		jTextFieldMax.setPreferredSize(new java.awt.Dimension(60, 21));
		jLabelUnitText = new JLabel();
		container.add(jLabelUnitText,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(70, 410, 0, 0),
		                                     0,
		                                     0));
		jLabelUnitText.setText("unit^3");
		jLabelUnitText.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		
		jLabelAnalysis = new JLabel();
		container.add(jLabelAnalysis,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(100, 10, 0, 0),
		                                     0,
		                                     0));
		jLabelAnalysis.setText("Type of analysis:");
		
		buttonGroupChoiceAnalysis.add(jRadioButton2D);
		buttonGroupChoiceAnalysis.add(jRadioButton3D);
		buttonGroupChoiceAnalysis.add(jRadioButton2D3D);
		jRadioButton2D.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		jRadioButton3D.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		jRadioButton2D3D.setFont(new java.awt.Font(boldFont, Font.ITALIC, 12));
		container.add(jRadioButton2D,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(130, 170, 0, 0),
		                                     0,
		                                     0));
		container.add(jRadioButton3D,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(130, 120, 0, 0),
		                                     0,
		                                     0));
		container.add(jRadioButton2D3D,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(130, 10, 0, 0),
		                                     0,
		                                     0));
		jRadioButton2D3D.setSelected(true);
		
		container.add(jButtonStart,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(160, 140, 0, 0),
		                                     0,
		                                     0));
		jButtonStart.setPreferredSize(new java.awt.Dimension(120, 21));
		container.add(jButtonQuit,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(160, 10, 0, 0),
		                                     0,
		                                     0));
		jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		this.setVisible(true);
		
		QuitListener quitListener = new QuitListener(this);
		jButtonQuit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		jButtonStart.addActionListener(startListener);
	}
	
	
	public double getXCalibration() {
		String xCal = jTextFieldXCalibration.getText();
		return Double.parseDouble(xCal.replace(",", "."));
	}
	
	
	public double getYCalibration() {
		String yCal = jTextFieldYCalibration.getText();
		return Double.parseDouble(yCal.replace(",", "."));
	}
	
	
	public double getZCalibration() {
		String zCal = jTextFieldZCalibration.getText();
		return Double.parseDouble(zCal.replace(",", "."));
	}
	
	
	public String getUnit() {
		return jTextFieldUnit.getText();
	}
	
	
	public double getMinVolume() {
		return Double.parseDouble(jTextFieldMin.getText());
	}
	
	
	public double getMaxVolume() {
		return Double.parseDouble(jTextFieldMax.getText());
	}
	
	
	public boolean isStart() {
		return start;
	}
	
	
	public boolean is2D3DAnalysis() {
		return jRadioButton2D3D.isSelected();
	}
	
	
	public boolean is2D() {
		return jRadioButton2D.isSelected();
	}
	
	
	public boolean is3D() {
		return jRadioButton3D.isSelected();
	}
	
	
	/**
	 *
	 */
	static class QuitListener implements ActionListener {
		final NucleusSegmentationAndAnalysisDialog nucleusSegmentationAndAnalysisDialog;
		
		
		/** @param nucleusSegmentationAndAnalysisDialog nucleusSegmentationAndAnalysisDialog GUI */
		public QuitListener(NucleusSegmentationAndAnalysisDialog nucleusSegmentationAndAnalysisDialog) {
			this.nucleusSegmentationAndAnalysisDialog = nucleusSegmentationAndAnalysisDialog;
		}
		
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			nucleusSegmentationAndAnalysisDialog.dispose();
		}
		
	}
	
	/** Classes listener to interact with the several elements of the window */
	class StartListener implements ActionListener {
		
		final NucleusSegmentationAndAnalysisDialog nucleusSegmentationAndAnalysisDialog;
		
		
		/** @param nucleusSegmentationAndAnalysisDialog nucleusSegmentationAndAnalysisDialog GUI */
		public StartListener(NucleusSegmentationAndAnalysisDialog nucleusSegmentationAndAnalysisDialog) {
			this.nucleusSegmentationAndAnalysisDialog = nucleusSegmentationAndAnalysisDialog;
		}
		
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			start = true;
			nucleusSegmentationAndAnalysisDialog.dispose();
		}
		
	}
	
}