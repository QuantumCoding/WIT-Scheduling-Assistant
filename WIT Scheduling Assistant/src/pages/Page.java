package pages;

import java.io.IOException;
import java.net.URL;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import util.Query;

public abstract class Page {
	protected static WebClient webClient;
	public static void setWebClient(WebClient webClient) {
		Page.webClient = webClient;
	}
	
	protected HtmlPage page;
	
	public Page(String url) throws IOException {
		page = webClient.getPage(url);
	}
	
	public Page(WebRequest request) throws IOException {
		page = webClient.getPage(request);
	}
	
	public Page(HtmlPage page) {
		this.page = page;
	}
	
	protected HtmlForm formLookup(String action) {
		return (HtmlForm) page.getByXPath("//form[@action='" + action + "' and @method='post']").get(0);
	}
	
	protected static WebRequest makeRequest(URL url, Query... queries) {
		WebRequest request = new WebRequest(url, HttpMethod.POST);
		
		String query = "";
		for(Query q : queries) {
			query += q + "&";
		}
		
		request.setRequestBody(query.substring(0, query.length() - 1));
		return request;
	}
	
	protected static Query Q(String key, String value) {
		return new Query(key, value);
	}
}
