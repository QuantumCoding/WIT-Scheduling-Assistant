import java.io.IOException;

import com.gargoylesoftware.htmlunit.WebClient;

import pages.LoginPage;
import pages.Page;
import pages.SchedulePage;
import pages.TermPage;
import web_interface.WebClientCreater;

public class PageTester {
	public static void main(String[] args) throws IOException {
		try(WebClient w = WebClientCreater.initWebClient()) {
			Page.setWebClient(w);
			
			LoginPage loginPage = new LoginPage();
			loginPage.login("cilfonej", "#Cbmuku35");
			
			new SchedulePage();
			/*TermPage termPage = */new TermPage();
//			System.out.println(termPage.changeTerm(termPage.getTerms().get(0)).getTitleText());

//			RegisterPage registerPage = new RegisterPage();
//			SchedulingException[] errors = registerPage.addClasses(11556);
//			for(SchedulingException error : errors) {
//				System.err.println(error.getMessage());
//			}
			
//			SubjectPage lookupPage = new SubjectPage();
			
//			Scanner scan = new Scanner(System.in);
			
//			LookupResultsPage results = lookupPage.findClasses(lookupPage.getSubjects().get(8));//, lookupPage.getSubjects().get(scan.nextInt()));
//			ArrayList<LookupResult> classes = results.getClasses(results.getSubjects().iterator().next());
//			
//			SectionsPage sectionPage = classes.get(0).select();
//			ArrayList<Section> sections = sectionPage.getSections();
//
//			System.out.println(sections.get(0));
//			System.out.println(sections.get(12));
//			
//			System.out.println(sections.get(0).findValidConfigurations(sections.get(12)));
//			
//			
//			LookupResultsPage resultsPage = lookupPage.findClasses(lookupPage.getSubjects().toArray(new Choise[lookupPage.getSubjects().size()]));
			
//			Choise[] choices = lookupPage.getSubjects().toArray(new Choise[lookupPage.getSubjects().size()]);
//			for(Choise choice : choices) System.out.println(choice);
//			
//			for(Choise choise : lookupPage.getSubjects()) {
//				LookupResultsPage resultsPage = lookupPage.findClasses(choise);
				
//				LocalTime latest = LocalTime.now();
//			
//				for(String subject : resultsPage.getSubjects()) { System.out.println(subject);
//				for(LookupResult clazz : resultsPage.getClasses(subject)) {
//				for(Section section : clazz.select().getSections()) {
//				for(Designation designation : section.getDesignations()) {
//					if(designation.getPeriod().getEndTime() != null)
//					if(designation.getPeriod().getEndTime().isAfter(latest)) {
//						latest = designation.getPeriod().getEndTime();
//						System.out.println(section.getClassName());
//					}
//				}}}}
//				
//				System.out.println(latest);
				
//			}
				
//			scan.close();
		} catch(Exception e) {			
			e.printStackTrace();
		}
	}
}
