package pages;

import scheduling.ClassAccessor;

public class ClassOption implements ClassAccessor {
	private String name;
	private int number;
	
	private String formXPath;
	
	private Choise term;
	private Choise department;

	protected ClassOption(Choise term, Choise department, String name, int number, String formXPath) {
		this.name = name;
		this.number = number;
		this.formXPath = formXPath;
		
		this.term = term;
		this.department = department;
	}

	public String getName() { return name; }
	public int getNumber() { return number; }

	public String getFormXPath() { return formXPath; }
	
	public Choise getTerm() { return term; }
	public Choise getDepartment() { return department; }
	
	public String toString() {
		return name;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + number;
		
		return result;
	}

	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof ClassOption))
			return false;
		
		ClassOption other = (ClassOption) obj;
		
		if(name == null) {
			if(other.name != null)
				return false;
		} else if(!name.equals(other.name))
			return false;
		
		if (number != other.number)
			return false;
		
		return true;
	}
	
	public ClassOption getClassOption() { return this; }
}
