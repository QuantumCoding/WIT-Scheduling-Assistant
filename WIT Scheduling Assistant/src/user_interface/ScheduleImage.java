package user_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import scheduling.ClassConfig;
import scheduling.Designation;
import scheduling.Location;
import scheduling.Period.WeekDay;
import scheduling.Schedule;
import scheduling.Section;

public class ScheduleImage extends BufferedImage {
	private static JFileChooser fileChooser;
	
	protected static final int RESOLUTION_X = 1024;
	protected static final int RESOLUTION_Y = 2048;

	public static final int RESOLUTION_XP = RESOLUTION_X + 3;
	public static final int RESOLUTION_YP = RESOLUTION_Y + 3;
	
	public static final int TIME_RESOLUTION = 10;
	
	public static final int MIN_HOUR = 8, MAX_HOUR = 22;
	public static final int TIME_DURATION_HOURS = MAX_HOUR - MIN_HOUR;
	
	private static final Stroke BACKGOUND_WEEK_STROKE = new BasicStroke(3);
	private static final Color BACKGOUND_WEEK_COLOR = new Color(125, 125, 125);
	
	private static final Stroke BACKGOUND_TIME_MINOR_STROKE = new BasicStroke(1);
	private static final Color BACKGOUND_TIME_MINOR_COLOR = new Color(225, 225, 225);
	private static final Stroke BACKGOUND_TIME_MAJOR_STROKE = new BasicStroke(3);
	private static final Color BACKGOUND_TIME_MAJOR_COLOR = new Color(125, 125, 125);
	
	public static final int HEADER_HEIGHT = 32;
	public static final int LABELS_LENGTH = 72;
	
	private static final Color HEADER_BG_COLOR = new Color(150, 150, 150);
	private static final Color HEADER_FG_COLOR = new Color(0, 0, 0);
	private static final Font HEADER_FONT = new Font("Tahoma", Font.BOLD, 18);

	private static final Color LABEL_BG_COLOR = new Color(240, 240, 240);
	
	private static final Color LABEL_MINOR_FG_COLOR = new Color(0, 0, 0);
	private static final Font LABEL_MINOR_FONT = new Font("Tahoma", Font.PLAIN, 10);
	
	private static final Color LABEL_MAJOR_FG_COLOR = new Color(0, 0, 0);
	private static final Font LABEL_MAJOR_FONT = new Font("Tahoma", Font.BOLD, 14);
	
	public static BufferedImage weekHeader, timeLabels;
	static {
		fileChooser = new JFileChooser();
		
		createWeekHeader();
		createTimeLabels();
	}
	
	private static void createWeekHeader() {
		weekHeader = new BufferedImage(RESOLUTION_X + 3, HEADER_HEIGHT, TYPE_INT_ARGB);
		Graphics2D g = weekHeader.createGraphics();
		
		g.setColor(HEADER_BG_COLOR);
		g.fillRect(0, 0, RESOLUTION_X + 3, HEADER_HEIGHT);
		
		drawDayLines(g);
		
		g.setFont(HEADER_FONT); g.setColor(HEADER_FG_COLOR);
		for(int i = 0, size = WeekDay.values().length; i < size; i ++) {
			String dayName = WeekDay.values()[i].toString();
			
			int x = (int) (i * (RESOLUTION_X / (float) size)) - 3;

			Rectangle2D textBounds = g.getFontMetrics().getStringBounds(dayName, g);
			int y = HEADER_HEIGHT - (int) ((HEADER_HEIGHT - textBounds.getHeight()) / 2.0f - textBounds.getCenterY());
			x += ((RESOLUTION_X / (float) size) - textBounds.getWidth()) / 2.0f;
			g.drawString(dayName, x, y);
		}
		
		g.dispose();
	}
	
	private static void createTimeLabels() {
		timeLabels = new BufferedImage(LABELS_LENGTH, RESOLUTION_Y + 3, TYPE_INT_ARGB);
		Graphics2D g = timeLabels.createGraphics();
		
		g.setColor(LABEL_BG_COLOR);
		g.fillRect(0, 0, LABELS_LENGTH, RESOLUTION_Y + 3);
		
		drawTimeLines(g, false);
		
		long conversion = 1000 * 60;
		for(int i = 0, size = TIME_DURATION_HOURS * 60 / TIME_RESOLUTION; i < size + 1; i ++) {
			int y = (int) (i * (RESOLUTION_Y / (float) size));
			
			if(i % (60 / TIME_RESOLUTION) == 0) {
				g.setFont(LABEL_MAJOR_FONT); 
				g.setColor(LABEL_MAJOR_FG_COLOR);
				
			} else if(i % 1 == 0) {
				g.setFont(LABEL_MINOR_FONT); 
				g.setColor(LABEL_MINOR_FG_COLOR);
				
			} else continue;
			
			String time = String.format("%1$Tl:%1$TM %1$Tp", (long)((MIN_HOUR + 5)*60*conversion + TIME_RESOLUTION*i*conversion));
			Rectangle2D textBounds = g.getFontMetrics().getStringBounds(time, g);
			int x = (int) (LABELS_LENGTH - textBounds.getWidth() - 1);
			y -= textBounds.getCenterY();
			
			g.drawString(time, x, y);
		}
		
		g.dispose();
	}
	
//	------------------------------ End of Static ------------------------------ \\
	
	private Schedule schedule;
	private Color[][][] shadingModel;
	
	public ScheduleImage(Schedule schedule, int variant) {
		super(RESOLUTION_X + 3, RESOLUTION_Y + 3, TYPE_INT_ARGB);
		this.schedule = schedule;
		
		render(variant);
	}
	
	public void render(int variant) {
		Graphics2D g = createGraphics();
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, RESOLUTION_XP, RESOLUTION_YP);

		drawGrid(g);
		
		if(shadingModel != null)
			drawShading(g);
		
		if(schedule != null) {
			ArrayList<ClassConfig> scheduleVarient = schedule.getVarient(variant);
			for(ClassConfig config : scheduleVarient)
				drawClass(g, config);
		}
	}
	
	private void drawShading(Graphics2D g) {
		int length = WeekDay.values().length;
		float rectLength = (RESOLUTION_X / (float) length);
		
		int height = TIME_DURATION_HOURS * 60 / TIME_RESOLUTION;
		float rectHeight = (RESOLUTION_Y / (float) height);
		
		for(int i = 0; i < length; i ++) {
			int x = (int) (i * rectLength);
			
			for(int j = 0; j < height; j ++) {
				int y = (int) (j * rectHeight);
				
				g.setColor(shadingModel[0][i][j]);
				g.fillRect(x, y, (int) rectLength + 3, (int) rectHeight + 3);
			}
		}
		
		for(int i = 0; i < length; i ++) {
			int x = (int) (i * rectLength);
			
			for(int j = 0; j < height; j ++) {
				int y = (int) (j * rectHeight);
				
				g.setColor(shadingModel[1][i][j]);
				g.fillRect(x, y, (int) rectLength + 3, (int) rectHeight + 3);
			}
			
		}
	}
	
	private void drawGrid(Graphics2D g) {
		Stroke startStroke = g.getStroke();
		
		drawTimeLines(g, true);
		drawDayLines(g);
		
		g.setStroke(startStroke);
	}
	
	private static void drawDayLines(Graphics2D g) {
		Stroke startStroke = g.getStroke();
		
		g.setStroke(BACKGOUND_WEEK_STROKE); g.setColor(BACKGOUND_WEEK_COLOR);
		for(int i = 0, size = WeekDay.values().length; i < size + 1; i ++) {
			int x = (int) (i * (RESOLUTION_X / (float) size));
			g.drawLine(x, 0, x, RESOLUTION_Y);
		}
		
		g.setStroke(startStroke);
	}
	
	private static void drawTimeLines(Graphics2D g, boolean major) {
		Stroke startStroke = g.getStroke();
		
		g.setStroke(BACKGOUND_TIME_MINOR_STROKE); g.setColor(BACKGOUND_TIME_MINOR_COLOR);
		for(int i = 0, size = TIME_DURATION_HOURS * 60 / TIME_RESOLUTION; i < size; i ++) {
			int y = (int) (i * (RESOLUTION_Y / (float) size));
			g.drawLine(0, y, RESOLUTION_Y, y);
		}
		
		if(major) {
			g.setStroke(BACKGOUND_TIME_MAJOR_STROKE); g.setColor(BACKGOUND_TIME_MAJOR_COLOR);
			for(int i = 0, size = TIME_DURATION_HOURS; i < size + 1; i ++) {
				int y = (int) (i * (RESOLUTION_Y / (float) size));
				g.drawLine(0, y, RESOLUTION_X, y);
			}
		}
		
		g.setStroke(startStroke);
	}
	
	private void drawClass(Graphics2D g, ClassConfig config) {
		Color classColor = schedule.getColorMap().get(config.getSection().getSubject());
		
		for(Designation designation : config.getSection().getDesignations())
			drawDesignation(g, classColor, config.getSection(), designation);
		
		if(config.getLab() != ClassConfig.NO_LAB) {
			Section lab = config.getSection().getLabs().get(config.getLab());
			for(Designation designation : lab.getDesignations())
				drawDesignation(g, classColor, lab, designation);
			
		}
	}

	private void drawDesignation(Graphics2D g, Color color, Section info, Designation designation) {
		int xShift = (int) (designation.getPeriod().getDay().ordinal() * (RESOLUTION_X / (float) WeekDay.values().length));
		int yShift = (int) ((designation.getPeriod().getStartTime().getHour() - MIN_HOUR) * (RESOLUTION_Y / TIME_DURATION_HOURS));
		yShift += (designation.getPeriod().getStartTime().getMinute() * (RESOLUTION_Y / (TIME_DURATION_HOURS * 60.0f)));
		
		int height = (int) (designation.getPeriod().getDuration().toMinutes() * (RESOLUTION_Y / (TIME_DURATION_HOURS * 60.0f)));
		int width = (int) (RESOLUTION_X / (float) WeekDay.values().length);
		
		xShift += 2; yShift += 2;
		height -= 0; width -= 2;
		
		g.setColor(color);
		g.fill3DRect(xShift, yShift, width, height, true);
		
		int midWidth = xShift + width / 2;
		
		Rectangle2D b = null; boolean first = true; int index = 0;
		for(String part : separate(g, info.getSubject().getClassName(), width, new Font("Tahoma", Font.BOLD, 20), 12, true)) {
			if(first) { first = false; g.setFont(new Font("Tahoma", Font.BOLD, Integer.parseInt(part))); continue; }
			b = drawCenteredString(g, capitalize(part), midWidth, yShift + 16 + g.getFont().getSize() * index ++, 
					width - 10, height, -2);
		}
		
		g.setColor(color.darker());
		g.drawLine(xShift + 8, (int) b.getMaxY() + 8, xShift + width - 8, (int) b.getMaxY() + 8);

		Location loc = designation.getLocation();
		g.setFont(new Font("Tahoma", Font.BOLD, 15));
		b = drawCenteredString(g, capitalize(loc.getBuilding() + " " + loc.getRoomNumber()), 
					midWidth, (int) b.getMaxY() + 24, width - 8, height - ((int) b.getMaxY() - yShift + 12), 24);
	
		int loop = 0;
		index = 0; int startHeight = 0;
		String[] parts = separate(g, info.getInstructor(), width, 
				new Font("Tahoma", Font.BOLD, b.getMaxY() - yShift > 1/2f * height ? 16 : 20), 1, true);
		
		for(String part : parts) {
			loop ++;
			
			if(loop == 1) { 
				g.setFont(new Font("Tahoma", Font.BOLD, Integer.parseInt(part))); 
				startHeight = (height - g.getFont().getSize() * (parts.length - 1)) + yShift;
				
				if(startHeight <= b.getMaxY())
					return;
				
				continue; 
			} 
			
			b = drawCenteredString(g, capitalize(part), midWidth, 
					startHeight + g.getFont().getSize() * index ++, width - 10, height, -2);
		
			if(loop == 2) {
				g.setColor(color.darker());
				g.drawLine(xShift + 8, (int) b.getMinY() - 8, xShift + width - 8, (int) b.getMinY() - 8);
			}
		}
	}
	
	private String[] separate(Graphics2D g, String str, int width, Font font, int minSize, boolean repeate) {
		ArrayList<String> results = new ArrayList<>();
		String[] splits = str.split(" ");
		g.setFont(font);
		
		FontMetrics metrics = g.getFontMetrics();
		String current = ""; int len = 0;
		for(String part : splits) {
			part += " ";
			int partLen = (int) metrics.getStringBounds(part, g).getWidth();
			
			cont:
			if(partLen > width) {
				if(font.getSize() <= minSize) break cont;
				else return separate(g, str, width, new Font(font.getFamily(), font.getStyle(), font.getSize()-1), minSize, true);
			}
			
			if(partLen + len > width) {
				len = partLen;
				results.add(current.substring(0, current.length() - 1));
				current = capitalize(part);
				
			} else {
				len += partLen;
				current += part;
			}
		}
		
		results.add(current); 
		results.add(0, font.getSize() + "");
		
		String[] otherResults = new String[10];
		if(repeate) {
			int fontSize = font.getSize();
			do { otherResults = separate(g, str, width, new Font(font.getFamily(), font.getStyle(), -- fontSize), minSize, false);
			} while(otherResults.length >= results.size() && fontSize > minSize && font.getSize() - 4 < fontSize);
		}
		
		return otherResults.length < results.size() ? otherResults : results.toArray(new String[results.size()]);
	}
	
	private String capitalize(String line) {
		return Character.toUpperCase(line.charAt(0)) + line.substring(1);
	}
	
	private static final int MIN_FONT_SIZE = 7;
	private static final int MAX_FONT_SIZE = 20;
	
	private static Font getMaxSize(Graphics2D g, Font font, String string, int width, int height, int maxCap){
		int currentSize = font.getSize();
		
		int minSize = MIN_FONT_SIZE;
		int maxSize = maxCap == -1 ? MAX_FONT_SIZE : maxCap;
		
		while (maxSize - minSize > 2){
			FontMetrics fm = g.getFontMetrics(font = new Font(font.getName(), font.getStyle(), currentSize));
			int fontWidth = fm.stringWidth(string);
			int fontHeight = fm.getHeight();
		
			if(fontWidth > width || fontHeight > height){
				maxSize = currentSize;
				currentSize = (maxSize + minSize) / 2;
			} else {
				minSize = currentSize;
				currentSize = (minSize + maxSize) / 2;
			}
		}
		
		return font;
	}
	
	private Rectangle2D drawCenteredString(Graphics2D g, String str, int x, int y, int width, int height, int maxCap) {
		Stroke startStroke = g.getStroke();
		if(maxCap != -2) g.setFont(getMaxSize(g, g.getFont(), str, width, height, maxCap));
		if(g.getFont().getSize() <= MIN_FONT_SIZE)
			return new Rectangle2D.Float(x, y, width, height);
		
		Font font = g.getFont();
	    GlyphVector gv = font.createGlyphVector(g.getFontRenderContext(), str);
    	Shape letter = gv.getOutline();

    	Rectangle2D bounds = letter.getBounds2D();
    	AffineTransform transform = new AffineTransform();
    	transform.translate(x - bounds.getCenterX(), y - bounds.getCenterY());
    	letter = transform.createTransformedShape(letter);
    	
    	g.setStroke(new BasicStroke(1));
    	g.setColor(Color.WHITE); g.fill(letter);    	
    	g.setColor(Color.BLACK); g.draw(letter);
	    
		g.setStroke(startStroke);
		
		return letter.getBounds2D();
	}
	
	public void setShadingModel(Color[][][] shadingModel) { this.shadingModel = shadingModel; }
	
	public void save() {
		int result = fileChooser.showSaveDialog(null);
		if(result == JFileChooser.CANCEL_OPTION)
			return;
		
		try {
			BufferedImage image = new BufferedImage(
					getWidth() + timeLabels.getWidth(), 
					getHeight() + weekHeader.getHeight(), 
				TYPE_INT_ARGB);
			
			Graphics2D g = image.createGraphics();
			g.drawImage(weekHeader, LABELS_LENGTH, 0, null);
			g.drawImage(timeLabels, 0, HEADER_HEIGHT, null);
			g.drawImage(this, LABELS_LENGTH, HEADER_HEIGHT, null);
			g.dispose();
			
			ImageIO.write(image, "png", fileChooser.getSelectedFile());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}
