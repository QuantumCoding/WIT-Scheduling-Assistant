import javax.swing.JFrame;

import javafx.embed.swing.JFXPanel;
import pages.Choise;
import pages.DepartmentClassesPage;
import pages.DepartmentSelectPage;
import pages.LoginPage;
import pages.TermPage;

public class PrueJavaTester {
	public static void main(String[] args) {
    	JFrame frame = new JFrame();
		frame.add(new JFXPanel());
		frame.dispose();
		
	    System.out.println("End Result: " + LoginPage.login("cilfonej", "#Cbmuku35"));
	    
	    Choise term = TermPage.getTerms().get(1);
	    
	    Choise deparment = DepartmentSelectPage.getDepartments(term).get(8);
	    System.out.println(deparment);
	    DepartmentClassesPage classes = DepartmentSelectPage.selectDepartment(term, deparment);
	    
	    System.out.println(classes.getClassList().get(1));
	    System.out.println(classes.selectClass(classes.getClassList().get(1)).getSections());
//	    System.out.println(DepartmentSelectPage.selectClass(term, deparment, classes.getClassList().get(1)).getSections());
	    
	    System.exit(0);
	}
}
