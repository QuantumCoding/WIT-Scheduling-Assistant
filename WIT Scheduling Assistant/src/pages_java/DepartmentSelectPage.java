package pages_java;

import java.util.ArrayList;

import util.References;

public class DepartmentSelectPage extends Page {
	private static ArrayList<Choise> departments;
	public static void clearTermCashe() { departments = null; }
	public static ArrayList<Choise> getDepartments(Choise term) {
		if(departments == null)
			departments = new DepartmentSelectPage(term).collectedDepartments;
		return departments;
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
		super(References.Department_URL + term.getValue(), department);
	}

	protected void init(Object[] args) {
		if(args.length > 0) {
			super.callJavaScript("getElementByXpath(\"" + References.Department_Selector_Path + "\").value = \""
						+ ((Choise) args[0]).getValue() + "\";");

			super.callJavaScript("$('input[name=SUB_BTN]')[0].type = 'hidden';");
			super.callJavaScript("$('input[name=SUB_BTN]')[1].remove();");
			super.submitForm(References.Department_Form_Path, doc -> {
				classesPage = new DepartmentClassesPage(doc, args.length > 1 ? (Boolean) args[1] : false);
				super.doneLoading();
			});
			
		} else {
			collectedDepartments = Choise.collectChoices(super.getByXPath(References.Department_Selector_Path));
			super.doneLoading();
		}
	}
}
