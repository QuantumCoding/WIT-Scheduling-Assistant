package web_interface;

import java.util.logging.Logger;

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
		
		Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
		Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);

		return webClient;
	}
}
