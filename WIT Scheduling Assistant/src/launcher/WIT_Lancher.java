package launcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import javafx.embed.swing.JFXPanel;
import updater.UpdateChecker;
import user_interface.Display;
import util.Fonts;

public class WIT_Lancher {
	public static final String VERSION = "1.2.7";
	
	public static void main(String[] args) {
		String VERSION_STR = VERSION + " ";
		
		BufferedImage loadedImage, scaledImage = new BufferedImage(
				(int) (400 * Fonts.LENGTH_SCALE), (int) (240 * Fonts.HEIGHT_SCALE), BufferedImage.TYPE_INT_ARGB);
		
		try { 
			loadedImage = ImageIO.read(WIT_Lancher.class.getResource("/launcher/Splash.png"));
			Graphics g = scaledImage.getGraphics();
			g.drawImage(loadedImage, 0, 0, scaledImage.getWidth(), scaledImage.getHeight(), null);
			g.dispose();
		} catch(IOException e) {}
		
		
		final JDialog splashScreen = new JDialog();
		((JPanel) splashScreen.getContentPane()).setBorder(new LineBorder(Color.BLACK, 1));	
		splashScreen.setSize(scaledImage.getWidth() + 2, scaledImage.getHeight() + 2);
		splashScreen.setLayout(null);
		
		JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
		imageLabel.setLocation(1, 1);
		imageLabel.setSize(scaledImage.getWidth(), scaledImage.getHeight());
		
		JLabel versionLabel = new JLabel(VERSION_STR);
		versionLabel.setFont(Fonts.STANDARD_LABEL);
		Rectangle2D bounds = Fonts.STANDARD_LABEL.getStringBounds(VERSION_STR, new FontRenderContext(null, false, true));
		versionLabel.setSize((int) bounds.getWidth() * 100, (int) bounds.getHeight());
		versionLabel.setLocation(scaledImage.getWidth() - (int) bounds.getWidth(), 
				scaledImage.getHeight() - (int) bounds.getHeight());
		
		splashScreen.add(versionLabel);
		splashScreen.add(imageLabel);	

		splashScreen.setUndecorated(true);
		splashScreen.setLocationRelativeTo(null);
		splashScreen.setVisible(true);

		JFXPanel platformStartPanel = new JFXPanel();
		splashScreen.add(platformStartPanel);
		splashScreen.remove(platformStartPanel);
		
		
		Display d =	new Display(); 
		d.setVisible(false);
		
		splashScreen.setVisible(false);
		
		UpdateChecker.checkAndPrompt();
		
		d.setVisible(true);
	}
}
