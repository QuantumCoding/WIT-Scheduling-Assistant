package pages_java;

import org.w3c.dom.Document;

import util.References;

public class LoginPage extends Page {
	
	public static State login(String username, String password) {
		return new LoginPage(username, password).getState();
	}

	public static enum State {
		Processing, Success, Failure;
	}
	
	private State state;
	
	private LoginPage(String username, String password) {
		super(References.Login_URL, username, password);
	}

	protected void init(Object[] args) {
		super.setValue(References.Login_Username_Path, (String) args[0]);
		super.setValue(References.Login_Password_Path, (String) args[1]);
		
		super.submitForm(References.Login_Form_Path, this::attemptResults);
	}
	
	private void attemptResults(Document doc) {
		state = doc.getElementsByTagName("title").item(0).getTextContent().equalsIgnoreCase("Sign In") ?
				State.Failure : State.Success;
		
		doneLoading();
	}
	
	public State getState() { return state == null ? State.Processing : state; }
}
