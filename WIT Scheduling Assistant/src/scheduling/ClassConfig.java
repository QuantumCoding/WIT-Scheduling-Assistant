package scheduling;

import java.util.ArrayList;

import pages.ClassOption;

public class ClassConfig implements ClassAccessor {
	public static final int NO_LAB = -1;
	
	private Section section;
	private int lab;
	
	public ClassConfig(Section section, int lab) {
		this.section = section;
		this.lab = lab;
	}
	
	public boolean doesConflict(ClassConfig otherConfig) {
		if(otherConfig == null) return false;
		Section other = otherConfig.getSection();
		
		@SuppressWarnings("unchecked")
		ArrayList<Designation> otherDesignations = (ArrayList<Designation>) other.getDesignations().clone();
		if(otherConfig.getLab() != ClassConfig.NO_LAB)
			otherDesignations.addAll(other.getLabs().get(otherConfig.getLab()).getDesignations());
		
		@SuppressWarnings("unchecked")
		ArrayList<Designation> currentDesignations = (ArrayList<Designation>) section.getDesignations().clone();
		if(this.lab != ClassConfig.NO_LAB)
			currentDesignations.addAll(section.getLabs().get(this.lab).getDesignations());
		
		for(Designation designation : currentDesignations) {
		for(Designation otherDesig : otherDesignations) {
			if(designation.getPeriod().intersect(otherDesig.getPeriod())) {
				return true;
			}
		}}
		
		return false;
	}
	
	public Section getSection() { return section; }
	public int getLab() { return lab; }
	
	public String toString() {
		return "Lab: " + lab;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + lab;
		result = prime * result + ((section == null) ? 0 : section.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof ClassConfig))
			return false;
		
		ClassConfig other = (ClassConfig) obj;
		
		if(lab != other.lab)
			return false;
		
		if(section == null) {
			if(other.section != null)
				return false;
		} else if(!section.equals(other.section))
			return false;
		
		return true;
	}

	public ClassOption getClassOption() { return section.getClassOption(); }
}
