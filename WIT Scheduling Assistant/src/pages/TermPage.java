package pages;

import java.io.IOException;
import java.util.ArrayList;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

import util.Choise;
import util.References;

public class TermPage extends Page {
	
	private ArrayList<Choise> terms;
	
	private HtmlSelect termSelector;
	private HtmlInput termSubmit;

	public TermPage() throws IOException {
		super(References.Term_URL);
		
		HtmlForm form = super.formLookup(References.Term_FormName);
		termSelector = form.getSelectByName(References.Term_SelectorName);
		termSubmit = form.getInputByValue(References.Term_SubmitButton);
		
		terms = Choise.collectChoices(termSelector, References.Term_Filter);
		
		if(terms.size() > 0)
			changeTerm(terms.get(0));
	}

	public HtmlPage changeTerm(Choise term) throws IOException {
		termSelector.setSelectedAttribute(term.getOption(), true);
		References.Current_Term = term.getValue();
		
		for(Choise choise : terms) {
			if(choise == term) continue;
			termSelector.setSelectedAttribute(choise.getOption(), false);
		}
		
		return termSubmit.click();
	}
	
	public ArrayList<Choise> getTerms() { return terms; }
}