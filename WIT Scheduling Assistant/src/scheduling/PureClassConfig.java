package scheduling;

public class PureClassConfig {
	public static final int NO_LAB = -1;
	
	private PureSection section;
	private int lab;
	
	public PureClassConfig(PureSection section, int lab) {
		this.section = section;
		this.lab = lab;
	}
	
	public PureSection getSection() { return section; }
	public int getLab() { return lab; }
	
	public String toString() {
		return "Lab: " + lab;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lab;
		result = prime * result + ((section == null) ? 0 : section.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof PureClassConfig))
			return false;
		
		PureClassConfig other = (PureClassConfig) obj;
		
		if(lab != other.lab)
			return false;
		
		if(section == null) {
			if(other.section != null)
				return false;
		} else if(!section.equals(other.section))
			return false;
		
		return true;
	}
	
	
}
