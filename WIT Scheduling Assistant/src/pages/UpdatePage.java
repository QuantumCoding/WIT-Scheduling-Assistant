package pages;

public class UpdatePage extends Page {
	private static final String VERSION_PAGE_URL = "https://quantumcoding.github.io/WIT-Scheduling-Assistant/";

	public static final String CURRENT_VERSION = "current";
	
	public static String getDownloadLink(String version) {
		return new UpdatePage(version).link;
	}
	
	private String link;
	
	protected UpdatePage(String version) {
		super(VERSION_PAGE_URL, version);
	}

	protected void init(Object[] args) {
		link = (String) getAttribute("//a[text()='" + (String) args[0] + "']", "href");
		super.doneLoading();
	}
}
