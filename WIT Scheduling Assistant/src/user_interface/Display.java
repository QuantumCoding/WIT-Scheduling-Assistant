package user_interface;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import pages.wit.LookupResultsPage.LookupResult;
import scheduling.Schedule;
import scheduling.Scheduler;
import scheduling.SchedulingException;
import scheduling.Section;
import security.SecretKeyUtil;
import util.References;
import web_interface.PageLock;

public class Display extends JFrame {
	private static final long serialVersionUID = 4395497351445767482L;
	
	private JPanel contentPane;
	private LoginScreen loginScreen;
	private MainMenuScreen menuScreen;
	private LoadingScreen loadingScreen;
	private AddClassScreen addClassScreen;
	private ScheduleDisplayScreen displayScreen;
	private TimePreferanceScreen timeScreen;
	
	private PageLock pages;
	private SecretKeyUtil vault;
	private Scheduler scheduler;
	
	public Display(PageLock pages) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) { }
		
		this.pages = pages;
		
		try {
			this.vault = new SecretKeyUtil(Paths.get(References.Vault_Location), References.Vault_Password.toCharArray());
			setIconImage(ImageIO.read(Display.class.getResource("WITSchedulerIcon.png")));
		} catch(KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
		}
		
		loginScreen = new LoginScreen(pages, this);
		menuScreen = new MainMenuScreen(pages, this);
		loadingScreen = new LoadingScreen();
		timeScreen = new TimePreferanceScreen(this);
		
		setTitle("WIT Scheduling Assistant");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout());

		contentPane.add(menuScreen, "menuScreen");
		contentPane.add(loginScreen, "loginScreen");
		contentPane.add(loadingScreen, "loadingScreen");
		contentPane.add(timeScreen, "timeScreen");

		((CardLayout) contentPane.getLayout()).show(contentPane, "loginScreen");
		
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		
		KeyListener debugToggle = new KeyAdapter() {
			private boolean shift, control, trigger;
			
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_SHIFT)
					shift = false;
				else if(e.getKeyCode() == KeyEvent.VK_CONTROL)
					control = false;
			}
			
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_SHIFT)
					shift = true;
				else if(e.getKeyCode() == KeyEvent.VK_CONTROL)
					control = true;
				else if(shift && control && !trigger && e.getKeyCode() == KeyEvent.VK_D) {
					boolean state = Section.DEBUG_ALLOW_NON_REG_CLASS;
					
					Section.DEBUG_ALLOW_NON_REG_CLASS = JOptionPane.showConfirmDialog(Display.this, "Are you sure you want to " + 
							(!state ? "Enable" : "Disable") + " DEBUG mode?") == JOptionPane.YES_OPTION ? !state : state;
					
					trigger = true;
				} else trigger = false;
			}
		};
		
		addToAll(this, debugToggle);
	}
	
	private void addToAll(Container container, KeyListener listener) {
		for(Component comp : container.getComponents()) {
			if(comp == null) continue;
			if(comp instanceof Container)
				addToAll((Container) comp, listener);
			
			comp.addKeyListener(listener);
		}
	}
	
	public void switchToClassAdder() {
		((CardLayout) contentPane.getLayout()).show(contentPane, "addClassScreen");
	}
	
	public void switchToMainMenu() {
		((CardLayout) contentPane.getLayout()).show(contentPane, "menuScreen");
	}
	
	public void switchToTimePreferences() {
		((CardLayout) contentPane.getLayout()).show(contentPane, "timeScreen");
	}
	
	public void showSchedules(ArrayList<Schedule> schedules) {
		if(displayScreen == null) {
			displayScreen = new ScheduleDisplayScreen(this, schedules);
			contentPane.add(displayScreen, "displayScreen");
		} else displayScreen.setSchedules(schedules);
		
		((CardLayout) contentPane.getLayout()).show(contentPane, "displayScreen");
	}
	
	public void showLoading(String message) {
		loadingScreen.setMessage(message);
		((CardLayout) contentPane.getLayout()).show(contentPane, "loadingScreen");
	}
	
	public void showLoading(String message, int value, int max) {
		loadingScreen.setMessage(message, value, max);
		((CardLayout) contentPane.getLayout()).show(contentPane, "loadingScreen");
	}
	
	public void loginComplete() {
		addClassScreen = new AddClassScreen(pages, this);
		contentPane.add(addClassScreen, "addClassScreen");

		pack();
		setLocationRelativeTo(null);
		
		switchToMainMenu();
	}
	
	public boolean shouldSave() { return loginScreen.rememberMe(); }
	public Color[][][] getTimeSchadingModel() { return timeScreen.getShadingModel(); }
	
	public void submitClasses(ArrayList<LookupResult> classes, HashMap<LookupResult, ArrayList<Section>> preCollectedSections, HashMap<LookupResult, ArrayList<Boolean>> nonValid) {
		showLoading("Collecting Class Options...");
		scheduler = new Scheduler(pages, classes, preCollectedSections, nonValid);
		scheduler.setRankings(timeScreen.getRankings());
		new SchedulerTask().execute();
	}
	
	private class SchedulerTask extends SwingWorker<Void, Integer> {
		private int stage, useCount;
		private boolean error;
		
		private String errorMessage;
		
		protected Void doInBackground() throws Exception {
			publish(-2); 
			scheduler.compare();
			try { Thread.sleep(500); } catch(InterruptedException e) {}
			
			publish(-1); int possibleScheduals = 0;
			try { possibleScheduals = scheduler.calculateCombinationCount(); }
			catch(SchedulingException e) { 
				errorMessage = e.getMessage();
				publish(-3);

				e.printStackTrace(); 
				return null; 
			}
			
			try { Thread.sleep(500); } catch(InterruptedException e) {}
			
			stage = 0;
			for(int i = 0; i < possibleScheduals; i ++) {
				scheduler.createSchedule(i);
				publish(i + 1);
			}

			try { Thread.sleep(500); } catch(InterruptedException e) {}
			stage = 1;
			for(int i = 0; i < possibleScheduals; i ++) {
				scheduler.removeInvalidSchedules(i);
				publish(i + 1);
			}
			
			
			if(scheduler.getSchedules().size() < 1) {
				errorMessage = "No Possible Schedules for Selected Classes";
				publish(-3);

				return null; 
			}
			
			ArrayList<Schedule> schedules = scheduler.getSchedules();
			try { Thread.sleep(500); } catch(InterruptedException e) {}
			stage = 2; useCount = schedules.size();
			
			for(int i = 0; i < schedules.size(); i ++) {
				scheduler.calculateVariants(schedules.get(i).getId());
				publish(i + 1);
			}
			
			try { Thread.sleep(500); } catch(InterruptedException e) {}
			stage = 3;
			
			for(int i = 0; i < schedules.size(); i ++) {
				scheduler.calculateWeights(schedules.get(i).getId());
				publish(i + 1);
			}
			
			try { Thread.sleep(500); } catch(InterruptedException e) {}
			return null;
		}
		
		protected void process(List<Integer> values) {
			switch(values.get(values.size() - 1)) {
				case -2: showLoading("Comparing Classes..."); break;
				case -1: showLoading("Preparing to Create Schedule.."); break;
				
				case -3: error = true;
					contentPane.add(new ErrorScreen(Display.this, errorMessage), "error");
					((CardLayout) contentPane.getLayout()).show(contentPane, "error");
				break;
				
				default: 
					switch(stage) {
						
						case 0:
							showLoading("Creating Schedule "
									+ "[" + values.get(values.size() - 1) + " / " + scheduler.posible() + "]", 
									values.get(values.size() - 1), scheduler.posible());  
						break;
						
						case 1:
							showLoading("Removing Invalid Schedules "
									+ "[" + values.get(values.size() - 1) + " / " + scheduler.posible() + "]", 
									values.get(values.size() - 1), scheduler.posible());  
						break;
						
						case 2:
							showLoading("Creating Varaints [" + 
								values.get(values.size() - 1) + " / " + useCount + "]",
								values.get(values.size() - 1), useCount);  
						break;
						
						case 3:
							showLoading("Calculating Weights [" + 
								values.get(values.size() - 1) + " / " + useCount + "]",
								values.get(values.size() - 1), useCount);  
						break;
					}
			}
		}
		
		protected void done() {
			if(stage < 1)
				return;
			
			scheduler.cleanOut();
//			System.out.println("Done");
			
			if(!error) {
				displayScreen = new ScheduleDisplayScreen(Display.this, scheduler.getSchedules());
				contentPane.add(displayScreen, "displayScreen");
	
				((CardLayout) contentPane.getLayout()).show(contentPane, "displayScreen");
			}
		}
	}
	
	public SecretKeyUtil getVault() { return vault; }
}
