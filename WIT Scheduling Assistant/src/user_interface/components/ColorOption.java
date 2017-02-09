package user_interface.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import util.Fonts;

public class ColorOption extends JPanel {
	private static final long serialVersionUID = 2976563988811928019L;
	private static final ColorPicker PICKER = new ColorPicker();
	
	private JLabel label;
	private JPanel color;
	
	public ColorOption(String text) {
		setLayout(new BorderLayout(0, 0));
		
		label = new JLabel(text);
		label.setFont(Fonts.TINY_LABEL);
		add(label, BorderLayout.WEST);
		
		JPanel colorPanelWrapper = new JPanel();
		colorPanelWrapper.setBackground(Color.WHITE);
		add(colorPanelWrapper, BorderLayout.EAST);
		colorPanelWrapper.setLayout(new MigLayout("", "[grow]", "[grow]"));
		
		color = new JPanel() {
			private static final long serialVersionUID = 3058022651742831571L;
			private Dimension dimension = new Dimension();
			
			public Dimension getPreferredSize() {
				dimension.height = dimension.width = (int) (label.getPreferredSize().height * 1.25);
			    return dimension;
		    }
		};
		colorPanelWrapper.add(color, "cell 0 0,alignx center,aligny center");
		
		color.setBackground(Color.RED);
		color.setBorder(new EtchedBorder(EtchedBorder.RAISED, Color.WHITE.brighter(), Color.WHITE.darker()));
		color.setLayout(new BorderLayout(0, 0));
		
		color.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				PICKER.setColor(color.getBackground());
				PICKER.setVisible(true);
				
				color.setBackground(PICKER.getColor());
			}
		});
	}

	public Color getColor() { return color.getBackground(); }
	public void setColor(Color color) { this.color.setBackground(color); }
	
	public String getText() { return label.getText(); }
}
