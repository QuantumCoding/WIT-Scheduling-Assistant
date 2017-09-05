package util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import user_interface.Display;

public class References {
	public static final String BaseUrl = "https://prodweb2.wit.edu/";

// ----------------------------------------------- Term ----------------------------------------------------------- \\	

	public static final String Term_URL = BaseUrl + "SSBPROD/bwskflib.P_SelDefTerm";
	public static final String Term_Filter = "(View only)";
	
	public static final String Term_FormAction = "/SSBPROD/bwcklibs.P_StoreTerm";
	public static final String Term_Form_Path = "/HTML//FORM[@action='" + Term_FormAction + "']";
	public static final String Term_Selector_Path = Term_Form_Path + "//SELECT";//[@id='term_in']";
	
// ----------------------------------------------- Login ----------------------------------------------------------- \\
	
	public static final String Login_URL = BaseUrl + "ssomanager/c/SSB";	
	
	public static final String Login_FormId = "fm1";	
	
	public static final String Login_Form_Path = "/HTML//FORM[@id='" + Login_FormId + "']";
	public static final String Login_Username_Path = Login_Form_Path + "//INPUT[@id='username']";
	public static final String Login_Password_Path = Login_Form_Path + "//INPUT[@id='password']";

// ----------------------------------------------- Schedule ----------------------------------------------------------- \\
	
	public static final String Schedule_URL = BaseUrl + "SSBPROD/bwskfshd.p_proc_crse_schd";
	
// ----------------------------------------------- Department ----------------------------------------------------------- \\
		
	public static final String Department_URL = BaseUrl + "SSBPROD/bwckgens.p_proc_term_date?"
			+ "p_calling_proc=P_CrseSearch&p_term=";	
	
	public static final String Department_FormAction = "/SSBPROD/bwskfcls.P_GetCrse";	
	
	public static final String Department_Form_Path = "/HTML//FORM[@action='" + Department_FormAction + "']";
	public static final String Department_Selector_Path = Department_Form_Path + "//SELECT[@id='subj_id']";
	public static final String Department_Submit_Path = Department_Form_Path + "//INPUT[@name='SUB_BTN']";
	
// ----------------------------------------------- Classes ----------------------------------------------------------- \\

	public static final String Classes_Table_Path = "/HTML//TABLE[@class='datadisplaytable']";
	public static final String Sections_Table_Path = "/HTML//TABLE[@class='datadisplaytable']";
	
// ----------------------------------------------- General ----------------------------------------------------------- \\
	
	public static LocalDate Selected_Date = LocalDate.now();
	
	public static String Current_Term = "";
	public static final String Save_Root = (System.getProperty("os.name").toUpperCase().contains("WIN") ? 
			System.getenv("AppData") : System.getProperty("user.home")) + "/WIT Scheduling Assistant";

// ----------------------------------------------- Time Pref. ----------------------------------------------------------- \\

	public static final String Time_Pref_Location = Save_Root + "/pref.dat";
	
// ----------------------------------------------- Vault ----------------------------------------------------------- \\
	
	public static final String Vault_Location = Save_Root + "/vault.key";
	
	public static final String Vault_Password = "_+XR===dsSW^Z%uDZ53+9Uv&*jFP7WF&jVnKfar6B?78vE^JK^AM&cX_ghbZ=9cCtB+CVmy?*Z+@pj"
			+ "L=wL_6*Lq8CPu35ypHAFAp!&eT#2v3rZBBQpm?YWud_mw#GvyX2uA?B67T9^AduP!?$rEQeFC^dUEWWdb8w4UY@y!UT*cW?tW8QW@&e2SU!!H!hd"
			+ "z66jgG^26bW_hDn+@^%M*-+JXK4MArdC$%7E2HaDgMAFmeXpFP*K4rJNzwjdgeTv7M3B$MxzYffZbZ*zxG#GV$HFt^c3Bk+!BfKsNMj99^hG&tf8"
			+ "E9awPbUE_ND+r6=T&bcR$TTq?W$rY9a&H^3V*UwfudC5Y36GP#JRyV-9+P!?cVm-B6k=en4snJCvL-hZ@4r@d&Ag*NfS?LKx5BQ^qt5u!VU3W!bn"
			+ "fXWj24eBpqmS4KL2v3xS=Esh?eK2#W@X%!_nX&VLenv8MNFaPP_x*y#@bQS_4Sd$Ybcv6ec4?KSSrbmNDwcAccGu*gh!LQR-WpWRAW9jfuL4#Xv+"
			+ "yk36FX5F&sJdecgLDa*jeKws@4+LuDMZvYj_PtGQabgp+HbDQJ3EQhb-$pP528M4Z-Jn&r-RF*Y#Q3RH3LRMusez5m@v7*s^C3TNC&@6@D*V@R9A"
			+ "hd!*WeG#KqrkYdV8d8fe3q@L8!Lc@h@!u^*Dd-R+hJW8U2eNHcVk$dLMD$D2G@&Kpy-Lbmpr6vbbR2UgBun*H+TmuMnXPAUZgykfs5BjJJ?#_^U6"
			+ "*83-xz%4ByBgyXf8fAVqyTBd2fhs&3LV_fXFk*czysYNNeD6J2uuu3c*$F&T@C6!!XqzKkYjwGZgR_CTFc@MrKMy83VRbfA%qV&*6&8fyxWY4b#u"
			+ "j$MkRxURLdFq&v*$WdcnHvm2cAHq#R2s9HzF&vW^zM?tYLGCps^qY9h3CySzf9AmBm2GNu-8ePFm!mP9MHpR6DdtgH#EU+a!4q^xPJ*Da_$M+CDu"
			+ "gDm-=K2DX%GVnM!_A6fCxL6@&Fey-CW*6t9Q8mb%XanmQrG@yhC*yP&PQ9Tt?2mp8pP*^8gZg6Z4RB+aK!K2h7*RaN7MsNANsyjVDw*tWVUp6rw^"
			+ "F^=4$qE^h28KG-htY&_7MV-xAWwYG*wFUsz!$H%tNtvrRx+tXJE+Zd!ExSPAHzX9!M7eXEGe3PaAKZCZ4HZsv*f3fk@-Pe!D%JU4xx?ah4dzRFeU"
			+ "vu$M8_M=jJYE8^_ggYgBjwE-?Q-b#D*9a!UNJ$4_?_$_PPv=dgqe8dszUq=7Nh7wCnW*fKnDGW@3EeWL^yF^HsYMHkDK7=BV6+9Zqp@n5pp75P$b"
			+ "E=fbQ?RtVPr&TdRUSN?brnEB$E36b3NfWjL=f6Xcp^jRvW2^!D-FwwDt%T@vtmYLr*uy#t$^QQvpR=9?jMGhmfgb8d@m682BNX3s7RMvcvcw?3xY"
			+ "zP#fqJ=B?cUXvTz!mw=$TDGVhzG4xrNQJLXT=GEY4D-9%+A%b!Ax$Jzt@dteqPNk&w^$mjeR%bevruUgNJY7-gN_J3sFp!xBVtG??eDDc9NsVCHY"
			+ "jG6QMHdMJy7eVNc3tRskmkVUTv@WkC75bwBcHG^r2Qg?B_tYTbd^3Ha-FEkra*45s$TVHKSs6r4URt$DRUEkk_MnaFm%=bxmq-dbL#XWfGPnxLP^"
			+ "qew7$b@r36exbUHveNhTzKeD^nW$8w2X_Jjf!?x5JB=rHJn%RJVsqY5HdWAyJcnL3FhH_D*^Sx_fUpGBcna#j3aGc_RCVYAC*U+JQ7uWPBTHMty7"
			+ "ubXB@Z984as#676XLjwhrrVL-ppMzC+UH%%PKx=zKwHWwZ62PB87Qrafh@HS6FJ6Zq3s$DQCp8kF+BFuvA-QSDe2?8$YQSW538taMthxjSnyQE%v"
			+ "6ebfqnSU!X^896edpU3&9!*pDq6U+qJ#cAx+t?BmxW^ZjUbVL62AsVT$LkNA?pUNcDUgQGx68w$9Rcp=!3fJw_BQNFT&qVRzaqPJQqsX$=DUDENJ"
			+ "tJs7Bs@NqkDjzMjPvY&L5!X-Rp=+8YY79DB4Pv+UeQAG?7hgba6@58X3=7BGN7Kgr&";

// ----------------------------------------------- Thread ----------------------------------------------------------- \\
	

// ----------------------------------------------- Images ----------------------------------------------------------- \\
	
	public static ImageIcon Icon_WIT_Header = null;
	static { // * Fonts.LENGTH_SCALE
		 try {
			 BufferedImage loadedImage = ImageIO.read(Display.class.getResource("witHeader.gif"));
			 BufferedImage scaledImage = new BufferedImage(
						(int) (loadedImage.getWidth()  * Fonts.LENGTH_SCALE), 
						(int) (loadedImage.getHeight() * Fonts.HEIGHT_SCALE), 
					BufferedImage.TYPE_INT_ARGB);
			 
			 Graphics g = scaledImage.getGraphics();
			 g.drawImage(loadedImage, (int) (7 * Fonts.LENGTH_SCALE), (int) (7 * Fonts.HEIGHT_SCALE), 
					 scaledImage.getWidth(), scaledImage.getHeight(), null);
			 g.dispose();
			 
			 Icon_WIT_Header = new ImageIcon(scaledImage);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private References() {}
}
