package scheduling;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import pages.ClassOption;

public class TreeSchedule implements Comparable<TreeSchedule> {
	private float weight;

	private ArrayList<ClassConfig> sections;
	private HashMap<ClassOption, Color> colorMap;
	
	public TreeSchedule(ArrayList<ClassConfig> sections, HashMap<ClassOption, Color> colorMap) {
		this.sections = sections;
		this.colorMap = colorMap != null ? colorMap : Scheduler_Tree.createColorMap(sections);
	}
	
	public void calculateWeight(float[][] rankings) {
		float sum = 0;
		
		for(ClassConfig config : sections) {
			@SuppressWarnings("unchecked")
			ArrayList<Designation> designations = (ArrayList<Designation>) config.getSection().getDesignations().clone();
			
			if(config.getLab() != ClassConfig.NO_LAB)
				designations.addAll(config.getSection().getLabs().get(config.getLab()).getDesignations());
			
			sum += getWeight(rankings, designations);
		}
		
		weight = sum / sections.size();
		
//		sum = 0; count = 0;
//		for(Section section : schedulable.keySet()) {
//			sum += pages.getProfessorRating(section.getInstructor(), 
//						section.getDesignations().get(0).getLocation().getCampus()); 
//			count ++;
//		}
//		
//		weight = (sum / count) * 0.25f + weight * 0.75f;
	}
	
	private float getWeight(float[][] ranking, ArrayList<Designation> designations) {
		float sum = 0;
		for(Designation designation : designations) 
			sum += designation.getPeriod().weight(ranking);
		return sum / designations.size();
	} 
	
	public float getWeight() { return weight; }
	
	public HashMap<ClassOption, Color> getColorMap() { return colorMap; }
	public ArrayList<ClassConfig> getSections() { return sections; }
	
	public ArrayList<Integer> getClassIds() {
		ArrayList<Integer> classIds = new ArrayList<>();
		for(ClassConfig config : sections) {
			classIds.add(config.getSection().getCourseNumber());
			if(config.getLab() != ClassConfig.NO_LAB)
				classIds.add(config.getSection().getLabs().get(config.getLab()).getCourseNumber());
		}
		
		return classIds;
	}
	
	public String toString() {
		return sections.toString();
	}

	public int compareTo(TreeSchedule other) {
		return Float.compare(this.weight, other.weight);
	}
}
