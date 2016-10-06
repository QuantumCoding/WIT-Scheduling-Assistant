package web_interface;

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

import com.gargoylesoftware.htmlunit.WebClient;

import user_interface.Display;
import util.References;

public class WIT_Lancher {
	public static void main(String[] args) {
		BufferedImage loadedImage, scaledImage = new BufferedImage(400, 240, BufferedImage.TYPE_INT_ARGB);
		
		try { 
			loadedImage = ImageIO.read(WIT_Lancher.class.getResource("/web_interface/Splash.png"));
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
		
		new Thread(() -> {
			
			try(WebClient webClient = WebClientCreater.initWebClient()) {
				PageLock pages = new PageLock(webClient);
				Display display = new Display(pages);
				pages.setDisplay(display);
				
				splashScreen.setVisible(false);
				
				while(true) {
					pages.cycle();
					try { Thread.sleep(10); }
					catch(InterruptedException e) {}
				}
			}
		}, References.Thread_Name).start();
	}
}
