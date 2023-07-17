package gred.nucleus.dialogs;


import gred.nucleus.plugins.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 *
 * @author
 */
public class MainGui extends JFrame{

	private final Container container;
	JButton     AutocropButton = new JButton("Autocrop");
	JButton SegmentationButton  = new JButton("Segmentation");
	JButton OverlayButton  = new JButton("Overlay");
	JButton NODeJButton  = new JButton("NODeJ");
	JButton CropFromCoordinatesButton  = new JButton("Crop From Coordinates");
	JButton ComputeParametersButton  = new JButton("Compute Parameters");
	public MainGui(){
		this.setTitle("NucleusJ 2.0");
		this.setMinimumSize(new Dimension(400, 500));
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		
		container = getContentPane();
		BoxLayout mainBoxLayout = new BoxLayout(getContentPane(), BoxLayout.Y_AXIS);
		container.setLayout(mainBoxLayout);
		
		
		JPanel        localPanel  = new JPanel();
		localPanel.setLayout(new GridBagLayout());
		
		
		JLabel WelcomeLabel = new JLabel("Welcome to NJ !");
		localPanel.add(WelcomeLabel,new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                   GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                   new Insets(0, 60, 0, 0), 0, 0));
	
		localPanel.add(AutocropButton,new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                    new Insets(40, 30, 0, 0), 88, 0));
		

		localPanel.add(SegmentationButton,new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                         GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                         new Insets(80, 30, 0, 0), 60, 0));
	

		localPanel.add(CropFromCoordinatesButton,new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                                GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                                new Insets(120, 30, 0, 0), 8, 0));

		localPanel.add(OverlayButton,new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                    GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                    new Insets(160, 30, 0, 0), 95, 0));

		localPanel.add(ComputeParametersButton,new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                              GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                              new Insets(200, 30, 0, 0), 16, 0));

		localPanel.add(NODeJButton,new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0,
		                                                  GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
		                                                  new Insets(240, 30, 0, 0), 100, 0));
		
		
		container.add(localPanel,0);
		
		AutocropButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Autocrop_ autocrop_ = new Autocrop_();
				autocrop_.run("");
			}
		});
		
		SegmentationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Segmentation_ segmentation = new Segmentation_();
				segmentation.run("");
			}
		});
		
		CropFromCoordinatesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CropFromCoordinates_ cropFromCoordinates = new CropFromCoordinates_();
				cropFromCoordinates.run("");
			}
		});
		
		OverlayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GenerateOverlay_ overlay = new GenerateOverlay_();
				overlay.run("");
			}
		});
		
		ComputeParametersButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ComputeParametersPlugin_ computeParameters = new ComputeParametersPlugin_();
				computeParameters.run("");
			}
		});
		
		
		NODeJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NODeJ nodej = new NODeJ();
				nodej.run("");
			}
		});
		
		
		
	}
	
}