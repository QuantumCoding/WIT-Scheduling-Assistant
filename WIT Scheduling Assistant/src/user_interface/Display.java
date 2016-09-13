package user_interface;

import java.awt.CardLayout;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import pages.LookupResultsPage.LookupResult;
import scheduling.Schedule;
import scheduling.Scheduler;
import scheduling.SchedulingException;
import security.SecretKeyUtil;
import util.References;
import web_interface.PageLock;

public class Display extends JFrame {
	private static final long serialVersionUID = 4395497351445767482L;
	
	private JPanel contentPane;
	private LoginScreen loginScreen;
	private LoadingScreen loadingScreen;
	private AddClassScreen addClassScreen;
	private ScheduleDisplayScreen displayScreen;
	
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
		} catch(KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			e.printStackTrace();
		}
		
		loginScreen = new LoginScreen(pages, this);
		loadingScreen = new LoadingScreen();
		
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
	}
	
	public void switchToClassAdder() {
		((CardLayout) contentPane.getLayout()).show(contentPane, "addClassScreen");
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
		
		((CardLayout) contentPane.getLayout()).show(contentPane, "addClassScreen");
	}
	
	public void submitClasses(ArrayList<LookupResult> classes) {
		showLoading("Collecting Class Options...");
		scheduler = new Scheduler(pages, classes);
		new SchedulerTask().execute();
	}
	
	private class SchedulerTask extends SwingWorker<Void, Integer> {
		private boolean creationDone;
		private boolean trimingDone;
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
			
			creationDone = false; trimingDone = false;
			for(int i = 0; i < possibleScheduals; i ++) {
				scheduler.createSchedule(i);
				publish(i + 1);
			}

			try { Thread.sleep(500); } catch(InterruptedException e) {}
			creationDone = true; 
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
			trimingDone = true;
			
			for(int i = 0; i < schedules.size(); i ++) {
				scheduler.calculateVariants(schedules.get(i).getId());
				publish(i + 1);
			}

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
					if(trimingDone) {
						showLoading("Creating Varaints [" + 
							values.get(values.size() - 1) + " / " + scheduler.getSchedules().size() + "]",
							values.get(values.size() - 1), scheduler.getSchedules().size());  break;
							
					} else {
						showLoading((creationDone ? "Removing Invalid Schedules " : "Creating Scedule ")
								+ "[" + values.get(values.size() - 1) + " / " + scheduler.posible() + "]", 
								values.get(values.size() - 1), scheduler.posible());  break;
					}
			}
		}
		
		protected void done() {
			if(!creationDone)
				return;
			
			scheduler.cleanOut();
			System.out.println("Done");
			
			if(!error) {
				displayScreen = new ScheduleDisplayScreen(scheduler.getSchedules());
				contentPane.add(displayScreen, "displayScreen");
	
				((CardLayout) contentPane.getLayout()).show(contentPane, "displayScreen");
			}
		}
	}
	
	public SecretKeyUtil getVault() { return vault; }
}
