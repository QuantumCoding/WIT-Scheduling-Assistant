package scheduling;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import pages.wit.LookupResultsPage.LookupResult;
import web_interface.PageLock;

public class Schedule implements Comparable<Schedule> {
	private int id;
	private Scheduler creator;
	private ArrayList<Section> sections;
	
	private HashMap<LookupResult, Color> colorMap;
	private HashMap<Section, ArrayList<ClassConfig>> schedulable;
	private boolean isValid;
	
	private ArrayList<Integer> levelSizes;
	private HashMap<Integer, ArrayList<ClassConfig>> variants;
	private float weight;
	
//	private PageLock pages;
	
	public Schedule(ArrayList<Section> sections, Scheduler creator, PageLock pages, int id) {
		this.id = id;
		this.creator = creator;	
		this.sections = sections;
		
//		this.pages = pages;
	}
	
	public Schedule(ArrayList<Section> sections) {
		this.sections = sections;
		this.isValid = true;
		this.levelSizes = new ArrayList<>();
		levelSizes.add(1);

		variants = new HashMap<>();
		ArrayList<ClassConfig> configs = new ArrayList<>();
		for(Section section : sections)
			configs.add(new ClassConfig(section, section.hasLab() ? 0 : -1));
		variants.put(0, configs);
		
		colorMap = new HashMap<>();
		float colorIncrement = .9f / sections.size();
		for(int i = 0; i < sections.size(); i ++) {
			colorMap.put(sections.get(i).getSubject(), Color.getHSBColor(i * colorIncrement, 1, 1));
		}
	}
	
	public boolean isValid() {
		if(creator == null) return isValid;

		HashMap<LookupResult, HashMap<Section, HashMap<Section, ArrayList<ClassConfig>>>> valiConfig = creator.getValidConfigs();
		schedulable = new HashMap<>();
		
		for(Section section : sections) {
			HashMap<Section, ArrayList<ClassConfig>> configs = valiConfig.get(section.getSubject()).get(section);
			ArrayList<ClassConfig> usableSections = null;
			
			for(Section checkSection : sections) {
				if(checkSection == section) continue;
				
				ArrayList<ClassConfig> options = configs.get(checkSection);
				if(options.isEmpty()) return isValid = false;
				
				if(usableSections == null) usableSections = new ArrayList<>(options);
				else usableSections.retainAll(options);
				
				if(usableSections.isEmpty())
					return isValid = false;
			}
			
			schedulable.put(section, usableSections);
		}
		
		colorMap = creator.getColorMap();
		
//		System.out.print("Schedule: ");
//		for(Section section : schedulable.keySet()) {
//			System.out.print("\n\t -" + section.getClassName() + " : " + section.getCourseNumber() + " [");
//			for(ClassConfig config : schedulable.get(section))
//				System.out.print(config.getLab());
//			System.out.print("]");
//		}
		
//		System.out.println();
		
		creator = null;
		return isValid = true;
	}
	
	public int getVariantCount() {
		if(levelSizes == null) {
			levelSizes = new ArrayList<>();
			ArrayList<Section> sections = new ArrayList<>(schedulable.keySet());
			Collections.reverse(sections);
			
			int total = 1;
			levelSizes.add(total);
			
			for(Section section : sections) {
				total *= schedulable.get(section).size();
				levelSizes.add(total);			
			}
			
			Collections.reverse(levelSizes);
		}
		
		return levelSizes.get(0);
	}
	
	protected void calculateVariants() {
		variants = new HashMap<>();
		
		for(int variant = 0; variant < getVariantCount(); variant ++) {
			ArrayList<ClassConfig> configs = new ArrayList<>();
			
			int level = 1, levelId = variant;
			for(Section section : schedulable.keySet()) {
				configs.add(schedulable.get(section).get(levelId / levelSizes.get(level ++)));
				levelId %= levelSizes.get(level - 1);
			}
			
			variants.put(variant, configs);
		}
	}
	
	public void calculateWeight(float[][] rankings) {
		int count = 0;
		float sum = 0;
		
		for(Section section : schedulable.keySet()) {
			float max = 0;
			for(ClassConfig config : schedulable.get(section)) {
				if(config.getLab() == ClassConfig.NO_LAB) continue;
				float weight = getWeight(rankings, section.getLabs().get(config.getLab()).getDesignations());
				if(weight > max) max = weight;
			}
			
			sum += max; count ++;
			sum += getWeight(rankings, section.getDesignations()); count ++;
		}
		
		weight = sum / count;
		
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
	
	public ArrayList<Integer> getClassIds(int variant) {
		ArrayList<Integer> classIds = new ArrayList<>();
		for(ClassConfig config : getVarient(variant)) {
			classIds.add(config.getSection().getCourseNumber());
			if(config.getLab() != ClassConfig.NO_LAB)
				classIds.add(config.getSection().getLabs().get(config.getLab()).getCourseNumber());
		}
		
		return classIds;
	}
	
	public ArrayList<ClassConfig> getVarient(int variant) {
		return variants.get(variant);
	}

	public HashMap<LookupResult, Color> getColorMap() { return colorMap; }
	public ArrayList<Section> getSections() { return sections; }
	public int getId() { return id; }
	
	public String toString() {
		return creator == null ? schedulable.toString() : "Valid?";
	}

	public int compareTo(Schedule other) {
		return Float.compare(this.weight, other.weight);
	}
}
