package user_interface.screens;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import user_interface.Display;
import util.Fonts;
import util.References;

public class ErrorScreen extends JPanel {
	private static final long serialVersionUID = 1453211385084357769L;
	
	public ErrorScreen(Display display, String message) {
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[][grow][]", "[][grow][]"));
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		panel.setBackground(Color.WHITE);
		add(panel, "cell 1 1,grow");
		panel.setLayout(new MigLayout("", "[grow]", "[][30%][grow][][]"));
		
		JLabel witHeaderLabel = new JLabel("");
		panel.add(witHeaderLabel, "flowy,cell 0 0,alignx left,aligny top");
		witHeaderLabel.setIcon(References.Icon_WIT_Header);
		
		JLabel errorTitleLabel = new JLabel("An Error has Occurred!");
		panel.add(errorTitleLabel, "cell 0 1,alignx center,aligny bottom");
		errorTitleLabel.setForeground(new Color(204, 0, 0));
		errorTitleLabel.setFont(Fonts.TITLE_MESSAGE);
		
		JLabel errrorMessageLabel = new JLabel(message);
		panel.add(errrorMessageLabel, "cell 0 2,alignx center,aligny top");
		errrorMessageLabel.setForeground(new Color(153, 0, 0));
		errrorMessageLabel.setFont(Fonts.LARGE_LABEL);
		
		JSeparator separator = new JSeparator();
		panel.add(separator, "cell 0 3,growx");
		
		JButton returnButton = new JButton("Return to Class Selection Page");
		panel.add(returnButton, "cell 0 4,alignx center,aligny bottom");
		returnButton.setBackground(Color.WHITE);
		returnButton.setFont(Fonts.STANDARD_LABEL);
		
		returnButton.addActionListener(e -> {
			display.switchToClassAdder();
		});
	}
}
