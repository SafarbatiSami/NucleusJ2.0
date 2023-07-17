package gred.nucleus.dialogs;

import gred.nucleus.core.NucleusSegmentation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;


public class SegmentationConfigDialog extends JFrame implements ItemListener {
	private final JTextField         minVolume           = new JTextField();
	private final JTextField         maxVolume             = new JTextField();
	private final JCheckBox          convexHullDetection   = new JCheckBox();
	private final JTextField         xCalibration          = new JTextField();
	private final JTextField         yCalibration          = new JTextField();
	private final JTextField         zCalibration          = new JTextField();
	private final JCheckBox          addCalibrationBox     = new JCheckBox();
	private final JButton            buttonOK              = new JButton("Done");
	private final JPanel             volumePane;
	private final Container          container;
	private final SegmentationDialog caller;
	private       Boolean            isConvexHullDetection = true;
	private       JPanel             xCalibrationPanel;
	private       JPanel             yCalibrationPanel;
	private       JPanel             zCalibrationPanel;
	
	
	public SegmentationConfigDialog(SegmentationDialog caller) {
		this.caller = caller;
		this.setTitle("Segmentation NucleusJ2");
		this.setSize(300, 340);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{1.0};
		gridBagLayout.rowHeights = new int[]{300};
		gridBagLayout.columnWeights = new double[]{0.0, 0.3};
		gridBagLayout.columnWidths = new int[]{180, 500};
		
		container.setLayout(gridBagLayout);
		getRootPane().setDefaultButton(buttonOK);


        /*/\*\
        -------------------------- Crop Box -----------------------------------
        \*\/*/
		
		
		volumePane = new JPanel();
		volumePane.setLayout(new BoxLayout(volumePane, BoxLayout.Y_AXIS));
		volumePane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		volumePane.setAlignmentX(0);
		
		JPanel minVolumePane = new JPanel();
		minVolumePane.setLayout(new BoxLayout(minVolumePane, BoxLayout.X_AXIS));
		minVolumePane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel xBox = new JLabel("Min:");
		minVolumePane.add(xBox);
		minVolumePane.add(Box.createRigidArea(new Dimension(10, 0)));
		minVolume.setText("1");
		minVolume.setMinimumSize(new Dimension(60, 10));
		minVolumePane.add(minVolume);
		
		JPanel maxVolumePane = new JPanel();
		maxVolumePane.setLayout(new BoxLayout(maxVolumePane, BoxLayout.X_AXIS));
		maxVolumePane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel yBox = new JLabel("Max:");
		maxVolumePane.add(yBox);
		maxVolumePane.add(Box.createRigidArea(new Dimension(10, 0)));
		maxVolume.setText("3000000");
		maxVolume.setMinimumSize(new Dimension(60, 10));
		maxVolumePane.add(maxVolume);
		
		JPanel convexHullPane = new JPanel();
		convexHullPane.setLayout(new BoxLayout(convexHullPane, BoxLayout.X_AXIS));
		convexHullPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		JLabel zBox = new JLabel("Convex Hull Detection (" + NucleusSegmentation.CONVEX_HULL_ALGORITHM +":");
		convexHullPane.add(zBox);
		convexHullPane.add(Box.createRigidArea(new Dimension(10, 0)));
		convexHullDetection.setSelected(true);
		convexHullDetection.setMinimumSize(new Dimension(100, 10));
		convexHullDetection.addItemListener(this);
		convexHullPane.add(convexHullDetection);
		
		JLabel volumeLabel = new JLabel("Volume:");
		volumeLabel.setAlignmentX(0);
		volumePane.add(volumeLabel);
		volumePane.add(minVolumePane);
		volumePane.add(maxVolumePane);
		volumePane.add(convexHullPane);
		volumePane.add(Box.createRigidArea(new Dimension(0, 20)));


        /*/\*\
        -------------------------- Calibration -----------------------------------
        \*\/*/
		
		
		JPanel calibrationPanel = new JPanel();
		JLabel calibrationLabel = new JLabel("Calibration:");
		calibrationLabel.setAlignmentX(0);
		calibrationPanel.add(calibrationLabel);
		addCalibrationBox.setSelected(false);
		addCalibrationBox.setMinimumSize(new Dimension(100, 10));
		addCalibrationBox.addItemListener(this);
		calibrationPanel.add(addCalibrationBox);
		volumePane.add(calibrationPanel);


        /*/\*\
        -------------------------- Validation Button -----------------------------------
        \*\/*/
		
		
		buttonOK.setPreferredSize(new java.awt.Dimension(80, 21));
		volumePane.add(Box.createRigidArea(new Dimension(0, 10)));
		volumePane.add(buttonOK);
		
		container.add(volumePane, new GridBagConstraints(0,
		                                                 0,
		                                                 0,
		                                                 0,
		                                                 0.0,
		                                                 0.0,
		                                                 GridBagConstraints.NORTHWEST,
		                                                 GridBagConstraints.NONE,
		                                                 new Insets(0, 0, 0, 0),
		                                                 0,
		                                                 0));
		
		this.setVisible(false);
		
		SegmentationConfigDialog.StartListener startListener = new StartListener(this);
		buttonOK.addActionListener(startListener);
	}
	
	
	public String getMinVolume() {
		return minVolume.getText();
	}
	
	
	public String getMaxVolume() {
		return maxVolume.getText();
	}
	
	
	public boolean getConvexHullDetection() {
		return convexHullDetection.isSelected();
	}
	
	
	public String getXCalibration() {
		return xCalibration.getText();
	}
	
	
	public String getYCalibration() {
		return yCalibration.getText();
	}
	
	
	public String getZCalibration() {
		return zCalibration.getText();
	}
	
	
	public boolean isCalibrationSelected() {
		return addCalibrationBox.isSelected();
	}
	
	
	@Override
	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == convexHullDetection) {
			isConvexHullDetection = convexHullDetection.isSelected();
		} else if (e.getSource() == addCalibrationBox) {
			if (addCalibrationBox.isSelected()) {
				
				xCalibrationPanel = new JPanel();
				xCalibrationPanel.setLayout(new BoxLayout(xCalibrationPanel, BoxLayout.X_AXIS));
				xCalibrationPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
				JLabel xBox2 = new JLabel("X:");
				xCalibrationPanel.add(xBox2);
				xCalibrationPanel.add(Box.createRigidArea(new Dimension(10, 0)));
				xCalibration.setText("1");
				xCalibration.setMinimumSize(new Dimension(60, 10));
				xCalibrationPanel.add(xCalibration);
				
				yCalibrationPanel = new JPanel();
				yCalibrationPanel.setLayout(new BoxLayout(yCalibrationPanel, BoxLayout.X_AXIS));
				yCalibrationPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
				JLabel yBox2 = new JLabel("Y:");
				yCalibrationPanel.add(yBox2);
				yCalibrationPanel.add(Box.createRigidArea(new Dimension(10, 0)));
				yCalibration.setText("1");
				yCalibration.setMinimumSize(new Dimension(60, 10));
				yCalibrationPanel.add(yCalibration);
				
				zCalibrationPanel = new JPanel();
				zCalibrationPanel.setLayout(new BoxLayout(zCalibrationPanel, BoxLayout.X_AXIS));
				zCalibrationPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
				JLabel zBox2 = new JLabel("Z:");
				zCalibrationPanel.add(zBox2);
				zCalibrationPanel.add(Box.createRigidArea(new Dimension(10, 0)));
				zCalibration.setText("1");
				zCalibration.setMinimumSize(new Dimension(60, 10));
				zCalibrationPanel.add(zCalibration);
				
				volumePane.remove(buttonOK);
				volumePane.add(xCalibrationPanel);
				volumePane.add(yCalibrationPanel);
				volumePane.add(zCalibrationPanel);
				volumePane.add(buttonOK);
			} else {
				try {
					volumePane.remove(buttonOK);
					volumePane.remove(xCalibrationPanel);
					volumePane.remove(yCalibrationPanel);
					volumePane.remove(zCalibrationPanel);
					volumePane.add(buttonOK);
				} catch (NullPointerException nullPointerException) {
					// Do nothing
				}
			}
		}
		validate();
		repaint();
	}
	
	
	static class StartListener implements ActionListener {
		SegmentationConfigDialog segmentationDialog;
		
		
		/** @param segmentationDialog  */
		public StartListener(SegmentationConfigDialog segmentationDialog) {
			this.segmentationDialog = segmentationDialog;
		}
		
		
		public void actionPerformed(ActionEvent actionEvent) {
			segmentationDialog.setVisible(false);
		}
		
	}
	
}