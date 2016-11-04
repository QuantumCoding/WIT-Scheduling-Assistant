import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebEngine;
import pages_java.Choise;
import pages_java.DepartmentClassesPage;
import pages_java.DepartmentSelectPage;
import pages_java.LoginPage;
import pages_java.Page;
import pages_java.TermPage;

public class PrueJavaTester {
	private static WebEngine webEngine;
	
	public static void main(String[] args) {
    	JFrame frame = new JFrame();
		frame.add(new JFXPanel());
		frame.dispose();
		
		Platform.runLater(() -> {
			try {
				webEngine = new WebEngine();
				
				SSLContext sc = SSLContext.getInstance("TLSv1"); 
			    sc.init(null, null, new SecureRandom()); 
			    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			    
			    Page.setWebEngine(webEngine);
			} catch(KeyManagementException | NoSuchAlgorithmException e) {
				
			}
		});

	    System.out.println("End Result: " + LoginPage.login("cilfonej", "#Cbmuku35"));
	    
	    Choise term = TermPage.getTerms().get(1);
	    
	    Choise deparment = DepartmentSelectPage.getDepartments(term).get(0);
	    DepartmentClassesPage classes = DepartmentSelectPage.selectDepartment(term, deparment);
	    
	    System.out.println(classes.selectClass(classes.getClassList().get(0)).getSections());
	    System.out.println(DepartmentSelectPage.selectClass(term, deparment, classes.getClassList().get(1)).getSections());
	    
	    System.exit(0);
	}
}
