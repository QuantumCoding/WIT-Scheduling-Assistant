package launcher;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import javafx.embed.swing.JFXPanel;
import user_interface.Display;
import util.Fonts;

public class WIT_Lancher {
	public static void main(String[] args) {
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
		splashScreen.add(new JLabel(new ImageIcon(scaledImage)));
		splashScreen.setUndecorated(true);
		splashScreen.pack();
		splashScreen.setLocationRelativeTo(null);
		splashScreen.setVisible(true);

		JFXPanel platformStartPanel = new JFXPanel();
		splashScreen.add(platformStartPanel);
		splashScreen.remove(platformStartPanel);
		
		new Display();
		splashScreen.setVisible(false);
	}
}
