package user_interface.screens;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;
import pages.LoginPage;
import pages.LoginPage.State;
import security.SecretKeyUtil;
import user_interface.Display;
import util.Fonts;
import util.References;

public class LoginScreen extends JPanel implements ActionListener {
	private static final long serialVersionUID = 7732473592531545896L;
	
	private JTextField passwordInput;
	private JTextField usernameInput;

	@SuppressWarnings("unused")
	private final JLabel lblnoteusernameAndPassword = new JLabel("<HTML><B>Note:</B><BR>Username and Password are used to Connect to \"prodweb2.wit.edu\" <BR>Information is transfered over a secure connection to the Site <BR>No user information will be saved or collected unless specified <BR></HTML>");
	
	private JButton logingButton;
	private JButton closeButton;
	private JCheckBox rememberMeCheckBox;
	
	private JLabel errorLabel;

	private Display display;
	
	public LoginScreen(Display display) {
		this.display = display;
		
		setBackground(Color.WHITE);
		setLayout(new MigLayout("", "[][grow,fill][]", "[][grow,fill][]"));
		
		JPanel loginPanel = new JPanel();
		add(loginPanel, "cell 1 1");
		loginPanel.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		loginPanel.setBackground(Color.WHITE);
		loginPanel.setLayout(new MigLayout("", "[][grow][]", "[][][][][][25px][]"));
		
		JLabel witHeaderLabel = new JLabel("");
		witHeaderLabel.setIcon(References.Icon_WIT_Header);
		loginPanel.add(witHeaderLabel, "cell 0 0 3 1");
		
		errorLabel = new JLabel("Invalid Username and Password Pair!");
		errorLabel.setFont(Fonts.MEDIUM_LABEL_BOLD);
		errorLabel.setForeground(Color.RED);
		loginPanel.add(errorLabel, "cell 0 1 3 1,alignx center");
		
		JLabel usernameLabel = new JLabel("Username: ");
		loginPanel.add(usernameLabel, "cell 0 2,alignx right,aligny center");
		usernameLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		usernameLabel.setFont(Fonts.MEDIUM_LABEL);
		
		usernameInput = new JTextField();
		usernameInput.setBackground(SystemColor.menu);
		loginPanel.add(usernameInput, "cell 1 2 2 1,growx,aligny top");
		usernameInput.setFont(Fonts.MEDIUM_LABEL);
		usernameInput.setColumns(20);
		
		JLabel passwordLabel = new JLabel("Password: ");
		loginPanel.add(passwordLabel, "flowx,cell 0 3,alignx right,aligny center");
		passwordLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		passwordLabel.setFont(Fonts.MEDIUM_LABEL);
		
		passwordInput = new JPasswordField();
		passwordInput.setBackground(SystemColor.menu);
		loginPanel.add(passwordInput, "cell 1 3 2 1,growx,aligny top");
		passwordInput.setFont(Fonts.MEDIUM_LABEL);
		passwordInput.setColumns(20);
		
		rememberMeCheckBox = new JCheckBox("Remember Me");
		rememberMeCheckBox.setBackground(Color.WHITE);
		rememberMeCheckBox.setFont(Fonts.TINY_LABEL);
		loginPanel.add(rememberMeCheckBox, "cell 2 4,alignx right,aligny top");
		rememberMeCheckBox.setHorizontalAlignment(SwingConstants.TRAILING);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.WHITE);
		loginPanel.add(buttonPanel, "cell 1 6 2 1,alignx right,aligny bottom");
		buttonPanel.setLayout(new GridLayout(0, 2, 5, 0));
		
		logingButton = new JButton("Login");
		logingButton.setBackground(Color.WHITE);
		logingButton.setFont(Fonts.MEDIUM_LABEL);
		buttonPanel.add(logingButton);
		
		closeButton = new JButton("Close");
		closeButton.setBackground(Color.WHITE);
		closeButton.setFont(Fonts.MEDIUM_LABEL);
		buttonPanel.add(closeButton);
		
		closeButton.addActionListener(this);
		logingButton.addActionListener(this);
		usernameInput.addActionListener(this);
		passwordInput.addActionListener(this);
		
		errorLabel.setVisible(false);
		
		try {
			SecretKeyUtil vault = display.getVault();
			if(vault.containsEnrty("Username")) {
				rememberMeCheckBox.setSelected(true);
				
				usernameInput.setText(new String(vault.retrieveEntryPassword("Username")));
				passwordInput.setText(new String(vault.retrieveEntryPassword("Password")));
			}
		} catch(NoSuchAlgorithmException | UnrecoverableEntryException | KeyStoreException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == closeButton) {
			System.exit(0);
			return;
		}

		display.showLoading("Logging in as \"" + usernameInput.getText() + "\"...");
		
		new Thread(() -> {
			State result = LoginPage.login(usernameInput.getText(), passwordInput.getText());
		
			if(result == State.Failure) {
				errorLabel.setVisible(true);
				passwordInput.setText("");
				display.loginFailed();
			
			} else {
				errorLabel.setVisible(false);
				
				try {
					SecretKeyUtil vault = display.getVault();
					
					if(rememberMeCheckBox.isSelected()) {
						vault.createKeyEntry("Username", usernameInput.getText().toCharArray());
						vault.createKeyEntry("Password", passwordInput.getText().toCharArray());
					} else {
						vault.removeKeyEntry("Username");
						vault.removeKeyEntry("Password");
					}
				} catch(KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | InvalidKeySpecException ex) {
					ex.printStackTrace();
				}
				
				display.loginComplete();
			}
		}).start();
	}
	
	public boolean rememberMe() { return rememberMeCheckBox.isSelected(); } 
}
