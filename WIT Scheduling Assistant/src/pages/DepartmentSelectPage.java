package pages;

import java.util.ArrayList;
import java.util.HashMap;

import util.References;

public class DepartmentSelectPage extends Page {
	private static HashMap<Choise, ArrayList<Choise>> departments = new HashMap<>();
	public static void clearTermCashe() { departments = null; }
	public static ArrayList<Choise> getDepartments(Choise term) {
		if(departments.get(term) == null)
			departments.put(term, new DepartmentSelectPage(term).collectedDepartments);
		return departments.get(term);
	}
	
	public static DepartmentClassesPage selectDepartment(Choise term, Choise department) {
		return new DepartmentSelectPage(term, department).classesPage;
	}
	
	public static SectionsPage selectClass(Choise term, Choise department, ClassOption classOption) {
		return new DepartmentSelectPage(term, department, true).classesPage.selectClass(classOption);
	}
	
	private ArrayList<Choise> collectedDepartments;
	private DepartmentClassesPage classesPage;

	private DepartmentSelectPage(Choise term, Object... department) {
		super(References.Department_URL + term.getValue(), term, department.length > 0 ? department[0] : null,
				department.length > 1 ? department[1] : null);
	}

	protected void init(Object[] args) {
		if(args[1] != null) {
			super.callJavaScript("getElementByXpath(\"" + References.Department_Selector_Path + "\").value = \""
						+ ((Choise) args[1]).getValue() + "\";");

			super.callJavaScript("$('input[name=SUB_BTN]')[0].type = 'hidden';");
//			super.callJavaScript("$('input[name=SUB_BTN]')[1].remove();");
			super.submitForm(References.Department_Form_Path, doc -> {
				classesPage = new DepartmentClassesPage(doc, (Choise)args[0], (Choise)args[1], args[2] != null?(Boolean)args[2]:false);
				super.doneLoading();
			});
			
		} else {
			collectedDepartments = Choise.collectChoices(super.getByXPath(References.Department_Selector_Path));
			super.doneLoading();
		}
	}
}
