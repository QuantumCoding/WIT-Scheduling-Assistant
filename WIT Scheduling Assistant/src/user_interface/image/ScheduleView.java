package user_interface.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import scheduling.ClassConfig;
import scheduling.Designation;
import scheduling.Period.WeekDay;
import scheduling.TreeSchedule;
import util.Fonts;

public class ScheduleView extends JScrollPane {
	private static final long serialVersionUID = 883594012378077662L;

	private ScheduleImage scheduleImage;
	private JPanel scheduleDisplay;
	
	private static final int LABELS_LENGTH = (int) (ScheduleImage.LABELS_LENGTH * Fonts.LENGTH_SCALE);
	private static final int HEADER_HEIGHT = (int) (ScheduleImage.HEADER_HEIGHT * Fonts.HEIGHT_SCALE);
	
	private int xMin, yMin, xMax, yMax;
	private int resX, resY;
	private boolean trim;
	
	private JPanel headerPanel;
	private JPanel labelPanel;
	
	public ScheduleView(TreeSchedule schedule) {
		trim = true;
		
		scheduleDisplay = new JPanel() {
			private static final long serialVersionUID = 4796041366087900724L;
			public void paintComponent(Graphics g) { super.paintComponent(g); paintSchedule(g); }
		};
		
		scheduleDisplay.setBackground(Color.WHITE);
		setViewportView(scheduleDisplay);
		
		headerPanel = new JPanel() {
			private static final long serialVersionUID = 7848856260287365290L;
			public void paintComponent(Graphics g) { 
				super.paintComponent(g); 
				g.drawImage(ScheduleImage.weekHeader, 0, 0, resX, HEADER_HEIGHT, xMin, 0, xMax, ScheduleImage.HEADER_HEIGHT, null);
			}
		};

		labelPanel = new JPanel() {
			private static final long serialVersionUID = 9199066327137139730L;
			public void paintComponent(Graphics g) { 
				super.paintComponent(g); 
				g.drawImage(ScheduleImage.timeLabels, 0, 0, LABELS_LENGTH, resY, 0, yMin, ScheduleImage.LABELS_LENGTH, yMax, null);
			}
		};

		changeSchedule(schedule);
	}
	
	private void paintSchedule(Graphics g) {
		g.drawImage(scheduleImage, 0, 0, resX, resY, xMin, yMin, xMax, yMax, null);
	}
	
	public void setShadingModel(Color[][][] shadingModel) {
		scheduleImage.setShadingModel(shadingModel);
		scheduleImage.render();
		repaint();
	}

	private void updateScales(TreeSchedule schedule) {
		if(trim) {
			xMax = yMax = 0;
			xMin = ScheduleImage.RESOLUTION_XP;
			yMin = ScheduleImage.RESOLUTION_YP;
			
			for(ClassConfig config : schedule.getSections()) {
				for(Designation designation : config.getSection().getDesignations())
					updateSizeLimits(designation);
				
				if(config.getLab() == ClassConfig.NO_LAB) continue;
				for(Designation designation : config.getSection().getLabs().get(config.getLab()).getDesignations())
					updateSizeLimits(designation);
			}
		} else {
			xMin = yMin = 0;
			xMax = ScheduleImage.RESOLUTION_XP;
			yMax = ScheduleImage.RESOLUTION_YP;
		}
		
		resX = (int) ((xMax - xMin) * Fonts.LENGTH_SCALE);
		resY = (int) ((yMax - yMin) * Fonts.HEIGHT_SCALE);
		
		Dimension headerSize = new Dimension(resX, HEADER_HEIGHT);
		headerPanel.setPreferredSize(headerSize);
		setColumnHeaderView(headerPanel);

		Dimension labeSize = new Dimension(LABELS_LENGTH, resY);
		labelPanel.setPreferredSize(labeSize);
		setRowHeaderView(labelPanel);

		Dimension displaySize = new Dimension(resX, resY);
		scheduleDisplay.setPreferredSize(displaySize);
		
		getVerticalScrollBar().setUnitIncrement(resY / 72);
	}
	
	public void changeSchedule(TreeSchedule schedule) {
		updateScales(schedule);
		
		scheduleImage = new ScheduleImage(schedule);
		scheduleDisplay.repaint();
		headerPanel.repaint();
		labelPanel.repaint();
	}
	
	private void updateSizeLimits(Designation designation) {
		float xSize = (ScheduleImage.RESOLUTION_X / (float) WeekDay.values().length);
		int calcXmin = (int) (designation.getPeriod().getDay().ordinal() * xSize);
		int calcXmax = (int) (calcXmin + xSize) + 3;

		int calcYmin = (int) ((designation.getPeriod().getStartTime().getHour() - ScheduleImage.MIN_HOUR) * (ScheduleImage.RESOLUTION_Y / ScheduleImage.TIME_DURATION_HOURS));
		calcYmin += (designation.getPeriod().getStartTime().getMinute() * (ScheduleImage.RESOLUTION_Y / (ScheduleImage.TIME_DURATION_HOURS * 60.0f)));
		int calcYmax = calcYmin + (int) (Math.ceil(designation.getPeriod().getDuration().toMinutes() / 60.0) * 60 * (ScheduleImage.RESOLUTION_Y / (ScheduleImage.TIME_DURATION_HOURS * 60.0f)));
	
		if(calcXmin < xMin) xMin = calcXmin;
		if(calcYmin < yMin) yMin = calcYmin;
		if(calcXmax > xMax) xMax = calcXmax;
		if(calcYmax > yMax) yMax = calcYmax;
	}
	
	public void shouldTrim(boolean trim) { 
		this.trim = trim; 
		updateScales(scheduleImage.getSchedule());
		scheduleImage.render(); 
		repaint(); 
	}
	
	public boolean isTrimming() { return trim; }
	
	public void saveImage() { scheduleImage.save(); }
	public ImageSettings getSettings() { return scheduleImage.getSettings(); }
	public void setSettings(ImageSettings settings) { scheduleImage.setSettings(settings); repaint(); }
}
