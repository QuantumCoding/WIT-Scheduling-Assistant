package user_interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import util.References;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

public class TimePreferanceScreen extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1333461675067105743L;
	
	private JButton menuButton;

	private TimePreferanceView timePreferanceView;
	
	private Display display;
	private JPanel samplePanel;
	private JSlider valueSlider;
	private JButton resetButton;
	
	public TimePreferanceScreen(Display display) {
		this.display = display;
		
		setBackground(Color.WHITE);
		setLayout(new MigLayout("insets 1px", "[grow]", "[top][top][grow][]"));
		
		JPanel topPanel = new JPanel();
		topPanel.setBackground(Color.WHITE);
		add(topPanel, "cell 0 0 1 2,grow");
		topPanel.setLayout(new MigLayout("", "[grow][grow][right]", "[grow]"));
		
		JPanel scheduleControlPanel = new JPanel();
		scheduleControlPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		scheduleControlPanel.setBackground(Color.WHITE);
		topPanel.add(scheduleControlPanel, "flowx,cell 1 0,alignx left,growy");
		scheduleControlPanel.setLayout(new MigLayout("", "[grow][][]", "[]"));
		
		JLabel instructionLabel = new JLabel("<HTML>\r\n\t<h2>Instructions</h2>\r\n\tUse this page to set a Preference of when your Classes are\r\n\t<BR>\r\n\tEx. If you Prefer Morning Classes, then make the Earlier hours Green\r\n</HTML>");
		instructionLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		scheduleControlPanel.add(instructionLabel, "cell 0 0");
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		scheduleControlPanel.add(separator, "cell 1 0,alignx center,growy");
		
		JLabel controlLabel = new JLabel("<HTML>\r\n<h2>Controls</h2>\r\n\tLeft Click and Drag to Change an area\r\n<BR>\r\n\tRight Click and Drag to Reset an area\r\n<BR>\r\n\tUse the Slider to select a Preference\r\n</HTML>");
		controlLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		scheduleControlPanel.add(controlLabel, "cell 2 0,alignx right,aligny top");
		
		JLabel label = new JLabel("");
//		label.setIcon(new ImageIcon("C:\\Users\\Joshua\\git\\WIT-Scheduling-Assistant\\WIT Scheduling Assistant\\res\\witHeader.gif"));
		label.setIcon(References.Icon_WIT_Header);
		topPanel.add(label, "cell 0 0,alignx left,aligny center");
		
		menuButton = new JButton("<HTML><Center>Return<BR>To<BR>Menu</Center></HTML>");
		menuButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		menuButton.setBackground(Color.WHITE);
		topPanel.add(menuButton, "cell 2 0,alignx center,growy");
		
		menuButton.addActionListener(this);
		
		timePreferanceView = new TimePreferanceView();
		add(timePreferanceView, "cell 0 2,grow");
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		optionsPanel.setBackground(Color.WHITE);
		add(optionsPanel, "cell 0 3,grow");
		optionsPanel.setLayout(new MigLayout("", "[][][][50px][grow]", "[]"));
		
		JLabel worstLabel = new JLabel("Worst");
		worstLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		optionsPanel.add(worstLabel, "cell 0 0,alignx right,aligny center");
		
		valueSlider = new JSlider();
		valueSlider.setValue(5);
		valueSlider.setMaximum(4);
		valueSlider.setSnapToTicks(true);
		valueSlider.setPaintTicks(true);
		valueSlider.setMajorTickSpacing(1);
		valueSlider.setBackground(Color.WHITE);
		optionsPanel.add(valueSlider, "cell 1 0,growx");
		
		JLabel bestLabel = new JLabel("Best");
		bestLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		optionsPanel.add(bestLabel, "cell 2 0,alignx left,aligny center");
		
		samplePanel = new JPanel();
		samplePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		samplePanel.setPreferredSize(new Dimension(25, 25));
		optionsPanel.add(samplePanel, "cell 3 0,alignx center,aligny center");
		
		resetButton = new JButton("Reset");
		resetButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		resetButton.setBackground(Color.WHITE);
		optionsPanel.add(resetButton, "cell 4 0,alignx right");
		resetButton.addActionListener(this);
		
		valueSlider.addChangeListener(this);
		stateChanged(new ChangeEvent(valueSlider));
		
		setPreferredSize(new Dimension(2, 2));
		timePreferanceView.load();
	}
	
	public float[][] getRankings() { return timePreferanceView.getRankings(); }
	public Color[][][] getShadingModel() { return timePreferanceView.getShadingModel(); }
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == menuButton) {
			display.switchToMainMenu();
			if(display.shouldSave())
				timePreferanceView.save();
			return;
		}
		
		if(e.getSource() == resetButton) {
			timePreferanceView.fullClear();
			return;
		}
	}

	public void stateChanged(ChangeEvent e) {
		samplePanel.setBackground(Color.getHSBColor(1/3f * (valueSlider.getValue() / (float) valueSlider.getMaximum()), 1, 1));
		timePreferanceView.setRank(samplePanel.getBackground());
	}
}
