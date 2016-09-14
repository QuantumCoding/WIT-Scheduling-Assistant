package user_interface;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;

import net.miginfocom.swing.MigLayout;
import scheduling.Schedule;
import util.References;

public class ScheduleDisplayScreen extends JPanel implements ActionListener {
	private static final long serialVersionUID = 2146555153971725092L;

	private ArrayList<Schedule> schedules;
	private int selectedIndex;
	
	private JButton prevButton;
	private JButton nextButton;
	private JLabel currentScheduleLabel;
	
	private JButton saveImageButton;
	private JButton menuButton;

	private JLabel variantLabel;
	private JComboBox<Integer> variantComboBox;
	private DefaultComboBoxModel<Integer> variantComboBoxModel;
	
	private JButton registerButton;
	private ScheduleView scheduleDisplayScrollPane;
	
	private Display display;
	private boolean updating;
	
	public ScheduleDisplayScreen(Display display, ArrayList<Schedule> schedules) {
		this.display = display;
		this.schedules = new ArrayList<>(schedules);
		Collections.shuffle(this.schedules);
		
		setBackground(Color.WHITE);
		setLayout(new MigLayout("insets 1px", "[grow]", "[top][top][grow][]"));
		
		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);
		add(topPanel, "cell 0 0 1 2,grow");
		topPanel.setLayout(new MigLayout("", "[grow][][20%,right]", "[grow]"));
		
		menuButton = new JButton("<HTML><Center>Return<BR>To<BR>Menu</Center></HTML>");
		menuButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		menuButton.setBackground(Color.WHITE);
		topPanel.add(menuButton, "cell 1 0,growy");
		
		JPanel scheduleControlPanel = new JPanel();
		scheduleControlPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		scheduleControlPanel.setBackground(Color.WHITE);
		topPanel.add(scheduleControlPanel, "flowx,cell 2 0,growx,aligny center");
		scheduleControlPanel.setLayout(new MigLayout("", "[grow][grow]", "[grow][][]"));
		
		JLabel witHeaderLabel = new JLabel("Possible Schedules");
		scheduleControlPanel.add(witHeaderLabel, "cell 0 0 2 1,alignx center,aligny top");
		witHeaderLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		
		currentScheduleLabel = new JLabel("[1 / 24]");
		currentScheduleLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		scheduleControlPanel.add(currentScheduleLabel, "cell 0 1 2 1,alignx center,aligny top");
		
		prevButton = new JButton("Prev.");
		scheduleControlPanel.add(prevButton, "cell 0 2,growx,aligny bottom");
		prevButton.setBackground(Color.WHITE);
		prevButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		nextButton = new JButton("Next");
		scheduleControlPanel.add(nextButton, "cell 1 2,growx,aligny bottom");
		nextButton.setBackground(Color.WHITE);
		nextButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel label = new JLabel("");
		label.setIcon(References.Icon_WIT_Header);
		topPanel.add(label, "cell 0 0,alignx left,aligny center");
		
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
		registerButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		optionsPanel.add(registerButton, "cell 0 0,alignx left");
		registerButton.addActionListener(this);
		
		saveImageButton = new JButton("Save Image");
		saveImageButton.setBackground(Color.WHITE);
		saveImageButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		optionsPanel.add(saveImageButton, "cell 1 0,alignx right");
		saveImageButton.addActionListener(this);
		
		variantLabel = new JLabel("Variant:");
		variantLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		optionsPanel.add(variantLabel, "cell 2 0,alignx right");
		
		variantComboBox = new JComboBox<>();
		variantComboBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
		variantComboBox.setModel(variantComboBoxModel = new DefaultComboBoxModel<>());
		optionsPanel.add(variantComboBox, "cell 3 0,growx");
		variantComboBox.addActionListener(this);
		
		menuButton.addActionListener(this);
		
		updateLabel();
	}
	
	public void setSchedules(ArrayList<Schedule> schedules) {
		this.schedules = schedules; updateLabel();
	}
	
	private void updateLabel() {
		if(updating) return;
		
		updating = true;
		currentScheduleLabel.setText("[" + (selectedIndex + 1) + " / " + schedules.size() + "]");
		
		prevButton.setEnabled(selectedIndex > 0);
		nextButton.setEnabled(selectedIndex < schedules.size() - 1);
		
		variantComboBoxModel.removeAllElements();
		for(int i = 0; i < schedules.get(selectedIndex).getVariantCount(); i ++)
			variantComboBoxModel.addElement(i);
		
		variantComboBox.setVisible(variantComboBoxModel.getSize() > 1);
		variantLabel.setVisible(variantComboBox.isVisible());
		
		scheduleDisplayScrollPane.changeSchedule(schedules.get(selectedIndex));
		
		updating = false;
	}
	
	private void changeVariant() {
		if(updating) return;
		scheduleDisplayScrollPane.changeVariant((Integer) variantComboBox.getSelectedItem()); 
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
		
		if(e.getSource() == variantComboBox) {
			changeVariant();
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
		
		if(e.getSource() == registerButton) {
			JPanel diplayPanel = new JPanel();
			diplayPanel.setLayout(new BoxLayout(diplayPanel, BoxLayout.Y_AXIS));
			
			JLabel webLink = new JLabel("<HTML><U><Center>Link to Resistration Page</Center></U></HTML>");
			webLink.setFont(new Font("Tahoma", Font.PLAIN, 16));
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
			diplayPanel.add(Box.createHorizontalStrut(5));
			
			JPanel subDiplayPanel = new JPanel();
			subDiplayPanel.setLayout(new BoxLayout(subDiplayPanel, BoxLayout.X_AXIS));
			diplayPanel.add(subDiplayPanel);
			
			for(Integer classId : schedules.get(selectedIndex).getClassIds((Integer) variantComboBox.getSelectedItem())) {
				JTextField classTextField = new JTextField(classId + " ");
				classTextField.setEditable(false);
				subDiplayPanel.add(classTextField);
				subDiplayPanel.add(Box.createHorizontalStrut(10));
			}
			
			JOptionPane.showMessageDialog(this, diplayPanel, "Register", JOptionPane.INFORMATION_MESSAGE);
			
			return;
		}
	}
}
