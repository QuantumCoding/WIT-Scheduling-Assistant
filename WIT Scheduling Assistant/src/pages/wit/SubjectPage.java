package pages.wit;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

import pages.Page;
import util.Choise;
import util.References;

public class SubjectPage extends Page {

	private ArrayList<Choise> subjects;
	
	private HtmlSelect subjSelecter;
	private HtmlSubmitInput search;
	
	public SubjectPage() throws IOException {
		super(makeRequest(new URL(References.Subject_URL), 
				Q("p_calling_proc", "P_CrseSearch"), Q("p_term", References.Current_Term)));
		
		HtmlForm form = super.formLookup(References.Subject_Form);
		
		subjSelecter = form.getSelectByName(References.Subject_Selector);
		search = form.getInputByValue(References.Subject_SearchButton);
		
		subjects = Choise.collectChoices(subjSelecter);
	}
	
	public LookupResultsPage findClasses(Choise... choises) throws IOException {
		ArrayList<Choise> remSubjects = new ArrayList<>(subjects);
		for(Choise choise : choises) {
			if(choise == null) continue;
			subjSelecter.setSelectedAttribute(choise.getOption(), true);
			remSubjects.remove(choise);
		}
		
		for(Choise choise : remSubjects)
			subjSelecter.setSelectedAttribute(choise.getOption(), false);
		
		return new LookupResultsPage(search.click());
	}

	public ArrayList<Choise> getSubjects() { return subjects; }
}
