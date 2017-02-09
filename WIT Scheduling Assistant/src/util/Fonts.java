package util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

public class Fonts {
	public static final double LENGTH_SCALE;
	public static final double HEIGHT_SCALE;
	
	private static final Graphics CALC_GRAPHICS_CONTEXT = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB).createGraphics();
	
	static {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		LENGTH_SCALE = (double) gd.getDisplayMode().getWidth() / 1366.0;
		HEIGHT_SCALE = (double) gd.getDisplayMode().getHeight() / 768.0;
	}
	
	public static final Font TINY_LABEL = calc(new Font("Tahoma", Font.PLAIN, 12));
	
	public static final Font STANDARD_LABEL = calc(new Font("Tahoma", Font.PLAIN, 14));
	public static final Font MENU_BUTTON = calc(new Font("Tahoma", Font.BOLD, 14));
	
	public static final Font MEDIUM_LABEL = calc(new Font("Tahoma", Font.PLAIN, 16));
	public static final Font MEDIUM_LABEL_BOLD = calc(new Font("Tahoma", Font.BOLD, 16));
	
	public static final Font LARGE_LABEL = calc(new Font("Tahoma", Font.PLAIN, 18));
	public static final Font TITLE_MESSAGE = calc(new Font("Tahoma", Font.BOLD, 24));
	
	private static Font calc(Font normal) {
		FontMetrics metrics = CALC_GRAPHICS_CONTEXT.getFontMetrics(normal);
		double targetHeight = metrics.getHeight() * HEIGHT_SCALE;
		
		int high = 1000, low = 0;
		int fontSize = 0;
		
		while(low < high) {
			fontSize = (high + low) / 2;
			Font testFont = new Font(normal.getFamily(), normal.getStyle(), fontSize);
			FontMetrics testMetrics = CALC_GRAPHICS_CONTEXT.getFontMetrics(testFont);
			
			if(testMetrics.getHeight() > targetHeight) {
				high = fontSize - 1;
			} else if(testMetrics.getHeight() < targetHeight) {
				low = fontSize + 1;
			} else {
				return testFont;
			}
		}
		
		Font testFont = new Font(normal.getFamily(), normal.getStyle(), fontSize);
		FontMetrics testMetrics = CALC_GRAPHICS_CONTEXT.getFontMetrics(testFont);
		if(testMetrics.getHeight() > targetHeight) fontSize --;
		
		return new Font(normal.getFamily(), normal.getStyle(), fontSize);
	}
	
	public static void scale(Component component) {
		component.setPreferredSize(new Dimension((int) (component.getPreferredSize().getWidth() * LENGTH_SCALE), 
				(int) (component.getPreferredSize().getHeight() * HEIGHT_SCALE)));
		
		Font compFont = component.getFont();
		component.setFont(new Font(compFont.getName(), compFont.getStyle(), (int) (compFont.getSize() * LENGTH_SCALE)));
		
		if(component instanceof Container) {
			for(Component comp : ((Container) component).getComponents())
				scale(comp);
		}
	}
}
