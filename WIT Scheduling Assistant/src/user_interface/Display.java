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

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import pages.ClassOption;
import scheduling.Scheduler_Tree;
import scheduling.Scheduler_Tree.Stage;
import scheduling.SchedulingException;
import scheduling.Section;
import scheduling.TreeSchedule;
import security.SecretKeyUtil;
import user_interface.screens.AddClassScreen;
import user_interface.screens.ErrorScreen;
import user_interface.screens.LoadingScreen;
import user_interface.screens.LoginScreen;
import user_interface.screens.MainMenuScreen;
import user_interface.screens.ScheduleDisplayScreen;
import user_interface.screens.TimePreferanceScreen;
import util.References;

public class Display extends JFrame {
	private static final long serialVersionUID = 4395497351445767482L;
	
	private JPanel contentPane;
	private LoginScreen loginScreen;
	private MainMenuScreen menuScreen;
	private LoadingScreen loadingScreen;
	private AddClassScreen addClassScreen;
	private ScheduleDisplayScreen displayScreen;
	private TimePreferanceScreen timeScreen;
	
	private SecretKeyUtil vault;
	
	public Display() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) { }
		
		try {
			this.vault = new SecretKeyUtil(Paths.get(References.Vault_Location), References.Vault_Password.toCharArray());
			setIconImage(ImageIO.read(Display.class.getResource("WITSchedulerIcon.png")));
		} catch(KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
		}
		
		loginScreen = new LoginScreen(this);
		menuScreen = new MainMenuScreen(this);
		loadingScreen = new LoadingScreen();
		timeScreen = new TimePreferanceScreen(this);
		
		setTitle("WIT Scheduling Assistant");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(new CardLayout());

		contentPane.add(loginScreen, "loginScreen");
		contentPane.add(loadingScreen, "loadingScreen");

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
	
	public float[][] getRankings() { return timeScreen.getRankings(); }
	
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
	
	public void showSchedules(ArrayList<TreeSchedule> schedules) {
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
		contentPane.add(menuScreen, "menuScreen");
		contentPane.add(timeScreen, "timeScreen");
		
		addClassScreen = new AddClassScreen(this);
		contentPane.add(addClassScreen, "addClassScreen");

		pack();
		setLocationRelativeTo(null);
		
		switchToMainMenu();
	}
	
	public void loginFailed() {
		((CardLayout) contentPane.getLayout()).show(contentPane, "loginScreen");
	}
	
	public boolean shouldSave() { return loginScreen.rememberMe(); }
	public Color[][][] getTimeSchadingModel() { return timeScreen.getShadingModel(); }
	
	public void submitClasses(ArrayList<ClassOption> classes, HashMap<ClassOption, ArrayList<Section>> preCollectedSections, HashMap<ClassOption, ArrayList<Boolean>> nonValid, boolean useSchedule) {
		final Scheduler_Tree scheduler = new Scheduler_Tree();
		
		new Thread(() -> {
			while(scheduler == null || scheduler.getStage() != Stage.Done) {
				if(scheduler != null) {
					Stage stage = scheduler.getStage();
					
					if(stage.isIndeterminate()) {
						showLoading(stage.getMessage() + "...");
					} else {
						showLoading(stage.getMessage() + 
								" [" + scheduler.getCurrent() + " / " + scheduler.getStageMax() + "]", 
								scheduler.getCurrent(), scheduler.getStageMax()
							);
					}
				} 
				
				try { Thread.sleep(10); } catch(InterruptedException e) { }
			}
		}, "Swing Updater Thread").start();
		
		new Thread(() -> {
			try {
				scheduler.run(classes, timeScreen.getRankings(), useSchedule, preCollectedSections, nonValid);
				if(scheduler.getSchedules().isEmpty())
					throw new SchedulingException("No Valid Schedules");
				
				displayScreen = new ScheduleDisplayScreen(Display.this, scheduler.getSchedules());
				contentPane.add(displayScreen, "displayScreen");

				((CardLayout) contentPane.getLayout()).show(contentPane, "displayScreen");
				
			} catch(SchedulingException e) {
				scheduler.canceled();
				contentPane.add(new ErrorScreen(Display.this, e.getMessage()), "error");
				((CardLayout) contentPane.getLayout()).show(contentPane, "error");
			}
		}, "Tree Scheduling Thread").start();
	}
	
	public SecretKeyUtil getVault() { return vault; }
}
