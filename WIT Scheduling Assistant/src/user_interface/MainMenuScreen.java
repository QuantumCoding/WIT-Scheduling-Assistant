package user_interface;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import pages.ViewSchedulePage;
import scheduling.TreeSchedule;
import util.Fonts;
import util.References;

public class MainMenuScreen extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1247067336925789430L;
	
	private JButton registerButton;
	private JButton timePreferneceButton;
	private JButton viewButton;
	private JButton closeButton;
	private JLabel discriptionLabel;
	
	private Display display;

	public MainMenuScreen(Display display) {
		this.display = display;
		
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[][grow][]", "[][grow][]"));
		
		JPanel midPanel = new JPanel();
		midPanel.setBackground(Color.WHITE);
		midPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		add(midPanel, "cell 1 1,grow");
		midPanel.setLayout(new MigLayout("", "[][][grow][]", "[][20%][][][grow]"));
		
		JLabel witHeaderLabel = new JLabel("");
		witHeaderLabel.setIcon(References.Icon_WIT_Header);
		midPanel.add(witHeaderLabel, "cell 0 0 4 1,alignx left,aligny top");
		
		registerButton = new JButton("Register For Classes");
		registerButton.setFont(Fonts.MEDIUM_LABEL);
		registerButton.setBackground(Color.WHITE);
		midPanel.add(registerButton, "cell 0 1,alignx left,aligny bottom");
		
		timePreferneceButton = new JButton("Time Preferences");
		timePreferneceButton.setBackground(Color.WHITE);
		timePreferneceButton.setFont(Fonts.MEDIUM_LABEL);
		midPanel.add(timePreferneceButton, "cell 1 1,alignx left,aligny bottom");
		
		viewButton = new JButton("View Schedule");
		viewButton.setFont(Fonts.MEDIUM_LABEL);
		viewButton.setBackground(Color.WHITE);
		midPanel.add(viewButton, "cell 0 2 2 1,growx,aligny bottom");
		
		closeButton = new JButton("Close");
		closeButton.setBackground(Color.WHITE);
		closeButton.setFont(Fonts.MEDIUM_LABEL);
		midPanel.add(closeButton, "cell 3 2,alignx right,aligny bottom");
		
		JSeparator separator = new JSeparator();
		midPanel.add(separator, "cell 0 3 4 1,growx");
		
		discriptionLabel = new JLabel("Description:");
		discriptionLabel.setFont(Fonts.MEDIUM_LABEL);
		midPanel.add(discriptionLabel, "cell 0 4 4 1,alignx left,aligny top");

		postInit();
	}
	
	private void postInit() {
		timePreferneceButton.addActionListener(this);
		registerButton.addActionListener(this);
		closeButton.addActionListener(this);
		viewButton.addActionListener(this);
		
		HashMap<JButton, String> descriptions = new HashMap<>();
		
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseEntered(MouseEvent e) { discriptionLabel.setText(descriptions.get(e.getSource())); }
			public void mouseExited(MouseEvent e) { discriptionLabel.setText(""); }
		};
		
		timePreferneceButton.addMouseListener(mouseListener);
		registerButton.addMouseListener(mouseListener);
		closeButton.addMouseListener(mouseListener);
		viewButton.addMouseListener(mouseListener);
		
		descriptions.put(closeButton, "<HTML>"
			+ "<B><font size=++>Close </font></B>"
			+ "<p>"
				+ "\tLogout and then Closes the Application"
			+ "</p>"
			+ "</HTML>");
		
		descriptions.put(viewButton, "<HTML>"
			+ "<B><font size=++>View Schedule </font></B>"
			+ "<p>"
				+ "Diplasys the User's Current Schedule graphicly"
			+ "<p>"
				+ "From this page, the User can save a Image of their Schedule"
			+ "</p>"
			+ "</HTML>");
		
		descriptions.put(timePreferneceButton, "<HTML>"
			+ "<B><font size=++>Configure Time Preferences </font></B>"
			+ "<p>"
				+ "Allows the User to specify which Periods of Time they Prefer to have Classes"
			+ "<p>"
				+ "The programe will use this Information to present the Best posible Schedules"
			+ "<BR><BR><B><font size=+>"
				+ "To use this Tool:"
			+ " </font></B><BR>"
				+ "Move the Preference Slider to the Correct Color"
			+ "<p>"
				+ "Then Select Areas of the Scedule to fill, by <B>LEFT</B> Clicking and Dragging"
			+ "<p>"
				+ "To Clear an Area <B>RIGHT</B> Click and then Drag over the Undesirable  Area"
			+ "<p><BR>"
				+ "Changes are Automatically Recorded"
			+ "</p>"
			+ "</HTML>");
		
		descriptions.put(registerButton, "<HTML>"
			+ "<B><font size=++>Register For Classes </font></B>"
			+ "<p>"
				+ "Allows the User to select classes from database"
			+ "<p>"
				+ "The programe will then calculate all posible Schedules for the selected classes"
			+ "<p>"
				+ "The Schedules will be weighted then Ranked based on diffent factors"
			+ "<p><BR>"
				+ "User will then be presented with all of the Schedules"
			+ "<p>"
				+ "From here the User can register for the Scedule that they like best"
			+ "</p>"
			+ "</HTML>");
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == registerButton) {
			display.switchToClassAdder();
			return;
		}
		
		if(e.getSource() == viewButton) {
			display.showLoading("Collecting Schedule Information...");
			
			new Thread(() -> {
				ArrayList<TreeSchedule> schedule = new ArrayList<>();
				schedule.add(ViewSchedulePage.getSchedule());
				display.showSchedules(schedule);
			}).start();
			return;
		}
		
		if(e.getSource() == timePreferneceButton) {
			display.switchToTimePreferences();
			return;
		}
		
		if(e.getSource() == closeButton) {
			System.exit(0);
			return;
		}
	}
}
