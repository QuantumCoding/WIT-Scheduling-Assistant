package user_interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import scheduling.Schedule;
import util.Fonts;

public class ScheduleView extends JScrollPane {
	private static final long serialVersionUID = 883594012378077662L;

	private ScheduleImage scheduleImage;
	private JPanel scheduleDisplay;
	private int variant;
	
	private static final int RESOLUTION_XP = (int) (ScheduleImage.RESOLUTION_XP * Fonts.LENGTH_SCALE);
	private static final int RESOLUTION_YP = (int) (ScheduleImage.RESOLUTION_YP * Fonts.HEIGHT_SCALE);

	private static final int LABELS_LENGTH = (int) (ScheduleImage.LABELS_LENGTH * Fonts.LENGTH_SCALE);
	private static final int HEADER_HEIGHT = (int) (ScheduleImage.HEADER_HEIGHT * Fonts.HEIGHT_SCALE);
	
	public ScheduleView(Schedule schedule) {
		getVerticalScrollBar().setUnitIncrement(RESOLUTION_YP / 72);
		
		scheduleDisplay = new JPanel() {
			private static final long serialVersionUID = 4796041366087900724L;
			public void paintComponent(Graphics g) { super.paintComponent(g); paintSchedule(g); }
		};
		scheduleDisplay.setBackground(Color.WHITE);
		Dimension displaySize = new Dimension(RESOLUTION_XP, RESOLUTION_YP);
		scheduleDisplay.setPreferredSize(displaySize);
		setViewportView(scheduleDisplay);
		
		JPanel headerPanel = new JPanel() {
			private static final long serialVersionUID = 7848856260287365290L;
			public void paintComponent(Graphics g) { 
				super.paintComponent(g); 
				g.drawImage(ScheduleImage.weekHeader, 0, 0, RESOLUTION_XP, HEADER_HEIGHT, null);
			}
		};

		Dimension headerSize = new Dimension(RESOLUTION_XP, HEADER_HEIGHT);
		headerPanel.setPreferredSize(headerSize);
		setColumnHeaderView(headerPanel);
		
		JPanel labelPanel = new JPanel() {
			private static final long serialVersionUID = 9199066327137139730L;
			public void paintComponent(Graphics g) { 
				super.paintComponent(g); 
				g.drawImage(ScheduleImage.timeLabels, 0, 0, LABELS_LENGTH, RESOLUTION_YP, null);
			}
		};

		Dimension labeSize = new Dimension(LABELS_LENGTH, RESOLUTION_YP);
		labelPanel.setPreferredSize(labeSize);
		setRowHeaderView(labelPanel);
		
		changeSchedule(schedule);
	}
	
	private void paintSchedule(Graphics g) {
		g.drawImage(scheduleImage, 0, 0, RESOLUTION_XP, RESOLUTION_YP, null);
	}
	
	public void setShadingModel(Color[][][] shadingModel) {
		scheduleImage.setShadingModel(shadingModel);
		scheduleImage.render(variant);
	}

	public void changeSchedule(Schedule schedule) {
		scheduleImage = new ScheduleImage(schedule, 0);
		scheduleDisplay.repaint();
	}
	
	public void changeVariant(int variant) {
		this.variant = variant;
		scheduleImage.render(variant);
		scheduleDisplay.repaint();
 	}
	
	public void saveImage() {
		scheduleImage.save();
	}
}
