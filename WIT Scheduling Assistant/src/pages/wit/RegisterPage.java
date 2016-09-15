package pages.wit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlLabel;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

import pages.Page;
import scheduling.SchedulingException;

public class RegisterPage extends Page {

	private HtmlTextInput[] clsNumberInputs;
	private HtmlSubmitInput submitButton;
	
	public RegisterPage() throws IOException {
		super("https://prodweb2.wit.edu/SSBPROD/leopard.zagreement.p_legal");
		
		HtmlForm form = super.formLookup("/SSBPROD/bwckcoms.P_Regs");
		submitButton = form.getInputByValue("Submit Changes");
		
		clsNumberInputs = new HtmlTextInput[10];
		for(int i = 0; i < 10; i ++) {
			HtmlLabel label = (HtmlLabel) page.getByXPath("//label[@for='crn_id" + (i + 1) + "]").get(0);
			clsNumberInputs[i] = (HtmlTextInput) label.getNextElementSibling();
		}
	}
	
	public SchedulingException[] addClasses(int... classIds) throws IOException {
		for(int i = 0; i < 10; i ++) {
			clsNumberInputs[i].setValueAttribute((i >= classIds.length ? "" : classIds[i]) + "");
		}
		
		HtmlPage resultPage = submitButton.click();
		HtmlTable errorTable = (HtmlTable) resultPage.getByXPath("//table[@summary='This layout table is used to present Registration Errors.']");
		
		boolean first = true;
		ArrayList<SchedulingException> errors = new ArrayList<>();
		for(HtmlTableBody body : errorTable.getBodies()) {
		for(HtmlTableRow row : body.getRows()) {
			if(first) { first = false; continue; }
			
			List<HtmlTableCell> messages = row.getCells();
			errors.add(new SchedulingException(
					messages.get(0).getTextContent().trim() + "! " + 
					messages.get(8).getTextContent().trim() + " " +
					messages.get(2).getTextContent().trim() + " " + 
					messages.get(3).getTextContent().trim() + ":" + messages.get(4).getTextContent().trim() + 
					" CRN: " + messages.get(1).getTextContent().trim()
				));
		}}
		
		return errors.toArray(new SchedulingException[errors.size()]);
	}
}
