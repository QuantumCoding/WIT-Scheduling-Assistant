package user_interface;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import util.Fonts;
import util.References;

public class LoadingScreen extends JPanel {
	private static final long serialVersionUID = 8834991877620709140L;

	private JLabel loginWitHeader;
	private JProgressBar loadingProgressBar;
	private JLabel loadingLabel;
	
	public LoadingScreen() {
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[][grow,center][]", "[][grow,center][]"));
		
		JPanel midPanel = new JPanel();
		midPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		midPanel.setBackground(Color.WHITE);
		midPanel.setLayout(new MigLayout("", "[grow]", "[grow][" + (25 * Fonts.HEIGHT_SCALE) + "px][][grow]"));
		
		loginWitHeader = new JLabel("");
		loginWitHeader.setIcon(References.Icon_WIT_Header);
		midPanel.add(loginWitHeader, "cell 0 0,alignx left,aligny top");
		
		loadingProgressBar = new JProgressBar();
		loadingProgressBar.setIndeterminate(true);
		midPanel.add(loadingProgressBar, "cell 0 1,grow");
		
		loadingLabel = new JLabel("");
		loadingLabel.setFont(Fonts.MEDIUM_LABEL);
		midPanel.add(loadingLabel, "cell 0 2,alignx center");

		add(midPanel, "cell 1 1,grow");
		
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	}
	
	public void setMessage(String message) { 
		loadingLabel.setText(message); 
		loadingProgressBar.setIndeterminate(true);
	}
	
	public void setMessage(String message, int value, int total) { 
		loadingLabel.setText(message); 
		
		loadingProgressBar.setIndeterminate(false);
		loadingProgressBar.setMaximum(total);
		loadingProgressBar.setValue(value);
		loadingProgressBar.setMinimum(0);
	}
}
