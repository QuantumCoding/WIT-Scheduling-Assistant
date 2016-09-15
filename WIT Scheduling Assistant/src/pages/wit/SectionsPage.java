package pages.wit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import pages.Page;
import pages.wit.LookupResultsPage.LookupResult;
import scheduling.Section;

public class SectionsPage extends Page {
	
	private LookupResult result;
	private ArrayList<Section> sections;
	private ArrayList<Section> viableSections;
	
	public SectionsPage(LookupResult result, HtmlPage page) throws IOException {
		super(page);
	
		this.result = result;
		this.sections = new ArrayList<>();
		
		HtmlTable sectionsTable = (HtmlTable) page.getByXPath("//table[@class='datadisplaytable']").get(0);
		
		List<HtmlTableBody> bodies = sectionsTable.getBodies();
		List<HtmlTableRow> rows = new ArrayList<>();
		for(HtmlTableBody body : bodies) 
			rows.addAll(body.getRows());
		
		sections = new ArrayList<>();
		viableSections = new ArrayList<>();
		
		if(rows.size() < 2) 
			return;
		
		Section prevSection = null;
		for(int i = 2; i < rows.size(); i ++) {
			Section section = new Section(prevSection, result, rows.get(i));
			
			if(section.getLabs() != null)
				prevSection = section;
			
			if(section.getLabs() != null || prevSection == null) {
				sections.add(section);
				
				if(section.isViable())
					viableSections.add(section);
			}
		}
	}

	public ArrayList<Section> getViableSections() { return viableSections; }
	public ArrayList<Section> getSections() { return sections; }
	public LookupResult getResultsPage() { return result; }
}
