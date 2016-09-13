package web_interface;

import com.gargoylesoftware.htmlunit.WebClient;

import user_interface.Display;
import util.References;

public class WIT_Lancher {
	public static void main(String[] args) {
		new Thread(() -> {
			try(WebClient webClient = WebClientCreater.initWebClient()) {
				PageLock pages = new PageLock(webClient);
				Display display = new Display(pages);
				pages.setDisplay(display);
				
				while(true) {
					pages.cycle();
					try { Thread.sleep(10); }
					catch(InterruptedException e) {}
				}
			}
		}, References.Thread_Name).start();
	}
}
