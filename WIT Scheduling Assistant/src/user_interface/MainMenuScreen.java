package user_interface;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import scheduling.Schedule;
import web_interface.PageLock;

public class MainMenuScreen extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1247067336925789430L;
	
	private JButton registerButton;
	private JButton viewButton;
	private JButton closeButton;
	private JLabel discriptionLabel;
	
	private Display display;
	private PageLock pages;

	public MainMenuScreen(PageLock pages, Display display) {
		this.display = display;
		this.pages = pages;
		
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[][grow][]", "[][grow][]"));
		
		JPanel midPanel = new JPanel();
		midPanel.setBackground(Color.WHITE);
		midPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		add(midPanel, "cell 1 1,grow");
		midPanel.setLayout(new MigLayout("", "[][grow][]", "[][20%][][grow]"));
		
		JLabel witHeaderLabel = new JLabel("");
		witHeaderLabel.setIcon(new ImageIcon("C:\\Users\\Joshua\\git\\WIT-Scheduling-Assistant\\WIT Scheduling Assistant\\res\\witHeader.gif"));
		midPanel.add(witHeaderLabel, "cell 0 0 3 1,alignx left,aligny top");
		
		registerButton = new JButton("Register For Classes");
		registerButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		registerButton.setBackground(Color.WHITE);
		midPanel.add(registerButton, "cell 0 1,alignx left,aligny bottom");
		
		viewButton = new JButton("View Schedule");
		viewButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		viewButton.setBackground(Color.WHITE);
		midPanel.add(viewButton, "cell 1 1,alignx left,aligny bottom");
		
		closeButton = new JButton("Close");
		closeButton.setBackground(Color.WHITE);
		closeButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
		midPanel.add(closeButton, "cell 2 1,alignx right,aligny bottom");
		
		JSeparator separator = new JSeparator();
		midPanel.add(separator, "cell 0 2 3 1,growx");
		
		discriptionLabel = new JLabel("Description:");
		discriptionLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		midPanel.add(discriptionLabel, "cell 0 3 3 1,alignx left,aligny top");

		registerButton.addActionListener(this);
		closeButton.addActionListener(this);
		viewButton.addActionListener(this);
		
		HashMap<JButton, String> descriptions = new HashMap<>();
		
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseEntered(MouseEvent e) { discriptionLabel.setText(descriptions.get(e.getSource())); }
			public void mouseExited(MouseEvent e) { discriptionLabel.setText(""); }
		};
		
		registerButton.addMouseListener(mouseListener);
		closeButton.addMouseListener(mouseListener);
		viewButton.addMouseListener(mouseListener);
		
		descriptions.put(closeButton, "<HTML>"
			+ "<h1>Close</h1>"
			+ "<p>"
				+ "\tLogout and then Closes the Application"
			+ "</p>"
			+ "</HTML>");
		
		descriptions.put(viewButton, "<HTML>"
			+ "<h1>View Schedule</h1>"
			+ "<p>"
				+ "Diplasys the User's Current Schedule graphicly"
			+ "<p>"
				+ "From this page, the User can save a Image of their Schedule"
			+ "</p>"
			+ "</HTML>");
		
		descriptions.put(registerButton, "<HTML>"
			+ "<h1>Register For Classes</h1>"
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
				ArrayList<Schedule> schedule = new ArrayList<>();
				schedule.add(pages.getSchedule());
				display.showSchedules(schedule);
			}).start();
			return;
		}
		
		if(e.getSource() == closeButton) {
			System.exit(0);
			return;
		}
	}
}
