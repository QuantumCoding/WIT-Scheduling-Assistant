package web_interface;

import com.gargoylesoftware.htmlunit.WebClient;

public class WebClientCreater {
	public static WebClient initWebClient() {
		WebClient webClient = new WebClient();
		
		webClient.getOptions().setSSLClientProtocols(new String[] { "TLSv1" });
		
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setPrintContentOnFailingStatusCode(false);
		
		webClient.getOptions().setRedirectEnabled(true);
		
		return webClient;
	}
}
