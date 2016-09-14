package pages;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import pages.LookupResultsPage.LookupResult;
import scheduling.Campus;
import scheduling.Designation;
import scheduling.Schedule;
import scheduling.Section;
import util.References;

public class SchedulePage extends Page {
	private Schedule schedule;
	
	public SchedulePage() throws IOException {
		super(Page.makeRequest(new URL("https://prodweb2.wit.edu/SSBPROD/bwskfshd.p_proc_crse_schd"),
				Q("goto_date_in", String.format("%1$tm/%1$td/%1$tY", LocalDate.now()))));
		
		HtmlTable table = (HtmlTable) page.getByXPath("//table[@class='datadisplaytable']").get(0);
		List<HtmlElement> classLinks = table.getElementsByTagName("a");
		
		ArrayList<ClassPage> pages = new ArrayList<>();
		for(HtmlElement element : classLinks) {
			ClassPage classP = new ClassPage(((HtmlAnchor) element).getHrefAttribute());
			pages.add(classP);
		}
		
		ArrayList<Section> sections = new ArrayList<>();
		for(ClassPage labPage : pages) {
			if(labPage.isLab) {
				Section section = labPage.makeSection();
				sections.add(section);
				
				for(ClassPage checkPage : pages) {
					if(checkPage == labPage) continue; 
					
					if(checkPage.data[0].equals(labPage.data[0])) {
						if(checkPage.labs == null)
							checkPage.labs = new ArrayList<>();
						
						checkPage.labs.add(section);
					}
				}
			}
		}
		
		secPageLoop:
		for(ClassPage secPage : pages) {
			if(secPage.isLab) continue;
			
			for(Section section : sections)
				if(section.getCourseNumber() == Integer.parseInt(secPage.data[4]))
					continue secPageLoop;
			
			sections.add(secPage.makeSection());
		}
		
		schedule = new Schedule(sections);
	}

	public Schedule getSchedule() { return schedule; }
	
	private static class ClassPage extends Page {

		private String[] data;
		private ArrayList<Designation> designations;
		private boolean isLab;
		
		private ArrayList<Section> labs;
		
		public ClassPage(String url) throws IOException {
			super(References.BaseUrl + url);
			
			data = new String[8];
			designations = new ArrayList<>();
			
			HtmlTable bigTable = (HtmlTable) page.getByXPath("//table[@class='datadisplaytable']").get(0);
			String titleLine = bigTable.getElementsByTagName("caption").get(0).getTextContent().trim();
			
			int index = titleLine.indexOf("-");
			data[0] = titleLine.substring(0, index).trim(); // Class Name
			titleLine = titleLine.substring(index + 1).trim();
			
			index = titleLine.indexOf("-");
			String loi = titleLine.substring(0, index).trim(); // Lab or Info
			
			if(loi.toLowerCase().startsWith("lab")) {
				isLab = true;
				index = titleLine.indexOf("-");
				titleLine = titleLine.substring(index + 1).trim();
				index = titleLine.indexOf("-");
			} 
			
			String clsTypeAndNumber = titleLine.substring(0, index).trim();
			index = clsTypeAndNumber.indexOf(" ");
			data[1] = clsTypeAndNumber.substring(0, index); // Class Type
			data[2] = clsTypeAndNumber.substring(index + 1); // Class Number
			
			index = titleLine.indexOf("-");
			titleLine = titleLine.substring(index + 1);
			
			data[3] = titleLine.substring(0, titleLine.length()).trim(); // Section Number
			
			HtmlTableBody bigBody = bigTable.getBodies().get(0);
			
			data[4] = bigBody.getRows().get(1).getElementsByTagName("td").get(0).getTextContent().trim(); // Course Number
			data[5] = bigBody.getRows().get(3).getElementsByTagName("td").get(0).getTextContent().trim(); // Professor 
			data[6] = bigBody.getRows().get(5).getElementsByTagName("td").get(0).getTextContent().trim(); // Credits
			data[7] = bigBody.getRows().get(7).getElementsByTagName("td").get(0).getTextContent().trim(); // Campus
			
			HtmlTable locTable = (HtmlTable) page.getByXPath("//table[@class='datadisplaytable']").get(1);
			List<HtmlTableRow> rows = locTable.getBodies().get(0).getRows();
			
			for(int i = 1; i < rows.size(); i ++) {
				HtmlTableRow row = rows.get(i);
				List<HtmlElement> units = row.getElementsByTagName("td");
				
				String[] destPart = units.get(3).getTextContent().trim().split(" ");
				
				designations.addAll(Designation.parse(Campus.getByName(data[7]), 
						destPart[0] + " " + destPart[destPart.length - 1], 
						units.get(2).getTextContent().trim(), 
						units.get(1).getTextContent().trim()
					));
			}
		}
		
		private static class SudoLookupResult extends LookupResult {

			public SudoLookupResult(String subject, String className, String classId) {
				super.subject = subject;
				super.className = className;
				super.classId = classId;
			}
			
			public SectionsPage select() {
				throw new IllegalAccessError("Cannot Select a SudoLookupResult!");
			}
		}
		
		public Section makeSection() {
			return Section.parse(new SudoLookupResult(data[1], data[0], data[2]), data[0], data[2], Integer.parseInt(data[4]),  
					data[3], Double.parseDouble(data[6]), designations, data[5], isLab, labs);
		}
	}
}
