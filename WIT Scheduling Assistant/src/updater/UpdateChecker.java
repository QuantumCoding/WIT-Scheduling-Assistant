package updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import launcher.WIT_Lancher;
import pages.UpdatePage;

public class UpdateChecker {
	private static final Pattern PATTERN = Pattern.compile("V((\\d+?)\\.(\\d+?)\\.(\\d+?))"); // V#.#.#
	private static String CURRENT_VERSION_URL;
	
	public static void checkAndPrompt() {
		if(isLatestVersion()) return;
		
		int result = JOptionPane.showConfirmDialog(null, "A new Version is avalable. Would you like to Download it now?", 
				"New Version Avalable", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		if(result != JOptionPane.YES_OPTION) return;
		
		try {
			File updateFile = File.createTempFile("witUpdater", ".jar");
			
			FileOutputStream out = new FileOutputStream(updateFile);
			ReadableByteChannel source = Channels.newChannel(UpdateChecker.class.getResourceAsStream("update.jar"));
			FileChannel destination = out.getChannel();
	        destination.transferFrom(source, 0, Long.MAX_VALUE);
	        out.close();
			
			Runtime.getRuntime().exec(new String[]{ "java", "-jar", updateFile.getAbsolutePath(), 
					getSelf().getAbsolutePath(), CURRENT_VERSION_URL, pullOutVersion(CURRENT_VERSION_URL)
			});
			
			System.exit(0);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isLatestVersion() {
		try { 
			String cVersion = pullOutVersion(CURRENT_VERSION_URL = UpdatePage.getDownloadLink(UpdatePage.CURRENT_VERSION));
			if(cVersion == null) return true;
			return compareVersions(WIT_Lancher.VERSION, cVersion) > 0;
		} catch(IllegalArgumentException e) { return false; }
	}
	
	public static String pullOutVersion(String downloadURL) {
		Matcher match = PATTERN.matcher(downloadURL);
		if(match.find()) {
			return match.group(1);
		}
		
		return null;
	}
	
	public static int compareVersions(String v1, String v2) {
		int v1Magor, v2Magor;
		int v1Minor, v2Minor;
		int v1Patch, v2Patch;
		
		try {
			if(!v1.startsWith("V")) v1 = "V" + v1;
			
			Matcher matchV1 = PATTERN.matcher(v1);
			if(!matchV1.find()) throw new IllegalArgumentException();
			
			v1Magor = Integer.parseInt(matchV1.group(2));
			v1Minor = Integer.parseInt(matchV1.group(3));
			v1Patch = Integer.parseInt(matchV1.group(4));
		} catch(Exception e) {
			throw new IllegalArgumentException(v1 + " is not a valid Version String");
		}
		
		try {
			if(!v2.startsWith("V")) v2 = "V" + v2;
			
			Matcher matchV2 = PATTERN.matcher(v2);
			if(!matchV2.find()) throw new IllegalArgumentException();
			
			v2Magor = Integer.parseInt(matchV2.group(2));
			v2Minor = Integer.parseInt(matchV2.group(3));
			v2Patch = Integer.parseInt(matchV2.group(4));
		} catch(Exception e) {
			throw new IllegalArgumentException(v2 + " is not a valid Version String");
		}
		
		if(v1Magor > v2Magor) return 1;
		if(v1Magor < v2Magor) return -1;
		
		if(v1Minor > v2Minor) return 1;
		if(v1Minor < v2Minor) return -1;
		
		if(v1Patch > v2Patch) return 1;
		if(v1Patch < v2Patch) return -1;
		
		return 0;
	}
	
	public static File getSelf() { 
		try { return new File(UpdateChecker.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()); }
		catch (URISyntaxException e) { e.printStackTrace(); return null; } 
	}
}
