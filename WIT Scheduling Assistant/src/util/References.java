package util;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import user_interface.Display;

public class References {
	public static final String BaseUrl = "https://prodweb2.wit.edu/";

// ----------------------------------------------- Lookup ----------------------------------------------------------- \\	
		
	public static final String Lookup_Result_Form = "/SSBPROD/bwskfcls.P_GetCrse";
	public static final String Lookup_Result_Select = "SUB_BTN";
	
	
// ----------------------------------------------- Subject ----------------------------------------------------------- \\	
	
	public static final String Subject_URL = BaseUrl + "SSBPROD/bwckgens.p_proc_term_date";
	
	public static final String Subject_Form = "/SSBPROD/bwskfcls.P_GetCrse";
	public static final String Subject_Selector = "sel_subj";
	public static final String Subject_SearchButton = "Course Search";
	
// ----------------------------------------------- Term ----------------------------------------------------------- \\	

	public static final String Term_URL = BaseUrl + "SSBPROD/bwskflib.P_SelDefTerm";
	public static final String Term_Filter = "(View only)";
	
	public static final String Term_FormName = "/SSBPROD/bwcklibs.P_StoreTerm";
	public static final String Term_SelectorName = "term_in";
	public static final String Term_SubmitButton = "Submit";
	
// ----------------------------------------------- Login ----------------------------------------------------------- \\
	
	public static final String Login_URL = BaseUrl + "ssomanager/c/SSB";	
	
	public static final String Login_FormId = "fm1";	
	public static final String Login_Username = "username";	
	public static final String Login_Password = "password";
	public static final String Login_SignInButton = "Sign In";
	
// ----------------------------------------------- General ----------------------------------------------------------- \\
	
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
	
	public static final String Thread_Name = "WIT-Pages Thread";

// ----------------------------------------------- Images ----------------------------------------------------------- \\
	public static ImageIcon Icon_WIT_Header = null;
	static { 
		 try {
			Icon_WIT_Header = new ImageIcon(ImageIO.read(Display.class.getResource("witHeader.gif")));
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private References() {}
}
