package user_interface.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

public class ColorPicker extends JDialog implements ActionListener, ChangeListener{
	private static final long serialVersionUID = 2976563988811928019L;
	
	private SVSelectorPanel colorPanel;
	
	private JButton cancleButton;
	private JButton selectButton;
	private JButton resetButton;
	
	private JSlider hueSlider;
	private JSlider satSlider;
	private JSlider valSlider;

	private JSpinner hueSpinner;
	private JSpinner satSpinner;
	private JSpinner valSpinner;
	
	private JSlider redSlider;
	private JSlider greenSlider;
	private JSlider blueSlider;
	
	private JSpinner redSpinner;
	private JSpinner greenSpinner;
	private JSpinner blueSpinner;
	
	private HexTextField hexTextField;
	private ColorGrid colorGrid;
	
	private JPanel newColorSample;
	private JPanel oldColorSample;
	
	private Color current, start;
	
	private float hue, sat, val;
	private int red, green, blue;

	public ColorPicker() {
		setTitle("Color Picker");
		getContentPane().setLayout(new BorderLayout(0, 0));
		setModalityType(ModalityType.APPLICATION_MODAL);
		setAlwaysOnTop(true);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setBackground(Color.WHITE);
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new BorderLayout(0, 3));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		buttonPanel.setBorder(new EmptyBorder(0, 0, 3, 3));
		bottomPanel.add(buttonPanel, BorderLayout.EAST);
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
		selectButton = new JButton("Select");
		selectButton.setBackground(Color.WHITE);
		buttonPanel.add(selectButton);
		
		cancleButton = new JButton("Cancle");
		cancleButton.setBackground(Color.WHITE);
		buttonPanel.add(cancleButton);
		
		JSeparator separator = new JSeparator();
		bottomPanel.add(separator, BorderLayout.NORTH);
		
		JPanel resetPanel = new JPanel();
		resetPanel.setBorder(new EmptyBorder(0, 3, 3, 0));
		resetPanel.setBackground(Color.WHITE);
		bottomPanel.add(resetPanel, BorderLayout.WEST);
		resetPanel.setLayout(new BoxLayout(resetPanel, BoxLayout.X_AXIS));
		
		resetButton = new JButton("Reset");
		resetButton.setBackground(Color.WHITE);
		resetPanel.add(resetButton);
		
		JPanel inputPanel = new JPanel();
		inputPanel.setBackground(Color.WHITE);
		getContentPane().add(inputPanel, BorderLayout.EAST);
		inputPanel.setLayout(new MigLayout("", "[grow,right][grow,center][grow,center][right]", "[][][][][][][][][grow][][]"));
		
		JLabel hueLabel = new JLabel("Hue:");
		inputPanel.add(hueLabel, "cell 0 0");
		
		hueSpinner = new JSpinner();
		inputPanel.add(hueSpinner, "cell 3 0");
		hueSpinner.setModel(new SpinnerNumberModel(0, 0, 360, 1));
		
		hueSlider = new JSlider();
		hueSlider.setBackground(Color.WHITE);
		inputPanel.add(hueSlider, "cell 1 0 2 1");
		
		JLabel satLabel = new JLabel("Sat:");
		inputPanel.add(satLabel, "cell 0 1");
		
		satSlider = new JSlider();
		satSlider.setBackground(Color.WHITE);
		inputPanel.add(satSlider, "cell 1 1 2 1");
		
		satSpinner = new JSpinner();
		satSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
		inputPanel.add(satSpinner, "cell 3 1");
		
		JLabel valLabel = new JLabel("Val:");
		inputPanel.add(valLabel, "cell 0 2");
		
		valSlider = new JSlider();
		valSlider.setBackground(Color.WHITE);
		inputPanel.add(valSlider, "cell 1 2 2 1");
		
		valSpinner = new JSpinner();
		valSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
		inputPanel.add(valSpinner, "cell 3 2");
		
		JSeparator hsvRGBSerporator = new JSeparator();
		inputPanel.add(hsvRGBSerporator, "cell 0 3 4 1,growx");
		
		JLabel redLabel = new JLabel("Red:");
		inputPanel.add(redLabel, "cell 0 4");
		
		redSlider = new JSlider();
		redSlider.setBackground(Color.WHITE);
		inputPanel.add(redSlider, "cell 1 4 2 1");
		
		redSpinner = new JSpinner();
		redSpinner.setModel(new SpinnerNumberModel(0, 0, 255, 1));
		inputPanel.add(redSpinner, "cell 3 4");
		
		JLabel greenLabel = new JLabel("Green:");
		inputPanel.add(greenLabel, "cell 0 5");
		
		greenSlider = new JSlider();
		greenSlider.setBackground(Color.WHITE);
		inputPanel.add(greenSlider, "cell 1 5 2 1");
		
		greenSpinner = new JSpinner();
		greenSpinner.setModel(new SpinnerNumberModel(0, 0, 255, 1));
		inputPanel.add(greenSpinner, "cell 3 5");
		
		JLabel blueLabel = new JLabel("Blue:");
		inputPanel.add(blueLabel, "cell 0 6");
		
		blueSlider = new JSlider();
		blueSlider.setBackground(Color.WHITE);
		inputPanel.add(blueSlider, "cell 1 6 2 1");
		
		blueSpinner = new JSpinner();
		blueSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
		inputPanel.add(blueSpinner, "cell 3 6");
		
		JSeparator rgbHEXSeportator = new JSeparator();
		inputPanel.add(rgbHEXSeportator, "cell 0 7 4 1,growx");
		
		JPanel samplePanel = new JPanel();
		samplePanel.setBackground(Color.WHITE);
		samplePanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		inputPanel.add(samplePanel, "cell 2 9 2 2,grow");
		samplePanel.setLayout(new MigLayout("", "[grow][][grow]", "[][][grow]"));
		
		JLabel newColorLabel = new JLabel("New Color");
		samplePanel.add(newColorLabel, "cell 0 0,alignx center");
		
		JSeparator newOldSerparator = new JSeparator();
		newOldSerparator.setOrientation(SwingConstants.VERTICAL);
		samplePanel.add(newOldSerparator, "cell 1 0 1 3,growy");
		
		JLabel oldColorLabel = new JLabel("Old Color");
		samplePanel.add(oldColorLabel, "cell 2 0,alignx center");
		
		JSeparator labelColorSeporator = new JSeparator();
		samplePanel.add(labelColorSeporator, "cell 0 1 3 1,growx");
		
		JPanel newColorSampleWrapper = new JPanel();
		samplePanel.add(newColorSampleWrapper, "cell 0 2,grow");
		newColorSampleWrapper.setLayout(new BoxLayout(newColorSampleWrapper, BoxLayout.X_AXIS));
		
		newColorSample = new JPanel();
		newColorSample.setUI(new ColorSamplePanelUI());
		newColorSampleWrapper.add(newColorSample);
		
		JPanel oldColorSampleWrapper = new JPanel();
		samplePanel.add(oldColorSampleWrapper, "cell 2 2,grow");
		oldColorSampleWrapper.setLayout(new BoxLayout(oldColorSampleWrapper, BoxLayout.X_AXIS));
		
		oldColorSample = new JPanel();
		oldColorSample.setUI(new ColorSamplePanelUI());
		oldColorSampleWrapper.add(oldColorSample);
		
		JLabel hexLabel = new JLabel("Hex:");
		inputPanel.add(hexLabel, "cell 0 9,alignx trailing");
		
		hexTextField = new HexTextField(24);
		hexTextField.setBackground(new Color(240, 240, 240));
		inputPanel.add(hexTextField, "cell 1 9,growx,aligny bottom");
		hexTextField.setColumns(6);
		
		JPanel perviuosColors = new JPanel();
		perviuosColors.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, null, null), "Previous Colors", TitledBorder.CENTER, TitledBorder.TOP, null, null));
		perviuosColors.setBackground(Color.WHITE);
		inputPanel.add(perviuosColors, "cell 0 10 2 1,grow");
		perviuosColors.setLayout(new BorderLayout());
		
		colorGrid = new ColorGrid(3, 4);
		perviuosColors.add(colorGrid);
		
		colorPanel = new SVSelectorPanel();
		getContentPane().add(colorPanel, BorderLayout.CENTER);
		colorPanel.setBackground(Color.RED);
		
		hueSlider.setUI(new ColorSliderUI(hueSlider, p -> Color.getHSBColor(p, sat, val), 360));
		satSlider.setUI(new ColorSliderUI(satSlider, p -> Color.getHSBColor(hue, p, val), 100));
		valSlider.setUI(new ColorSliderUI(valSlider, p -> Color.getHSBColor(hue, sat, p), 100));
		
		redSlider  .setUI(new ColorSliderUI(redSlider  , p -> new Color((int) (p * 255), green, blue), 255));
		greenSlider.setUI(new ColorSliderUI(greenSlider, p -> new Color(red,   (int) (p * 255), blue), 255));
		blueSlider .setUI(new ColorSliderUI(blueSlider , p -> new Color(red, green,  (int) (p * 255)), 255));
		
		hueSlider.setMaximum(360);
		satSlider.setMaximum(100);
		valSlider.setMaximum(100);
		
		redSlider  .setMaximum(255);
		greenSlider.setMaximum(255);
		blueSlider .setMaximum(255);
		
		pack();
		setLocationRelativeTo(null);
		setMinimumSize((Dimension) getSize().clone());
		
		colorGrid.addChangeListenser(this);
		hexTextField.addChangeListenser(this);
		colorPanel.addChangeListenser(this);
		
		selectButton.addActionListener(this);
		cancleButton.addActionListener(this);
		resetButton .addActionListener(this);
		
		hueSlider.addChangeListener(this);
		satSlider.addChangeListener(this);
		valSlider.addChangeListener(this);
		
		redSlider  .addChangeListener(this);
		greenSlider.addChangeListener(this);
		blueSlider .addChangeListener(this);
		
		hueSpinner.addChangeListener(this);
		satSpinner.addChangeListener(this);
		valSpinner.addChangeListener(this);
		
		redSpinner  .addChangeListener(this);
		greenSpinner.addChangeListener(this);
		blueSpinner .addChangeListener(this);

		setColor(Color.RED);
	}
	
	private boolean updating;
	private void updateColor(Color color) {
		if(updating) return;
		updating = true;
		
		current = color;
		
		red = color.getRed();
		green = color.getGreen();
		blue = color.getBlue();
		
		float[] hsb = Color.RGBtoHSB(red, green, blue, null);
		
		hue = hsb[0];
		sat = hsb[1];
		val = hsb[2];
		
		newColorSample.setBackground(current);
		hexTextField.setText(Integer.toString(color.getRGB() & 0x00FFFFFF, 16).toUpperCase());
		colorPanel.setColor(color);
		
		hueSlider.setValue(Math.round(hue * 360));
		satSlider.setValue(Math.round(sat * 100));
		valSlider.setValue(Math.round(val * 100));
		
		hueSpinner.setValue(Math.round(hue * 360));
		satSpinner.setValue(Math.round(sat * 100));
		valSpinner.setValue(Math.round(val * 100));
		
		redSlider  .setValue(red);
		greenSlider.setValue(green);
		blueSlider .setValue(blue);
		
		redSpinner  .setValue(red);  
		greenSpinner.setValue(green);
		blueSpinner .setValue(blue); 
		
		hueSlider.repaint();
		satSlider.repaint();
		valSlider.repaint();
		
		redSlider  .repaint();
		greenSlider.repaint();
		blueSlider .repaint();
		
		colorPanel.repaint();
		
		updating = false;
	}
	
	public Color getColor() { return current; }
	
	public void setColor(Color color) {
		updateColor(color);
		
		this.start = color;
		oldColorSample.setBackground(color);
	}

	public void stateChanged(ChangeEvent e) {
		if(e.getSource() instanceof Color) {
			updateColor((Color) e.getSource());
			return;
		}
		
		if(e.getSource() == hexTextField) {
			updateColor(new Color(hexTextField.getIntValue()));
			return;
		}
		
		if(e.getSource() == hueSlider || e.getSource() == satSlider || e.getSource() == valSlider) {
			updateColor(Color.getHSBColor(hueSlider.getValue() / 360f, satSlider.getValue() / 100f, valSlider.getValue() / 100f));
			return;
		}
		
		if(e.getSource() == hueSpinner || e.getSource() == satSpinner || e.getSource() == valSpinner) {
			updateColor(Color.getHSBColor((int) hueSpinner.getValue() / 360f, (int) satSpinner.getValue() / 100f, (int) valSpinner.getValue() / 100f));
			return;
		}
		
		if(e.getSource() == redSlider || e.getSource() == greenSlider || e.getSource() == blueSlider) {
			updateColor(new Color(redSlider.getValue(), greenSlider.getValue(), blueSlider.getValue()));
			return;
		}
		
		if(e.getSource() == redSpinner || e.getSource() == greenSpinner || e.getSource() == blueSpinner) {
			updateColor(new Color((int) redSpinner.getValue(), (int) greenSpinner.getValue(), (int) blueSpinner.getValue()));
			return;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == resetButton) {
			updateColor(start); 
			return;
		}
		
		if(e.getSource() == cancleButton) {
			updateColor(start); 
			setVisible(false);
			return;
		}
		
		if(e.getSource() == selectButton) {
			colorGrid.push(current);
			setVisible(false);
			return;
		}
	}
}
