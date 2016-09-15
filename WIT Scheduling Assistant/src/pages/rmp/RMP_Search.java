package pages.rmp;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlUnorderedList;

import pages.Page;
import scheduling.Campus;

public class RMP_Search extends Page {
	
	private RMP_Search() throws IOException { super((String) null); }

	public static float search(String professor, Campus campus) throws IOException {
		HtmlPage page = Page.webClient.getPage("http://www.ratemyprofessors.com/search.jsp?query=" + professor.substring(professor.lastIndexOf(" ") + 1));
		
		HtmlUnorderedList professorResultsList = (HtmlUnorderedList) page.getByXPath("//ul[@class='listings']").get(0);
		List<HtmlElement> listItems = professorResultsList.getElementsByTagName("li");
		for(HtmlElement element : listItems) {
			HtmlListItem item = (HtmlListItem) element;
			
			HtmlAnchor link = (HtmlAnchor) item.getElementsByTagName("a").get(0);
			@SuppressWarnings("unchecked")
			String cat = ((List<HtmlElement>) link.getByXPath("//span[@class='listing-cat']")).get(0).getTextContent().trim();
			if(!cat.toUpperCase().contains("PROFESSOR")) continue;
			
			@SuppressWarnings("unchecked")
			String unv = ((List<HtmlElement>)((List<HtmlElement>) link.getByXPath("//span[@class='listing-name']")).get(0).
							getByXPath("//span[@class='sub']")).get(0).getTextContent().split(",")[0];
			if(!unv.equalsIgnoreCase(campus.name())) continue;
			
			HtmlPage results = webClient.getPage("http://www.ratemyprofessors.com" + link.getHrefAttribute());
			@SuppressWarnings("unchecked")
			String rating = ((List<HtmlElement>) results.getByXPath("//div[@class='grade']")).get(0).getTextContent().trim();
			
			return Float.parseFloat(rating) / 5;
		}
		
		return 0.5f;
	}
}
