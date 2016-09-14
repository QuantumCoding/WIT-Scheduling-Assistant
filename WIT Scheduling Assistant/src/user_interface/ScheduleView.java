package user_interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import scheduling.Schedule;

public class ScheduleView extends JScrollPane {
	private static final long serialVersionUID = 883594012378077662L;

	private ScheduleImage scheduleImage;
	private JPanel scheduleDisplay;
	
	public ScheduleView(Schedule schedule) {
		getVerticalScrollBar().setUnitIncrement(ScheduleImage.RESOLUTION_YP / 72);
		
		scheduleDisplay = new JPanel() {
			private static final long serialVersionUID = 4796041366087900724L;
			public void paintComponent(Graphics g) { super.paintComponent(g); paintSchedule(g); }
		};
		scheduleDisplay.setBackground(Color.WHITE);
		Dimension displaySize = new Dimension(ScheduleImage.RESOLUTION_XP, ScheduleImage.RESOLUTION_YP);
		scheduleDisplay.setPreferredSize(displaySize);
		setViewportView(scheduleDisplay);
		
		JPanel headerPanel = new JPanel() {
			private static final long serialVersionUID = 7848856260287365290L;
			public void paintComponent(Graphics g) { 
				super.paintComponent(g); 
				g.drawImage(ScheduleImage.weekHeader, 0, 0, ScheduleImage.RESOLUTION_XP, ScheduleImage.HEADER_HEIGHT, null);
			}
		};

		Dimension headerSize = new Dimension(ScheduleImage.RESOLUTION_XP, ScheduleImage.HEADER_HEIGHT);
		headerPanel.setPreferredSize(headerSize);
		setColumnHeaderView(headerPanel);
		
		JPanel labelPanel = new JPanel() {
			private static final long serialVersionUID = 9199066327137139730L;
			public void paintComponent(Graphics g) { 
				super.paintComponent(g); 
				g.drawImage(ScheduleImage.timeLabels, 0, 0, ScheduleImage.LABELS_LENGTH, ScheduleImage.RESOLUTION_YP, null);
			}
		};

		Dimension labeSize = new Dimension(ScheduleImage.LABELS_LENGTH, ScheduleImage.RESOLUTION_YP);
		labelPanel.setPreferredSize(labeSize);
		setRowHeaderView(labelPanel);
		
		changeSchedule(schedule);
	}
	
	private void paintSchedule(Graphics g) {
		g.drawImage(scheduleImage, 0, 0, ScheduleImage.RESOLUTION_XP, ScheduleImage.RESOLUTION_YP, null);
	}

	public void changeSchedule(Schedule schedule) {
		scheduleImage = new ScheduleImage(schedule, 0);
		scheduleDisplay.repaint();
	}
	
	public void changeVariant(int variant) {
		scheduleImage.render(variant);
		scheduleDisplay.repaint();
 	}
	
	public void saveImage() {
		scheduleImage.save();
	}
}
