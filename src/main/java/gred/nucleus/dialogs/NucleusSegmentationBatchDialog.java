package gred.nucleus.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;


/**
 * Class to construct graphical interface for the nucleus segmentation in batch
 *
 * @author pouletaxel
 */
public class NucleusSegmentationBatchDialog extends JFrame {
	private static final long                serialVersionUID        = 1L;
	private final        JFormattedTextField jTextFieldXCalibration  = new JFormattedTextField(Number.class);
	private final        JFormattedTextField jTextFieldYCalibration  = new JFormattedTextField(Number.class);
	private final        JFormattedTextField jTextFieldZCalibration  = new JFormattedTextField(Number.class);
	private final        JFormattedTextField jTextFieldMax           = new JFormattedTextField(Number.class);
	private final        JFormattedTextField jTextFieldMin           = new JFormattedTextField(Number.class);
	private final        JTextField          jTextFieldUnit          = new JTextField();
	private final        JTextField          jTextFieldWorkDirectory = new JTextField();
	private final        JTextField          jTextFieldRawData       = new JTextField();
	private              boolean             start                   = false;
	private              int                 nbCpuChosen             = 1;
	
	
	/** Architecture of the graphical windows */
	public NucleusSegmentationBatchDialog() {
		final JButton            jButtonWorkDirectory = new JButton("Output Directory");
		final JButton            jButtonStart         = new JButton("Start");
		final JButton            jButtonQuit          = new JButton("Quit");
		final JButton            jButtonRawData       = new JButton("Raw Data");
		final JComboBox<Integer> comboBoxCpu          = new JComboBox<>();
		final Container          container;
		final JLabel             jLabelXCalibration;
		final JLabel             jLabelYCalibration;
		final JLabel             jLabelZCalibration;
		final JLabel             jLabelUnit;
		final JLabel             jLabelSegmentation;
		final JLabel             jLabelVolumeMin;
		final JLabel             jLabelVolumeMax;
		final JLabel             jLabelWorkDirectory;
		final JLabel             jLabelCalibration;
		final JLabel             jLabelNbCpu;
		this.setTitle("Nucleus segmentation (batch)");
		this.setSize(500, 450);
		this.setLocationRelativeTo(null);
		container = getContentPane();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.rowHeights = new int[]{17, 71, 124, 7};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.1};
		gridBagLayout.columnWidths = new int[]{236, 109, 72, 20};
		container.setLayout(gridBagLayout);
		jLabelWorkDirectory = new JLabel();
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
		jLabelWorkDirectory.setText("Work directory and Raw data choice : ");
		container.add(jButtonRawData,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(30, 10, 0, 0),
		                                     0,
		                                     0));
		jButtonRawData.setPreferredSize(new java.awt.Dimension(120, 21));
		jButtonRawData.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(jTextFieldRawData,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(30, 160, 0, 0),
		                                     0,
		                                     0));
		jTextFieldRawData.setPreferredSize(new java.awt.Dimension(280, 21));
		jTextFieldRawData.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(jButtonWorkDirectory,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(60, 10, 0, 0),
		                                     0,
		                                     0));
		jButtonWorkDirectory.setPreferredSize(new java.awt.Dimension(120, 21));
		jButtonWorkDirectory.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		container.add(jTextFieldWorkDirectory,
		              new GridBagConstraints(0,
		                                     1,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(60, 160, 0, 0),
		                                     0,
		                                     0));
		jTextFieldWorkDirectory.setPreferredSize(new java.awt.Dimension(280, 21));
		jTextFieldWorkDirectory.setFont(new java.awt.Font("Albertus", Font.ITALIC, 10));
		jLabelCalibration = new JLabel();
		container.add(jLabelCalibration,
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
		jLabelCalibration.setText("Voxel Calibration:");
		container.setLayout(gridBagLayout);
		jLabelXCalibration = new JLabel();
		container.add(jLabelXCalibration,
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
		jLabelXCalibration.setText("x :");
		jLabelXCalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(jTextFieldXCalibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(40, 60, 0, 0),
		                                     0,
		                                     0));
		jTextFieldXCalibration.setText("1");
		jTextFieldXCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		jLabelYCalibration = new JLabel();
		container.add(jLabelYCalibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(65, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelYCalibration.setText("y :");
		jLabelYCalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(jTextFieldYCalibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(65, 60, 0, 0),
		                                     0,
		                                     0));
		jTextFieldYCalibration.setText("1");
		jTextFieldYCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		jLabelZCalibration = new JLabel();
		container.add(jLabelZCalibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(90, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelZCalibration.setText("z :");
		jLabelZCalibration.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(jTextFieldZCalibration,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(90, 60, 0, 0),
		                                     0,
		                                     0));
		jTextFieldZCalibration.setText("1");
		jTextFieldZCalibration.setPreferredSize(new java.awt.Dimension(60, 21));
		jLabelUnit = new JLabel();
		container.add(jLabelUnit,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(115, 20, 0, 0),
		                                     0,
		                                     0));
		jLabelUnit.setText("unit :");
		jLabelUnit.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(jTextFieldUnit,
		              new GridBagConstraints(0,
		                                     2,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(115, 60, 0, 0),
		                                     0,
		                                     0));
		jTextFieldUnit.setText("pixel");
		jTextFieldUnit.setPreferredSize(new java.awt.Dimension(60, 21));
		jLabelSegmentation = new JLabel();
		container.add(jLabelSegmentation,
		              new GridBagConstraints(0,
		                                     3,
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
		                                     3,
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
		jLabelVolumeMin.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(jTextFieldMin,
		              new GridBagConstraints(0,
		                                     3,
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
		jLabelVolumeMax = new JLabel();
		container.add(jLabelVolumeMax,
		              new GridBagConstraints(0,
		                                     3,
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
		jLabelVolumeMax.setFont(new java.awt.Font("Albertus Extra Bold (W1)", Font.ITALIC, 12));
		container.add(jTextFieldMax,
		              new GridBagConstraints(0,
		                                     3,
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
		OperatingSystemMXBean bean  = ManagementFactory.getOperatingSystemMXBean();
		int                   nbCpu = bean.getAvailableProcessors();
		for (int i = 1; i <= nbCpu; ++i) comboBoxCpu.addItem(i);
		jLabelNbCpu = new JLabel();
		jLabelNbCpu.setText("How many CPU :");
		container.add(jLabelNbCpu,
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
		container.add(comboBoxCpu,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(90, 200, 0, 0),
		                                     0,
		                                     0));
		comboBoxCpu.addItemListener(new ItemState());
		container.add(jButtonStart,
		              new GridBagConstraints(0,
		                                     3,
		                                     0,
		                                     0,
		                                     0.0,
		                                     0.0,
		                                     GridBagConstraints.NORTHWEST,
		                                     GridBagConstraints.NONE,
		                                     new Insets(130, 140, 0, 0),
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
		                                     new Insets(130, 10, 0, 0),
		                                     0,
		                                     0));
		jButtonQuit.setPreferredSize(new java.awt.Dimension(120, 21));
		this.setVisible(true);
		WorkDirectoryListener wdListener = new WorkDirectoryListener();
		jButtonWorkDirectory.addActionListener(wdListener);
		RAwDataDirectoryListener ddListener = new RAwDataDirectoryListener();
		jButtonRawData.addActionListener(ddListener);
		QuitListener quitListener = new QuitListener(this);
		jButtonQuit.addActionListener(quitListener);
		StartListener startListener = new StartListener(this);
		jButtonStart.addActionListener(startListener);
	}
	
	
	/** @param args arguments */
	public static void main(String[] args) {
		NucleusSegmentationBatchDialog nucleusSegmentationBatchDialog = new NucleusSegmentationBatchDialog();
		nucleusSegmentationBatchDialog.setLocationRelativeTo(null);
	}
	
	
	public int getNbCpu() {
		return nbCpuChosen;
	}
	
	
	public void setNbCpu(int nb) {
		nbCpuChosen = nb;
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
	
	
	public String getWorkDirectory() {
		return jTextFieldWorkDirectory.getText();
	}
	
	
	public String getRawDataDirectory() {
		return jTextFieldRawData.getText();
	}
	
	
	public boolean isStart() {
		return start;
	}
	
	
	/**
	 *
	 */
	static class QuitListener implements ActionListener {
		final NucleusSegmentationBatchDialog nucleusSegmentationBatchDialog;
		
		
		/** @param nucleusSegmentationBatchDialog nucleusSegmentationBatchDialog GUI */
		public QuitListener(NucleusSegmentationBatchDialog nucleusSegmentationBatchDialog) {
			this.nucleusSegmentationBatchDialog = nucleusSegmentationBatchDialog;
		}
		
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			nucleusSegmentationBatchDialog.dispose();
		}
		
	}
	
	/** Classes listener to interact with the several elements of the window */
	class ItemState implements ItemListener {
		/**
		 *
		 */
		public void itemStateChanged(ItemEvent e) {
			setNbCpu((Integer) e.getItem());
		}
		
	}
	
	/**
	 *
	 */
	class StartListener implements ActionListener {
		final NucleusSegmentationBatchDialog nucleusSegmentationBatchDialog;
		
		
		/** @param nucleusSegmentationBatchDialog nucleusSegmentationBatchDialog GUI */
		public StartListener(NucleusSegmentationBatchDialog nucleusSegmentationBatchDialog) {
			this.nucleusSegmentationBatchDialog = nucleusSegmentationBatchDialog;
		}
		
		
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			if (jTextFieldWorkDirectory.getText().isEmpty() || jTextFieldRawData.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null,
				                              "You did not choose a work directory or the raw data",
				                              "Error",
				                              JOptionPane.ERROR_MESSAGE);
			} else {
				start = true;
				nucleusSegmentationBatchDialog.dispose();
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
				@SuppressWarnings("unused") String run           = jFileChooser.getSelectedFile().getName();
				String                             workDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				jTextFieldWorkDirectory.setText(workDirectory);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
	
	/**
	 *
	 */
	class RAwDataDirectoryListener implements ActionListener {
		/**
		 *
		 */
		public void actionPerformed(ActionEvent actionEvent) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			JFileChooser jFileChooser = new JFileChooser();
			jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnValue = jFileChooser.showOpenDialog(getParent());
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				@SuppressWarnings("unused") String run              = jFileChooser.getSelectedFile().getName();
				String                             rawDataDirectory = jFileChooser.getSelectedFile().getAbsolutePath();
				jTextFieldRawData.setText(rawDataDirectory);
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
		
	}
	
}