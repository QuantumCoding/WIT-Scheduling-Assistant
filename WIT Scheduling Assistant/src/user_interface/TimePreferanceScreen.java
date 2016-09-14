package user_interface;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import util.References;
import javax.swing.ImageIcon;

public class TimePreferanceScreen extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1333461675067105743L;
	
	private JButton menuButton;

	private ScheduleView scheduleDisplayScrollPane;
	
	private Display display;
	
	public TimePreferanceScreen(Display display) {
		this.display = display;
		
		setBackground(Color.WHITE);
		setLayout(new MigLayout("insets 1px", "[grow]", "[top][top][grow][]"));
		
		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);
		add(topPanel, "cell 0 0 1 2,grow");
		topPanel.setLayout(new MigLayout("", "[grow][][right]", "[grow]"));
		
		JPanel scheduleControlPanel = new JPanel();
		scheduleControlPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		scheduleControlPanel.setBackground(Color.WHITE);
		topPanel.add(scheduleControlPanel, "flowx,cell 1 0,growx,aligny center");
		scheduleControlPanel.setLayout(new MigLayout("", "[grow][grow]", "[grow][][]"));
		
		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon("C:\\Users\\Joshua\\git\\WIT-Scheduling-Assistant\\WIT Scheduling Assistant\\res\\witHeader.gif"));
//		label.setIcon(References.Icon_WIT_Header);
		topPanel.add(label, "cell 0 0,alignx left,aligny center");
		
		menuButton = new JButton("<HTML><Center>Return<BR>To<BR>Menu</Center></HTML>");
		menuButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		menuButton.setBackground(Color.WHITE);
		topPanel.add(menuButton, "cell 2 0,alignx center,growy");
		
		menuButton.addActionListener(this);
		
		scheduleDisplayScrollPane = new ScheduleView(null);
		add(scheduleDisplayScrollPane, "cell 0 2,grow");
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		optionsPanel.setBackground(Color.WHITE);
		add(optionsPanel, "cell 0 3,grow");
		optionsPanel.setLayout(new MigLayout("", "[][grow][grow][50px]", "[]"));
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == menuButton) {
			display.switchToMainMenu();
			return;
		}
	}
}
