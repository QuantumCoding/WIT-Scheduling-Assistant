package pages_java;

import java.util.ArrayList;

import org.w3c.dom.Element;

import util.References;

public class TermPage extends Page {
	private static ArrayList<Choise> terms;
	
	public static void clearTermCashe() { terms = null; }
	
	public static ArrayList<Choise> getTerms() {
		if(terms == null)
			terms = new TermPage().collectedTerms;
		return terms;
	}
	
//	------------------------------------------------------------------------------------- \\
	
	private Element termSelector;
	private ArrayList<Choise> collectedTerms;
	
	private TermPage() {
		super(References.Term_URL);
	}

	protected void init(Object[] args) {
		termSelector = super.getByXPath(References.Term_Selector_Path);
		
		collectChoices();
		super.doneLoading();
	}
	
	private void collectChoices() {
		collectedTerms = Choise.collectChoices(termSelector);
	}
}
