package pages;

import java.io.IOException;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import util.References;

public class LoginPage extends Page {

	private HtmlTextInput userId;
	private HtmlPasswordInput password;
	private HtmlSubmitInput loginButton;
	
	public LoginPage() throws IOException {
		super(References.Login_URL);
		
		HtmlForm form = (HtmlForm) page.getElementById(References.Login_FormId);

		userId = form.getInputByName(References.Login_Username);
		password = form.getInputByName(References.Login_Password);
		
		loginButton = form.getInputByValue(References.Login_SignInButton);
	}

	public HtmlPage login(String usernameVal, String passwordVal) throws IOException {
		userId.setValueAttribute(usernameVal);
		password.setValueAttribute(passwordVal);
		
		return loginButton.click();
	}
}
