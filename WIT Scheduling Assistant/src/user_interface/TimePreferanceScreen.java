package user_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

import net.miginfocom.swing.MigLayout;
import util.Fonts;
import util.References;

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
		topPanel.setLayout(new MigLayout("", "[][grow][][grow][right]", "[grow]"));
		
		JPanel controlPaddingPanel = new JPanel();
		controlPaddingPanel.setBackground(Color.WHITE);
		topPanel.add(controlPaddingPanel, "cell 1 0,grow");
		controlPaddingPanel.setLayout(new MigLayout("", "[grow][grow][grow]", "[]"));
		
		JPanel scheduleControlPanel = new JPanel();
		controlPaddingPanel.add(scheduleControlPanel, "cell 1 0,grow");
		scheduleControlPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		scheduleControlPanel.setBackground(Color.WHITE);
		scheduleControlPanel.setLayout(new MigLayout("inset 0 0 0 0 ", "[][grow][][][grow]", "[]"));
		
		JLabel instructionLabel = new JLabel("<HTML><B><font size=+>Instructions</font></B><BR>Indicate when you prefer to have class<BR><BR>Ex. If you Prefer Morning Classes,<BR>&emsp;then make the Earlier hours Green</HTML>");
		instructionLabel.setFont(Fonts.STANDARD_LABEL);
		scheduleControlPanel.add(instructionLabel, "cell 1 0,alignx left,aligny top");
		
		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		scheduleControlPanel.add(separator, "cell 3 0,alignx center,growy");
		
		JLabel controlLabel = new JLabel("<HTML><B><font size=+>Controls</font></B><BR><BR>Left Click and Drag to Change an area<BR>Right Click and Drag to Reset an area<BR>Use the Slider to select a Preference</HTML>");
		controlLabel.setFont(Fonts.STANDARD_LABEL);
		scheduleControlPanel.add(controlLabel, "cell 4 0,alignx left,aligny top");
		
		JLabel label = new JLabel("");
		label.setIcon(References.Icon_WIT_Header);
		topPanel.add(label, "cell 0 0,alignx left,aligny center");
		
		menuButton = new JButton("<HTML><Center>Return<BR>To<BR>Menu</Center></HTML>");
		menuButton.setFont(Fonts.STANDARD_LABEL);
		menuButton.setBackground(Color.WHITE);
		topPanel.add(menuButton, "cell 4 0,alignx center,growy");
		
		menuButton.addActionListener(this);
		
		timePreferanceView = new TimePreferanceView();
		add(timePreferanceView, "cell 0 2,grow");
		
		JPanel optionsPanel = new JPanel();
		optionsPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		optionsPanel.setBackground(Color.WHITE);
		add(optionsPanel, "cell 0 3,grow");
		optionsPanel.setLayout(new MigLayout("", "[][][][50px][grow]", "[]"));
		
		JLabel worstLabel = new JLabel("Worst ");
		worstLabel.setFont(Fonts.MEDIUM_LABEL);
		optionsPanel.add(worstLabel, "cell 0 0,alignx right,aligny center");
		
		valueSlider = new JSlider();
		valueSlider.setValue(5);
		valueSlider.setMaximum(5);
		valueSlider.setMinimum(1);
//		valueSlider.setSnapToTicks(true);
//		valueSlider.setPaintTicks(true);
		valueSlider.setMajorTickSpacing(1);
		valueSlider.setBackground(Color.WHITE);
		Dimension preferred = valueSlider.getPreferredSize();
		valueSlider.setPreferredSize(new Dimension(
				(int) (preferred.width * Fonts.LENGTH_SCALE), 
				(int) (preferred.height * Fonts.LENGTH_SCALE))
			);
		optionsPanel.add(valueSlider, "cell 1 0,growx");
		valueSlider.setUI(new BasicSliderUI(valueSlider) {
			private double renderProgress = 0;
			private Handler handler;
			
			protected void calculateThumbSize() {
				thumbRect.setSize(slider.getHeight(), slider.getHeight());
			}
			
			protected void calculateTrackRect() {
				double shift = 1.0 / valueSlider.getMaximum();
				int strokWidth = 3;

				int width = slider.getWidth();
				int height = slider.getHeight();
				
				trackRect.setRect((width - strokWidth / 2) * shift * 1 - height / 4, strokWidth / 2, 
						(width - strokWidth / 2) * shift * (valueSlider.getMaximum() - 1) - height / 4, height - strokWidth);
			}
			
			public void paint(Graphics g, JComponent c) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(c.getBackground());
				g2d.fillRect(0, 0, c.getWidth(), c.getHeight());
				
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					
				int strokWidth = 3;
				g2d.setStroke(new BasicStroke(strokWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g2d.setBackground(c.getBackground().darker().darker());
					
				int width = c.getWidth();
				int height = c.getHeight();
				
				renderProgress =  (thumbRect.x + height) / (double) width;
					
				int innerWidth = (int) Math.round(width * renderProgress);

				int x = (strokWidth / 2);
				int y = (strokWidth / 2);
					
				Point2D start = new Point2D.Double(x, y);
				Point2D end = new Point2D.Double(x, y + (height - strokWidth));
				
				Color base = getColor((float) (valueSlider.getMaximum() * renderProgress)); //new Color(255, 140, 0);
				float[] dist = { 0.0f, 0.25f, 1.0f };
				Color[] colors = { base, base.brighter(), base.darker() };
				LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);
				g2d.setPaint(p);
				
				RoundRectangle2D fill = new RoundRectangle2D.Double((strokWidth / 2), (strokWidth / 2),
						innerWidth - (height / 4), height - strokWidth, height / 2, height);
				
				RoundRectangle2D outline = new RoundRectangle2D.Double((strokWidth / 2), (strokWidth / 2),
						width - strokWidth, height - strokWidth, height, height);

				g2d.setClip(outline);
				g2d.fill(fill);
				g2d.setClip(null);
				
				g2d.setPaint(null);
				g2d.setColor(c.getBackground().darker().darker());
				g2d.draw(outline);
				
				double shift = 1.0 / valueSlider.getMaximum();
				Arc2D arc = new Arc2D.Double();
				
				for(int i = 1; i < valueSlider.getMaximum(); i ++) {
					arc.setArc(width * shift * i - height / 2, strokWidth / 2, height / 2, height - strokWidth, -90, 180, Arc2D.OPEN);
					g2d.draw(arc);
				}

				Point2D center = new Point2D.Double(innerWidth - height / 3, (strokWidth / 2) + height / 3);
				RoundRectangle2D nob = new RoundRectangle2D.Double(innerWidth - (height - strokWidth), (strokWidth / 2),
						height, height - strokWidth, height, height);
				
				float[] nobDist = { 0.0f, 0.25f, 1.0f };
				Color[] nobColors = { new Color(150, 150, 150), new Color(100, 100, 100), new Color(50, 50, 50) };
				RadialGradientPaint nobPaint = new RadialGradientPaint(center, height, nobDist, nobColors);
				g2d.setPaint(nobPaint);

				g2d.setClip(outline);
				g2d.fill(nob);
				g2d.dispose();
			}
			
			protected void installDefaults(JSlider slider) {
				super.installDefaults(slider);
				slider.setOpaque(false);
				slider.setBorder(null);
			}
			
			protected void installListeners(JSlider slider) {
				super.installListeners(slider);
				slider.addMouseMotionListener(getHandler());
			}
				
			protected Handler getHandler() {
				if(handler == null)
					handler = new Handler();
				return handler;
			}
				
			class Handler extends MouseMotionAdapter {
				public void mouseDragged(MouseEvent e) {
					slider.repaint();
				}
			}
	    });
		
		
		JLabel bestLabel = new JLabel(" Best");
		bestLabel.setFont(Fonts.MEDIUM_LABEL);
		optionsPanel.add(bestLabel, "cell 2 0,alignx left,aligny center");
		
		samplePanel = new JPanel();
		samplePanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		samplePanel.setPreferredSize(new Dimension(25, 25));
		optionsPanel.add(samplePanel, "cell 3 0,alignx center,aligny center");
		
		resetButton = new JButton("Reset");
		resetButton.setFont(Fonts.STANDARD_LABEL);
		resetButton.setBackground(Color.WHITE);
		optionsPanel.add(resetButton, "cell 4 0,alignx right");
		resetButton.addActionListener(this);
		
		valueSlider.addChangeListener(this);
		stateChanged(new ChangeEvent(valueSlider));
		
		setPreferredSize(new Dimension(706, 433));
		timePreferanceView.load();
		
		samplePanel.setVisible(false);
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
			int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to Clear your Time Preferances?", 
					"Comfirm Clear", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			
			if(response == JOptionPane.YES_OPTION)
				timePreferanceView.fullClear();
			return;
		}
	}

	public Color getColor(float value) {
		return Color.getHSBColor(1/3f * ((value - valueSlider.getMinimum()) / 
				(valueSlider.getMaximum() - valueSlider.getMinimum())), 1, 1);
	}
	
	public void stateChanged(ChangeEvent e) {
		samplePanel.setBackground(getColor(valueSlider.getValue()));
		timePreferanceView.setRank(samplePanel.getBackground());
	}
}
