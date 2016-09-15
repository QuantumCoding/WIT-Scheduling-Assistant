package user_interface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import scheduling.Period.WeekDay;
import util.References;

public class TimePreferanceView extends JScrollPane implements MouseMotionListener, MouseListener {
	private static final long serialVersionUID = 883594012378077662L;

	private ScheduleImage scheduleImage;
	private JPanel scheduleDisplay;
	
	public TimePreferanceView() {
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
		
		changeSchedule();
		fullClear();
	}
	
	private void paintSchedule(Graphics g) {
		g.drawImage(scheduleImage, 0, 0, ScheduleImage.RESOLUTION_XP, ScheduleImage.RESOLUTION_YP, null);
	}

	public void changeSchedule() {
		scheduleImage = new ScheduleImage(null, 0);
		scheduleDisplay.repaint();
	}

	private static final Color CLEAR_COLOR = new Color(255, 255, 255, 0);
	private static final int FINAL_ALPHA = 175; 
	private static final int MODIFY_ALPHA = 75; 
	
	private Color[][][] shadingModel;
	private Point startCell, endCell;
	private int buttonDown;
	
	private Color currentRank, modifyRank, modifyRankHold;
	
	public Color[][][] getShadingModel() { return shadingModel; }
	
	public float[][] getRankings() {
		float[][] ranks = new float[shadingModel[0].length][shadingModel[0][0].length];
		for(int i = 0; i < ranks.length; i ++) 
		for(int j = 0; j < ranks[i].length; j ++) 
			ranks[i][j] = shadingModel[0][i][j] == CLEAR_COLOR ? 0.5f :
				Color.RGBtoHSB(
					shadingModel[0][i][j].getRed(), 
					shadingModel[0][i][j].getGreen(), 
					shadingModel[0][i][j].getBlue(), 
				null)[0] * 3f/5f;
		
		return ranks;
	}
	
	public void save() {
		try(DataOutputStream out = new DataOutputStream(Files.newOutputStream(Paths.get(References.Time_Pref_Location)))) {
			
			for(int i = 0; i < shadingModel[0].length; i ++) 
			for(int j = 0; j < shadingModel[0][i].length; j ++) 
				out.writeFloat(shadingModel[0][i][j] == CLEAR_COLOR ? 0.5f :
					Color.RGBtoHSB(
						shadingModel[0][i][j].getRed(), 
						shadingModel[0][i][j].getGreen(), 
						shadingModel[0][i][j].getBlue(), 
					null)[0] * 3f/5f);
			
		} catch(IOException e) {}
	}
	
	public void load() {
		try(DataInputStream in = new DataInputStream(Files.newInputStream(Paths.get(References.Time_Pref_Location)))) {
			Color current = currentRank;
			
			for(int i = 0; i < shadingModel[0].length; i ++) { 
			for(int j = 0; j < shadingModel[0][i].length; j ++) {
				float ranking = in.readFloat(); 
				shadingModel[0][i][j] = Color.getHSBColor(ranking * 5f/3f, 1, 1);
				setRank(shadingModel[0][i][j]);
				shadingModel[0][i][j] = currentRank;
			}}
			
			currentRank = current;
			updateImage();
		} catch(IOException e) {}
	}
	
	public void setRank(Color rank) { 
		this.currentRank = new Color(rank.getRed(), rank.getGreen(), rank.getBlue(), FINAL_ALPHA); 
		this.modifyRank = new Color(rank.getRed(), rank.getGreen(), rank.getBlue(), MODIFY_ALPHA); 
	}
	
	public Point getCell(Point point) {
		int xLimit = WeekDay.values().length;
		int yLimit = ScheduleImage.TIME_DURATION_HOURS * 60 / ScheduleImage.TIME_RESOLUTION;
		
		int cellX = (int) (point.getX() / ScheduleImage.RESOLUTION_X * xLimit);
		int cellY = (int) (point.getY() / ScheduleImage.RESOLUTION_Y * yLimit); 
		
		cellX = cellX < 0 ? 0 : cellX;
		cellY = cellY < 0 ? 0 : cellY;

		cellX = cellX > xLimit - 1 ? xLimit - 1 : cellX;
		cellY = cellY > yLimit - 1 ? yLimit - 1 : cellY;
		
		return new Point(cellX, cellY);
	} 
	
	private void updateImage() {
		scheduleImage.setShadingModel(shadingModel);
		scheduleImage.render(0);
		scheduleDisplay.repaint();
	}
	
	public void fullClear() {
		scheduleDisplay.addMouseListener(this);
		scheduleDisplay.addMouseMotionListener(this);
		shadingModel = new Color[2][WeekDay.values().length]
				[(ScheduleImage.TIME_DURATION_HOURS * 60 / ScheduleImage.TIME_RESOLUTION)];
		
		startCell = new Point(0, 0);
		endCell = new Point(
				WeekDay.values().length - 1,
				ScheduleImage.TIME_DURATION_HOURS * 60 / ScheduleImage.TIME_RESOLUTION - 1
			);
		
		clear(); cancle();
		
		updateImage();
	}
	
	private void cancle() {
		for(Color[] columns : shadingModel[1])
			Arrays.fill(columns, CLEAR_COLOR);
		
		startCell = null;
		endCell = null;
		
		updateImage();
	}
	
	private void clear() {
		if(startCell == null || endCell == null) return;
		
		for(int x = Math.min(startCell.x, endCell.x); x < Math.max(startCell.x, endCell.x) + 1; x ++)
		for(int y = Math.min(startCell.y, endCell.y); y < Math.max(startCell.y, endCell.y) + 1; y ++)
			shadingModel[0][x][y] = CLEAR_COLOR;
				
		cancle();
	}
	
	private void conclude() {
		if(startCell == null || endCell == null) return;
		
		for(int x = Math.min(startCell.x, endCell.x); x < Math.max(startCell.x, endCell.x) + 1; x ++)
		for(int y = Math.min(startCell.y, endCell.y); y < Math.max(startCell.y, endCell.y) + 1; y ++)
			shadingModel[0][x][y] = currentRank;
			
		cancle();
	}
	
	public void mouseDragged(MouseEvent e) {
		if(buttonDown == MouseEvent.NOBUTTON) return; 
		
		Point selectedCell = getCell(e.getPoint());
		
		if(startCell == null)
			startCell = selectedCell;
	
		endCell = selectedCell;
		
		for(Color[] columns : shadingModel[1])
			Arrays.fill(columns, CLEAR_COLOR);
		
		for(int x = Math.min(startCell.x, endCell.x); x < Math.max(startCell.x, endCell.x) + 1; x ++)
		for(int y = Math.min(startCell.y, endCell.y); y < Math.max(startCell.y, endCell.y) + 1; y ++)
			shadingModel[1][x][y] = modifyRank;
		
		updateImage();
	}
	
	public void mousePressed(MouseEvent e) {
		if(buttonDown == MouseEvent.BUTTON1 && buttonDown != e.getButton()) {
			buttonDown = MouseEvent.BUTTON3;
			cancle();
			buttonDown = MouseEvent.NOBUTTON;
		}
		
		buttonDown = e.getButton();
		
		if(buttonDown != MouseEvent.BUTTON1) {
			modifyRankHold = modifyRank;
			modifyRank = new Color(255, 255, 255, 150);
		}
	}

	public void mouseReleased(MouseEvent e) {
		if(buttonDown == MouseEvent.BUTTON1) 
			conclude();
		else {
			if(modifyRankHold != null) {
				modifyRank = modifyRankHold;
				modifyRankHold = null;
			}
			
			clear();
		}
		
		buttonDown = MouseEvent.NOBUTTON;
	}

	public void mouseMoved(MouseEvent e) { }

	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
}
