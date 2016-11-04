package pages_java;

public class ClassOption {
	private String name;
	private int number;
	
	private String formXPath;

	protected ClassOption(String name, int number, String formXPath) {
		this.name = name;
		this.number = number;
		this.formXPath = formXPath;
	}

	public String getName() { return name; }
	public int getNumber() { return number; }

	public String getFormXPath() { return formXPath; }
	
	public String toString() {
		return number + ": " + name;
	}
}
