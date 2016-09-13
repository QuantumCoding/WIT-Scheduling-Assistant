
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class Tester {
	
	private static WebClient webClient;
	
	public static void main(String[] args) {
		System.out.println("Start");
		long current = System.currentTimeMillis();
		
		try(WebClient w = initWebClient()) {
			Tester.webClient = w;
			
			login("cilfonej", "#Cbmuku35");
			selectTerm(getTerms().get(0));
			
			HtmlPage requestPage = webClient.getPage(getRequest(new URL("https://prodweb2.wit.edu/SSBPROD/bwckgens.p_proc_term_date"), 
					Q("p_calling_proc", "P_CrseSearch"), Q("p_term", "201710")));
			
			HtmlForm form = (HtmlForm) requestPage.getByXPath("//form[@action='/SSBPROD/bwskfcls.P_GetCrse' and @method='post']").get(0);
			
			HtmlSelect deptSelecter = form.getSelectByName("sel_subj");
			HtmlSubmitInput search = form.getInputByValue("Course Search");
			
			deptSelecter.setSelectedAttribute("COMP", true);
			deptSelecter.setSelectedAttribute("CIVE", true);
			HtmlPage p = search.click();
			
			System.out.println(p.asXml().contains("COMPUTER SCIENCE I"));
			
			@SuppressWarnings("unchecked")
			List<HtmlElement> classes = (List<HtmlElement>) p.getByXPath("//td[@class='dddefault' and @width='10%']");
			for(HtmlElement element : classes) {
				DomElement next = element.getNextElementSibling();
				if(next == null) {
					System.err.println(element.getTextContent());
					continue;
				}
				
				System.out.println(element.getTextContent() + ": " + next.getTextContent());
			}
//			
//			System.out.println();
//			System.out.println(coursePage.getTitleText());
//			System.out.println(coursePage.asXml());
			
//			HtmlOption term = getClassTerms().get(0);
//			HtmlPage departmentPage = changeClassLookUpTerm(term);
//			System.out.println(departmentPage.getTitleText());
			
//			System.out.println(departmentPage.asXml());
			
//			List<HtmlOption> departments = getDepartments(departmentPage);
//			HtmlPage coursesPage = selectDepartments(departmentPage, departments, departments.get(0));
//			
//			System.out.println(coursesPage.getTitleText());
			
//			System.out.println();
//			HtmlPage page2 = webClient.getPage("https://prodweb2.wit.edu/SSBPROD/bwckgens.p_proc_term_date");
//			System.out.println(page2.getTitleText());
//			System.out.println(page2.asXml());

			System.out.println("Time Taken: " + (double)(System.currentTimeMillis() - current) / 1000);
		} catch(FailingHttpStatusCodeException | IOException | IndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		
	}
	
	public static class Query {
		public String key;
		public String value;
		
		public Query(String key, String value) {
			this.key = key;
			this.value = value;
		}
		
		public String toString() {
			return key + "=" + value;
		}
	}
	
	public static Query Q(String key, String value) {
		return new Query(key, value);
	}
	
	public static WebRequest getRequest(URL url, Query... queries) {
		WebRequest request = new WebRequest(url, HttpMethod.POST);
		
		
//		request.setAdditionalHeader("Origin", "https://prodweb2.wit.edu");			
//		request.setAdditionalHeader("Upgrade-Insecure-Requests", "1");			
////		request.setAdditionalHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36");			
//		request.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");			
////		request.setAdditionalHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");	
////		request.setAdditionalHeader("Referer", "https://prodweb2.wit.edu/SSBPROD/bwskfcls.p_sel_crse_search");		
//		request.setAdditionalHeader("Accept-Encoding", "gzip, deflate, br");			
//		request.setAdditionalHeader("Accept-Language", "en-US,en;q=0.8");			
////		request.setAdditionalHeader("Cookie", "SESSID=MEtNNldJMzY5Nzkx; tc_ptid=1LguiZ1vz68QaIIgkkGC0g; tc_ptidexpiry=1535305602353; _ga=GA1.2.1766851083.1450719697; tc_q=; IDMSESSID=2883BE0FC9F97C9FE053CBFA080A4728");			
		
		request.setAdditionalHeader("Origin", "https://prodweb2.wit.edu");			
		request.setAdditionalHeader("Upgrade-Insecure-Requests", "1");			
		request.setAdditionalHeader("Content-Type", "application/x-www-form-urlencoded");	
		request.setAdditionalHeader("Accept-Encoding", "gzip, deflate, br");			
		request.setAdditionalHeader("Accept-Language", "en-US,en;q=0.8");	
		
		String query = "";
		for(Query q : queries) {
			query += q + "&";
		}
		
		request.setRequestBody(query.substring(0, query.length() - 1));
		return request;
	}
	
	public static WebClient initWebClient() {
		WebClient webClient = new WebClient();
		
		webClient.getOptions().setSSLClientProtocols(new String[] {"TLSv1" });
		
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setPrintContentOnFailingStatusCode(false);
		
		webClient.getOptions().setRedirectEnabled(true);
		
		return webClient;
	}
	
	public static void login(String usernameVal, String passwordVal) throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = webClient.getPage("https://prodweb2.wit.edu/ssomanager/c/SSB");
		System.out.println(page.getTitleText());
		
		HtmlForm form = (HtmlForm) page.getElementById("fm1");

		HtmlTextInput userId = form.getInputByName("username");
		HtmlPasswordInput password = form.getInputByName("password");
		
		HtmlSubmitInput loginButton = form.getInputByValue("Sign In");
		
		userId.setValueAttribute(usernameVal);
		password.setValueAttribute(passwordVal);
		
		loginButton.click();
	}
	
	public static List<HtmlOption> collectSelectOptions(HtmlSelect selecter, String... filters) {
		ArrayList<HtmlOption> options = new ArrayList<>(selecter.getOptions());
		
		for(String filter : filters) {
			ListIterator<HtmlOption> iter = options.listIterator();
			
			while(iter.hasNext()) {
				HtmlOption option = iter.next();
				if(option.getText().contains(filter))
					iter.remove();
			}
		}
		
		return options;
	}
	
	public static List<HtmlOption> getTerms() throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = webClient.getPage("https://prodweb2.wit.edu/SSBPROD/bwskflib.P_SelDefTerm");
		HtmlForm form = (HtmlForm) page.getByXPath("//form[@action='/SSBPROD/bwcklibs.P_StoreTerm' and @method='post']").get(0);
		HtmlSelect termSelecter = form.getSelectByName("term_in");
		
		return collectSelectOptions(termSelecter, "(View only)");
	}
	
	public static void selectTerm(HtmlOption term) throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = webClient.getPage("https://prodweb2.wit.edu/SSBPROD/bwskflib.P_SelDefTerm");
		HtmlForm form = (HtmlForm) page.getByXPath("//form[@action='/SSBPROD/bwcklibs.P_StoreTerm' and @method='post']").get(0);
		
		HtmlSelect termSelecter = form.getSelectByName("term_in");
		HtmlSubmitInput submit = form.getInputByValue("Submit");
		
		termSelecter.setSelectedAttribute(term, true);
		submit.click();
	}
	
	public static List<HtmlOption> getClassTerms() throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = webClient.getPage("https://prodweb2.wit.edu/SSBPROD/bwskfcls.p_sel_crse_search");
		HtmlForm form = (HtmlForm) page.getByXPath("//form[@action='/SSBPROD/bwckgens.p_proc_term_date' and @method='post']").get(0);
		HtmlSelect termSelecter = form.getSelectByName("p_term");
		
		return collectSelectOptions(termSelecter, "(View only)");
	}
	
	public static HtmlPage changeClassLookUpTerm(HtmlOption term) throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = webClient.getPage("https://prodweb2.wit.edu/SSBPROD/bwskfcls.p_sel_crse_search");
		HtmlForm form = (HtmlForm) page.getByXPath("//form[@action='/SSBPROD/bwckgens.p_proc_term_date' and @method='post']").get(0);
		
		HtmlSelect termSelecter = form.getSelectByName("p_term");
		HtmlSubmitInput submit = form.getInputByValue("Submit");
		System.out.println(submit);

		termSelecter.setSelectedAttribute(term, true);
		return submit.click();
	}
	
	public static List<HtmlOption> getDepartments(HtmlPage page) throws FailingHttpStatusCodeException, IOException {
		HtmlForm form = (HtmlForm) page.getByXPath("//form[@action='/SSBPROD/bwskfcls.P_GetCrse' and @method='post']").get(0);
		HtmlSelect deptSelecter = form.getSelectByName("sel_subj");
		
		return collectSelectOptions(deptSelecter);
	}
	
	public static HtmlPage selectDepartments(HtmlPage page, List<HtmlOption> allDept, HtmlOption... departments) throws FailingHttpStatusCodeException, IOException {
		HtmlForm form = (HtmlForm) page.getByXPath("//form[@action='/SSBPROD/bwskfcls.P_GetCrse' and @method='post']").get(0);
		
		HtmlSelect deptSelecter = form.getSelectByName("sel_subj");
		HtmlSubmitInput search = form.getInputByValue("Course Search");
		
		if(allDept == null)
			allDept = collectSelectOptions(deptSelecter);
		
		for(HtmlOption department : departments) {
			deptSelecter.setSelectedAttribute(department, true);
			allDept.remove(department);
		}
		
		for(HtmlOption department : allDept)
			deptSelecter.setSelectedAttribute(department, false);
		
		return search.click();
	}
	
	public static void classLookUp() {
		
	}
}
