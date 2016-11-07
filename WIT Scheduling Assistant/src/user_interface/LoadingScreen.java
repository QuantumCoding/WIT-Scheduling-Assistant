package user_interface;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BoundedRangeModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicProgressBarUI;

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
		
		loadingProgressBar.setUI(new BasicProgressBarUI() {
			private Handler handler;

			private double renderOffset = 0;
			private double renderProgress = 0;
			private double targetProgress = 0;
			private double progressDelta = 0.04;
			
			private Timer indeterminateTime;
			private Timer repaintTimer;
			private Timer paintTimer;
				
			{
			    repaintTimer = new Timer(5, e -> progressBar.repaint());
			    repaintTimer.setRepeats(false);
			    repaintTimer.setCoalesce(true);
				
			    paintTimer = new Timer(40, e -> {
					if(progressDelta < 0) {
					    if(renderProgress + progressDelta < targetProgress) {
						    ((Timer) e.getSource()).stop();
						    renderProgress = 0;//targetProgress + progressDelta;
					    }
					} else {
					    if(renderProgress + progressDelta > targetProgress) {
						    ((Timer) e.getSource()).stop();
						    renderProgress = targetProgress - progressDelta;
					    }
					}
				    
				    renderProgress += progressDelta;
				    requestRepaint();
			    });
			    
			    indeterminateTime = new Timer(40, e -> {
			    	renderProgress = .2;
			    	
			    	renderOffset += renderProgress;
			    	renderOffset = (renderOffset + progressDelta/1.5) % (1.25 + renderProgress);
			    	renderOffset -= renderProgress;
			    	
			    	requestRepaint();
			    });
			}
				
			protected void requestRepaint() { repaintTimer.restart(); }
				
			protected void installDefaults() {
				super.installDefaults();
				progressBar.setOpaque(false);
				progressBar.setBorder(null);
			}
				
			public void setRenderProgress(double value) {
				if(value != targetProgress) {
					paintTimer.stop();
				
					targetProgress = value;
					if (targetProgress < renderProgress && progressDelta > 0) {
					    progressDelta *= -1;
					} else if (targetProgress > renderProgress && progressDelta < 0) {
					    progressDelta *= -1;
					}
					
					paintTimer.start();
				}
			}
				
			public void paint(Graphics g, JComponent c) {
				Graphics2D g2d = (Graphics2D) g.create();
					
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					
				int strokWidth = 3;
				g2d.setStroke(new BasicStroke(strokWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
				g2d.setBackground(c.getBackground().darker().darker());
					
				int width = c.getWidth();
				int height = c.getHeight();
					
				RoundRectangle2D outline = new RoundRectangle2D.Double((strokWidth / 2), (strokWidth / 2),
						width - strokWidth, height - strokWidth, height, height);
				
				g2d.setColor(c.getBackground().darker().darker());
				g2d.draw(outline);
					
				int innerHeight = height - (strokWidth * 4);
				int innerWidth = width - (strokWidth * 4);

				int x = strokWidth * 2 + (int) Math.round(renderOffset * innerWidth);
				int y = strokWidth * 2;
					
				innerWidth = (int) Math.round(innerWidth * renderProgress);
					
				Point2D start = new Point2D.Double(x, y);
				Point2D end = new Point2D.Double(x, y + innerHeight);
				
				Color base = new Color(255, 140, 0);
				float[] dist = { 0.0f, 0.25f, 1.0f };
				Color[] colors = { base, base.brighter(), base.darker() };
				LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);
					
				g2d.setPaint(p);
				g2d.setClip(new RoundRectangle2D.Double(strokWidth * 2, strokWidth * 2,
						width - (strokWidth * 4), innerHeight, innerHeight, innerHeight)
					);
				
				RoundRectangle2D fill = new RoundRectangle2D.Double(x, y,
						innerWidth, innerHeight, innerHeight, innerHeight);
				g2d.fill(fill);
				
					
				g2d.dispose();
			}
				
			protected void installListeners() {
				super.installListeners();
				progressBar.addChangeListener(getHandler());
				progressBar.addPropertyChangeListener("indeterminate", getHandler());
			}
				
			protected Handler getHandler() {
				if(handler == null)
					handler = new Handler();
				return handler;
			}
				
			class Handler implements ChangeListener, PropertyChangeListener {
				public void stateChanged(ChangeEvent e) {
					BoundedRangeModel model = progressBar.getModel();
					int newRange = model.getMaximum() - model.getMinimum();
					double progress = (double) (model.getValue() / (double) newRange);
				
					if(progress < 0) 		progress = 0;
					else if(progress > 1) 	progress = 1;
				
					renderOffset = 0;
					setRenderProgress(progress);
				}

				public void propertyChange(PropertyChangeEvent e) {
					if(e.getPropertyName().equals("indeterminate")) {
						if((Boolean) e.getNewValue())
							indeterminateTime.start();
						else
							indeterminateTime.stop();
					}
				}
			}
	    });
		
		loadingProgressBar.setIndeterminate(true);
		loadingProgressBar.setIndeterminate(false);
		loadingProgressBar.setIndeterminate(true);
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
