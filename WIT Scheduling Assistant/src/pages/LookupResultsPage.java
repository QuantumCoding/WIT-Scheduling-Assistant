package pages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import util.References;

public class LookupResultsPage extends Page {
	
	private HashMap<String, ArrayList<LookupResult>> classes; 

	public LookupResultsPage(HtmlPage page) {
		super(page);
		
		@SuppressWarnings("unchecked")
		List<HtmlTable> tables = (List<HtmlTable>) page.getByXPath("/html//body//div[@class='pagebodydiv']//table[@class='datadisplaytable']");
		classes = new HashMap<>();
		
		int formIndex = 0;
		for(HtmlTable table : tables) {
			
			List<HtmlTableBody> bodies = table.getBodies();
			List<HtmlTableRow> rows = new ArrayList<>();
			for(HtmlTableBody body : bodies) 
				rows.addAll(body.getRows());
			
			if(rows.size() < 2) 
				continue;

			String subject = rows.get(1).getTextContent().trim();
			ArrayList<LookupResult> results;
			classes.put(subject, results = new ArrayList<>());
			
			@SuppressWarnings("unchecked")
			List<HtmlForm> forms = (List<HtmlForm>) page.getByXPath("//form[@action='" + References.Lookup_Result_Form + "']");
			for(int i = 2; i < rows.size(); i ++)
				results.add(new LookupResult(forms.get(formIndex ++), subject, rows.get(i)));
		}
	}
	
	public Set<String> getSubjects() { return classes.keySet(); }
	
	public ArrayList<LookupResult> getClasses(String subject) {
		return classes.get(subject);
	}
	
	public static class LookupResult {
		private String subject;
		private String className;
		private String classId;
		
		private HtmlSubmitInput select;
		
		public LookupResult(HtmlForm form, String subject, HtmlTableRow row) {
			this.subject = subject;
			
			List<HtmlElement> cells = row.getElementsByTagName("td");
			if(cells.size() < 3) throw new IllegalArgumentException("TR must have 3 TD: (ClassId, ClassName, Submit)");
			
			classId = cells.get(0).getTextContent();
			className = cells.get(1).getTextContent();
			
			select = form.getInputByName(References.Lookup_Result_Select);
		}

		public String toString() { return getClassName(); }
		public String getClassName() { return className; }
		public String getClassId() { return classId; }
		public String getSubject() { return subject; }

		public SectionsPage select() throws IOException {
			return new SectionsPage(this, select.click());
		}
	}
}
