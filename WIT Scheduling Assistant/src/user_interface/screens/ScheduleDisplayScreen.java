package user_interface.screens;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import pages.ViewSchedulePage;
import pages.ViewSchedulePage.ViewSchedule;
import scheduling.TreeSchedule;
import user_interface.Display;
import user_interface.image.ImageSettings;
import user_interface.image.ScheduleView;
import util.Fonts;
import util.References;

public class ScheduleDisplayScreen extends JPanel implements ActionListener {
	private static final long serialVersionUID = 2146555153971725092L;

	private ArrayList<TreeSchedule> schedules;
	private int selectedIndex;
	
	private JButton prevButton;
	private JButton nextButton;
	private JLabel currentScheduleLabel;

	private JButton imageSettingsButton;
	private JButton saveImageButton;
	private JButton menuButton;
	private JCheckBox overlayButton;

	private JButton registerButton;
	private ScheduleView scheduleDisplayScrollPane;
	
	private Display display;
	private boolean updating;
	private JCheckBox trimCheckBox;
	
	public ScheduleDisplayScreen(Display display, ArrayList<TreeSchedule> schedules) {
		this.display = display;
		this.schedules = schedules;
		
		setBackground(Color.WHITE);
		setLayout(new MigLayout("insets 1px", "[grow]", "[top][top][grow][]"));
		
		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);
		add(topPanel, "cell 0 0 1 2,grow");
		topPanel.setLayout(new MigLayout("", "[grow][][][20%,right]", "[grow][grow]"));
		
		trimCheckBox = new JCheckBox("<HTML><CENTER>Trim<BR>Schedule</CENTER></HTML>");
		trimCheckBox.setSelected(true);
		trimCheckBox.setFont(Fonts.STANDARD_LABEL);
		trimCheckBox.setBackground(Color.WHITE);
		topPanel.add(trimCheckBox, "cell 1 0,aligny center");
		
		overlayButton = new JCheckBox("<HTML><CENTER>Overlay<BR>Pref.</CENTER></HTML>");
		overlayButton.setFont(Fonts.STANDARD_LABEL);
		overlayButton.setBackground(Color.WHITE);
		topPanel.add(overlayButton, "cell 1 1,aligny center");
		
		menuButton = new JButton("<HTML><Center>Return<BR>To<BR>Menu</Center></HTML>");
		menuButton.setFont(Fonts.MENU_BUTTON);
		menuButton.setBackground(Color.WHITE);
		topPanel.add(menuButton, "cell 2 0 1 2,growy");
		
		JPanel scheduleControlPanel = new JPanel();
		scheduleControlPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		scheduleControlPanel.setBackground(Color.WHITE);
		topPanel.add(scheduleControlPanel, "flowx,cell 3 0 1 2,growx,aligny center");
		scheduleControlPanel.setLayout(new MigLayout("", "[grow][grow]", "[grow][][]"));
		
		JLabel witHeaderLabel = new JLabel("Possible Schedules");
		scheduleControlPanel.add(witHeaderLabel, "cell 0 0 2 1,alignx center,aligny top");
		witHeaderLabel.setFont(Fonts.MEDIUM_LABEL);
		
		currentScheduleLabel = new JLabel("[1 / 24]");
		currentScheduleLabel.setFont(Fonts.STANDARD_LABEL);
		scheduleControlPanel.add(currentScheduleLabel, "cell 0 1 2 1,alignx center,aligny top");
		
		prevButton = new JButton("Prev.");
		scheduleControlPanel.add(prevButton, "cell 0 2,growx,aligny bottom");
		prevButton.setBackground(Color.WHITE);
		prevButton.setFont(Fonts.STANDARD_LABEL);
		
		nextButton = new JButton("Next");
		scheduleControlPanel.add(nextButton, "cell 1 2,growx,aligny bottom");
		nextButton.setBackground(Color.WHITE);
		nextButton.setFont(Fonts.STANDARD_LABEL);
		
		JLabel label = new JLabel("");
		label.setIcon(References.Icon_WIT_Header);
		topPanel.add(label, "cell 0 0 1 2,alignx left,aligny center");
		
		scheduleDisplayScrollPane = new ScheduleView(this.schedules.get(0));
		add(scheduleDisplayScrollPane, "cell 0 2,grow");
		
		prevButton.addActionListener(this);
		nextButton.addActionListener(this);
		
		prevButton.setEnabled(false);
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		optionsPanel.setBackground(Color.WHITE);
		add(optionsPanel, "cell 0 3,grow");
		optionsPanel.setLayout(new MigLayout("", "[][grow][grow][50px]", "[]"));
		
		registerButton = new JButton("Register Schedule");
		registerButton.setBackground(Color.WHITE);
		registerButton.setFont(Fonts.STANDARD_LABEL);
		optionsPanel.add(registerButton, "cell 0 0,alignx left");
		registerButton.addActionListener(this);
		
		saveImageButton = new JButton("Save Image");
		saveImageButton.setBackground(Color.WHITE);
		saveImageButton.setFont(Fonts.STANDARD_LABEL);
		optionsPanel.add(saveImageButton, "cell 1 0,alignx right");
		
		imageSettingsButton = new JButton("Image Settings");
		optionsPanel.add(imageSettingsButton, "cell 3 0");
		imageSettingsButton.setFont(Fonts.STANDARD_LABEL);
		saveImageButton.addActionListener(this);
		
		menuButton.addActionListener(this);
		overlayButton.addActionListener(this);
		trimCheckBox.addActionListener(this);
		imageSettingsButton.addActionListener(this);
		
		updateLabel();
	}
	
	public void setSchedules(ArrayList<TreeSchedule> schedules) {
		selectedIndex = 0;
		this.schedules = schedules; 
		updateLabel();
	}
	
	private void updateLabel() {
		if(updating) return;
		
		updating = true;
		currentScheduleLabel.setText("[" + (selectedIndex + 1) + " / " + schedules.size() + "]");
		
		prevButton.setEnabled(selectedIndex > 0);
		nextButton.setEnabled(selectedIndex < schedules.size() - 1);
		
		scheduleDisplayScrollPane.changeSchedule(schedules.get(selectedIndex));
		actionPerformed(new ActionEvent(overlayButton, 0, ""));
		
		updating = false;
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == prevButton) {
			selectedIndex --;
			if(selectedIndex < 0)
				selectedIndex = 0;
			
			updateLabel();
			
			return;
		}
		
		if(e.getSource() == nextButton) {
			selectedIndex ++;
			if(selectedIndex >= schedules.size())
				selectedIndex = schedules.size() - 1;
			
			updateLabel();
			
			return;
		}
		
		if(e.getSource() == saveImageButton) {
			scheduleDisplayScrollPane.saveImage();
			return;
		}
		
		if(e.getSource() == menuButton) {
			display.switchToMainMenu();
			return;
		}
		
		if(e.getSource() == overlayButton) {
			if(overlayButton.isSelected()) 
				scheduleDisplayScrollPane.setShadingModel(display.getTimeSchadingModel());
			else
				scheduleDisplayScrollPane.setShadingModel(null);
			return;
		}
		
		if(e.getSource() == trimCheckBox) {
			scheduleDisplayScrollPane.shouldTrim(trimCheckBox.isSelected());
			return;
		}
		
		if(e.getSource() == registerButton) {
			JPanel diplayPanel = new JPanel();
			diplayPanel.setLayout(new BoxLayout(diplayPanel, BoxLayout.Y_AXIS));
			
			JLabel webLink = new JLabel("<HTML><U><Center>Link to Resistration Page</Center></U></HTML>");
			webLink.setFont(Fonts.MEDIUM_LABEL);
			webLink.setForeground(Color.BLUE);
			
			webLink.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					try {
						Desktop.getDesktop().browse(new URL("https://prodweb2.wit.edu/SSBPROD/leopard.zagreement.p_legal").toURI());
					} catch(IOException | URISyntaxException e1) {
						e1.printStackTrace();
					}
				}
			});
			
			diplayPanel.add(webLink);
			diplayPanel.add(Box.createVerticalStrut((int) (5 * Fonts.LENGTH_SCALE)));
			
			JPanel subDiplayPanel = new JPanel();
			subDiplayPanel.setLayout(new BoxLayout(subDiplayPanel, BoxLayout.X_AXIS));
			diplayPanel.add(subDiplayPanel);
			
			for(Integer classId : schedules.get(selectedIndex).getClassIds()) {
				JTextField classTextField = new JTextField(classId + " ");
				classTextField.setEditable(false);
				classTextField.setFont(Fonts.STANDARD_LABEL);
				subDiplayPanel.add(classTextField);
				subDiplayPanel.add(Box.createHorizontalStrut(10));
			}
			
			JOptionPane.showMessageDialog(this, diplayPanel, "Register", JOptionPane.INFORMATION_MESSAGE);
			
			return;
		}
		
		if(e.getSource() == imageSettingsButton) {
			LocalDate startDate = References.Selected_Date;
			ImageSettings settings = new ImageSettingScreen(scheduleDisplayScrollPane.getSettings()).getSettings();
			scheduleDisplayScrollPane.setSettings(settings);
		
			if(schedules.get(0) instanceof ViewSchedule && !startDate.equals(References.Selected_Date)) {
				display.showLoading("Collecting Schedule Information...");
				new Thread(() -> {
					ArrayList<TreeSchedule> schedule = new ArrayList<>();
					schedule.add(ViewSchedulePage.getSchedule());
					display.showSchedules(schedule);
				}).start();
			}
			
			return;
		}
	}
}
