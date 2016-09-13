package web_interface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import pages.LoginPage;
import pages.LookupResultsPage;
import pages.LookupResultsPage.LookupResult;
import pages.Page;
import pages.SubjectPage;
import pages.TermPage;
import scheduling.Section;
import user_interface.Display;
import util.Choise;
import util.References;

public class PageLock {
	private LoginPage loginPage;
	
	private TermPage termPage;
//	private RegisterPage registerPage;
	private SubjectPage subjectPage;
	
	private volatile boolean login;
	private volatile String loginUsername, loginPassword;
	private volatile HtmlPage loginReturn;
	
	private volatile boolean loginComplate;
	
	private volatile Choise newTerm;
	private volatile boolean changeTerm;
	
	private volatile Choise subject;
	private volatile LookupResultsPage results;
	private volatile boolean lookupClasses;
	
	private volatile ArrayList<LookupResult> classes;
	private volatile HashMap<LookupResult, ArrayList<Section>> options;
	private volatile boolean collectSections;
	
//	private volatile int[] registerClasses;
//	private volatile SchedulingException[] registerErrors;
//	private volatile boolean register;
	
	private Object sycn;
	private boolean prossesing;
	
	private Display display;
	private boolean loginDisplay;
	
	public PageLock(WebClient webClient) {
		sycn = new Object();
		
		Page.setWebClient(webClient);
		
		try {
			loginPage = new LoginPage();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setDisplay(Display display) { this.display = display; }
	
	protected void cycle() {
		prossesing = true;
		
		try {
			if(login) {
				loginReturn = loginPage.login(loginUsername, loginPassword);
				login = false;
			}
			
			if(loginComplate) {
				this.termPage = new TermPage();
				subjectPage = new SubjectPage();
//				registerPage = new RegisterPage();
				
				loginDisplay = true;
				loginComplate = false;
			}
			
			if(loginDisplay) {
				display.loginComplete();
				loginDisplay = false;
			}
			
			if(changeTerm) {
				termPage.changeTerm(newTerm);
				subjectPage = new SubjectPage();
//				registerPage = new RegisterPage();
				changeTerm = false;
			}
			
			if(lookupClasses) {
				results = subjectPage.findClasses(subject);
				lookupClasses = false;
			}
			
			if(collectSections) {
				options = new HashMap<>();
				
				for(LookupResult result : classes) {
					try { options.put(result, result.select().getViableSections()); } 
					catch(IOException e) { e.printStackTrace(); }
				}
				
				collectSections = true;
			}
			
//			if(register) {
//				registerErrors = registerPage.addClasses(registerClasses);
//				register = false;
//			}
			
			prossesing = false;
			
			synchronized(sycn) { sycn.notifyAll(); }
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void waitForOpening() {
		if(prossesing) {
			synchronized(sycn) {
				try { sycn.wait(); } 
				catch(InterruptedException e) { }
			}
		}
	} 
	
	public HtmlPage login(String username, String password) {
		waitForOpening();
		
		loginUsername = username;
		loginPassword = password;
		login = true;
		
		synchronized(sycn) {
			try { sycn.wait(); } 
			catch(InterruptedException e) { return null; }
		}
		
		return loginReturn;
	}
	
	public void loginComplete() { this.loginComplate = true; }
	
	public ArrayList<Choise> getTerms() { return termPage.getTerms(); }
	
	public void changeTerm(Choise newTerm) {
		waitForOpening();
		
		this.newTerm = newTerm; changeTerm = true;

		synchronized(sycn) {
			try { sycn.wait(); } 
			catch(InterruptedException e) { }
		}
	}
	
	public ArrayList<Choise> getSubjects() {
		return subjectPage.getSubjects();
	}
	
	public ArrayList<LookupResult> getClasses(Choise subject) {
		if(Thread.currentThread().getName().equals(References.Thread_Name)) {
			try {
				results = subjectPage.findClasses(subject);
				return results.getClasses(results.getSubjects().iterator().next());
			} catch(IOException e) { e.printStackTrace(); }
			
			return null;
		}
		
		waitForOpening();
		
		this.subject = subject;
		lookupClasses = true;
		
		synchronized(sycn) {
			try { sycn.wait(); } 
			catch(InterruptedException e) { return null; }
		}
		
		return results.getClasses(results.getSubjects().iterator().next());
	}
	
	public HashMap<LookupResult, ArrayList<Section>> collectSections(ArrayList<LookupResult> classes) {
		waitForOpening();
		
		this.classes = classes; 
		collectSections = true;

		synchronized(sycn) {
			try { sycn.wait(); } 
			catch(InterruptedException e) { return null; }
		}
		
		return options;
	}
	
//	public SchedulingException[] register(int... registerClasses) {
//		waitForOpening();
//		
//		this.registerClasses = registerClasses; 
//		register = true;
//
//		synchronized(sycn) {
//			try { sycn.wait(); } 
//			catch(InterruptedException e) { return null; }
//		}
//		
//		return registerErrors;
//	}
}