package security;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import util.References;

public class SecretKeyUtil {
	
	private SecretKeyFactory factory;
	private KeyStore ks;
	private Path keystoreLocation;
	private char[] keystorePassword;
	
	public SecretKeyUtil(Path keystoreLocation, char[] keystorePassword) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
		
		this.keystoreLocation = keystoreLocation;
		this.keystorePassword = keystorePassword;
		
		ks = KeyStore.getInstance("JCEKS");
		
		if(Files.exists(keystoreLocation)) {
			ks.load(Files.newInputStream(keystoreLocation), keystorePassword);
		} else {
			ks.load(null, keystorePassword);
		}
		
		factory = SecretKeyFactory.getInstance("PBE");
	}
	
	public void createKeyEntry(String alias, char[] password) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, InvalidKeySpecException {
		SecretKey generatedSecret = factory.generateSecret(new PBEKeySpec(password));
		ks.setEntry(alias, new SecretKeyEntry(generatedSecret), new PasswordProtection(keystorePassword));
		if(!Files.exists(Paths.get(References.Save_Root))) Files.createDirectories(Paths.get(References.Save_Root));
		ks.store(Files.newOutputStream(keystoreLocation), keystorePassword);
	}
	
	public void removeKeyEntry(String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, InvalidKeySpecException {
		if(containsEnrty(alias)) ks.deleteEntry(alias);
		ks.store(Files.newOutputStream(keystoreLocation), keystorePassword);
	}

	public boolean containsEnrty(String alias) throws KeyStoreException {
		return ks.containsAlias(alias);
	}
	
	public char[] retrieveEntryPassword(String alias) throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException, InvalidKeySpecException {
		SecretKeyEntry entry = (SecretKeyEntry) ks.getEntry(alias, new PasswordProtection(keystorePassword));
		PBEKeySpec keySpec = (PBEKeySpec) factory.getKeySpec(entry.getSecretKey(), PBEKeySpec.class);
		
		return keySpec.getPassword();
	}
}