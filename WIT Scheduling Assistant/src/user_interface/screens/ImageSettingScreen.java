package user_interface.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import pages.ClassOption;
import pages.ViewSchedulePage;
import user_interface.components.ColorOption;
import user_interface.image.ImageSettings;
import util.References;

public class ImageSettingScreen extends JDialog implements ActionListener {
	private static final long serialVersionUID = -9154619536549460392L;

	private JButton cancleButton;
	private JButton saveButton;
	
	private JCheckBox use3dRect;
	private JCheckBox useFill;
	private JCheckBox useOutline;
	
	private ColorOption outlineColor;
	private ColorOption fillColor;
	
	private JPanel classColorPanel;
	private ImageSettings settings;
	private JSpinner selectedDateSpinner;

	public ImageSettingScreen(ImageSettings settings) {
		this.settings = settings;
		
		setTitle("Image Settings");
		setAlwaysOnTop(true);
		setModalityType(ModalityType.DOCUMENT_MODAL);
		
		getContentPane().setBackground(Color.WHITE);
		((JComponent) getContentPane()).setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		getContentPane().setLayout(new MigLayout("", "[grow]", "[][grow][grow][][]"));
		
		JPanel textPanel = new JPanel();
		textPanel.setBackground(Color.WHITE);
		textPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, null, null), "Text Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		getContentPane().add(textPanel, "cell 0 0,grow");
		textPanel.setLayout(new MigLayout("", "[grow][][]", "[grow][][grow]"));
		
		fillColor = new ColorOption("Fill Color");
		fillColor.setBackground(Color.WHITE);
		fillColor.setColor(settings.getTextColor());
		textPanel.add(fillColor, "cell 0 0,grow");
		
		JSeparator verticalSeporator = new JSeparator();
		verticalSeporator.setOrientation(SwingConstants.VERTICAL);
		textPanel.add(verticalSeporator, "cell 1 0 1 3,growy");
		
		useFill = new JCheckBox("Enabled");
		useFill.setSelected(settings.getTextColor() != null);
		useFill.setFocusable(false);
		useFill.setBackground(Color.WHITE);
		textPanel.add(useFill, "cell 2 0");
		
		JSeparator separator = new JSeparator();
		textPanel.add(separator, "cell 0 1 3 1,growx");
		
		outlineColor = new ColorOption("Outline Color");
		outlineColor.setBackground(Color.WHITE);
		outlineColor.setColor(settings.getOutlineColor());
		textPanel.add(outlineColor, "cell 0 2,grow");
		
		useOutline = new JCheckBox("Enabled");
		useOutline.setSelected(settings.getOutlineColor() != null);
		useOutline.setFocusable(false);
		useOutline.setBackground(Color.WHITE);
		textPanel.add(useOutline, "cell 2 2");
		
		setSize(new Dimension(436, 442));
		
		JPanel sectionsPanel = new JPanel();
		sectionsPanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, null, null), "Class Settings", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		sectionsPanel.setBackground(Color.WHITE);
		getContentPane().add(sectionsPanel, "cell 0 1,grow");
		sectionsPanel.setLayout(new BorderLayout(0, 0));
		
		use3dRect = new JCheckBox("Use Beveled Rectangles");
		use3dRect.setFocusable(false);
		use3dRect.setSelected(settings.using3dRect());
		use3dRect.setBackground(Color.WHITE);
		sectionsPanel.add(use3dRect, BorderLayout.NORTH);
		
		JPanel sectionsWrapperPanel = new JPanel();
		sectionsWrapperPanel.setBackground(Color.WHITE);
		sectionsWrapperPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		sectionsPanel.add(sectionsWrapperPanel, BorderLayout.CENTER);
		sectionsWrapperPanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane sectionScrollPane = new JScrollPane();
		sectionsWrapperPanel.add(sectionScrollPane);
		
		classColorPanel = new JPanel();
		classColorPanel.setBackground(Color.WHITE);
		sectionScrollPane.setViewportView(classColorPanel);
		
		JPanel datePanel = new JPanel();
		datePanel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, null, null), "Displayed Week", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		datePanel.setBackground(Color.WHITE);
		getContentPane().add(datePanel, "cell 0 2,grow");
		datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));
		
		selectedDateSpinner = new JSpinner();
		selectedDateSpinner.setModel(new SpinnerDateModel(
				Date.from(References.Selected_Date.atStartOfDay(ZoneId.systemDefault()).toInstant()), 
				null, null, Calendar.DAY_OF_YEAR));
		selectedDateSpinner.setEditor(new JSpinner.DateEditor(selectedDateSpinner, ViewSchedulePage.FORMATTER_PATTERN));
		selectedDateSpinner.setBackground(SystemColor.menu);
		datePanel.add(selectedDateSpinner);
		
		JSeparator buttonSeparator = new JSeparator();
		getContentPane().add(buttonSeparator, "cell 0 3,growx");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		getContentPane().add(buttonPanel, "cell 0 4,grow");
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
		Component horizontalGlue = Box.createHorizontalGlue();
		buttonPanel.add(horizontalGlue);
		
		saveButton = new JButton("Save");
		saveButton.setBackground(Color.WHITE);
		buttonPanel.add(saveButton);
		
		cancleButton = new JButton("Cancle");
		cancleButton.setBackground(Color.WHITE);
		buttonPanel.add(cancleButton);
		
		classColorPanel.setLayout(new BoxLayout(classColorPanel, BoxLayout.Y_AXIS));
		for(ClassOption clazz: settings.getClassColors().keySet()) {
			ColorOption option = new ColorOption(clazz.getName());
			option.setColor(settings.getClassColors().get(clazz));
			option.setBackground(Color.WHITE);
			classColorPanel.add(option);
		}
		
		saveButton.addActionListener(this);
		cancleButton.addActionListener(this);
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public ImageSettings getSettings() { return settings; }
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == saveButton) {
			HashMap<ClassOption, Color> classColors = new HashMap<>();
			ImageSettings newSettings = new ImageSettings(classColors);
			
			for(ClassOption clazz : settings.getClassColors().keySet()) {
				Color color = null;
				for(Component component : classColorPanel.getComponents()) {
					if(!(component instanceof ColorOption)) continue;
					
					ColorOption option = (ColorOption) component;
					if(!option.getText().equals(clazz.getName())) continue;
					
					color = option.getColor();
					break;
				}
				
				classColors.put(clazz, color);
			}
			
			newSettings.setTextColor(useFill.isSelected() ? fillColor.getColor() : null);
			newSettings.setOutlineColor(useOutline.isSelected() ? outlineColor.getColor() : null);
			
			newSettings.setUse3dRect(use3dRect.isSelected());
			
			References.Selected_Date = ((Date) selectedDateSpinner.getValue()).toInstant()
					.atZone(ZoneId.systemDefault()).toLocalDate();
			
			settings = newSettings;
			setVisible(false);
			return;
		}
		
		if(e.getSource() == cancleButton) {
			setVisible(false);
			return;
		}
	}
}
