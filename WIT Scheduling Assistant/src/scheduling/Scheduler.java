package scheduling;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import pages.wit.LookupResultsPage.LookupResult;
import web_interface.PageLock;

public class Scheduler {
	private HashMap<LookupResult, Color> colorMap;
	private HashMap<LookupResult, ArrayList<Section>> options;
	private HashMap<LookupResult, HashMap<Section, HashMap<Section, ArrayList<ClassConfig>>>> validConfigs;
	private ArrayList<Integer> levelSizes;
	private HashMap<Integer, Schedule> schedules;
	
	private float[][] timeRankings;
	private PageLock pages;
	
	public Scheduler(PageLock pages, ArrayList<LookupResult> classes) { this(pages, classes, null, null); }
	
	public Scheduler(PageLock pages, ArrayList<LookupResult> classes, HashMap<LookupResult, ArrayList<Section>> preCollectedSections, HashMap<LookupResult, ArrayList<Boolean>> nonViable) {
		options = new HashMap<>();
		ArrayList<LookupResult> collectClasses = new ArrayList<>(classes);
		
		if(preCollectedSections != null && nonViable != null) {
			collectClasses.removeAll(preCollectedSections.keySet());
			
			for(LookupResult clazz : preCollectedSections.keySet()) {
				if(!classes.contains(clazz)) continue;
				
				ArrayList<Section> collectedSections;
				options.put(clazz, collectedSections = new ArrayList<>());
				
				ArrayList<Boolean> valid = nonViable.get(clazz);
				ArrayList<Section> sections = preCollectedSections.get(clazz);
				
				for(int i = 0; i < valid.size(); i ++) {
					if(valid.get(i)) {
						collectedSections.add(sections.get(i));
					}
				}
			}
		}	
		
		options.putAll(pages.collectSections(collectClasses));
		this.pages = pages;
		
		colorMap = new HashMap<>();
		float colorIncrement = .9f / classes.size();
		for(int i = 0; i < classes.size(); i ++) {
			colorMap.put(classes.get(i), Color.getHSBColor(i * colorIncrement, 1, 1));
		}
	}
	
	public void setRankings(float[][] rankings) { this.timeRankings = rankings; }
	
	public void compare() {
		validConfigs = new HashMap<>();
		for(LookupResult subject : options.keySet()) {
			for(Section section : options.get(subject)) {
				for(LookupResult compareSubject : options.keySet()) {
					if(compareSubject == subject) continue;
					
					for(Section compareSection : options.get(compareSubject)) {
						HashMap<Section, HashMap<Section, ArrayList<ClassConfig>>> subjectConfigMap;
						if((subjectConfigMap = validConfigs.get(subject)) == null)
							validConfigs.put(subject, subjectConfigMap = new HashMap<>());
						
						HashMap<Section, ArrayList<ClassConfig>> sectionConfigMap;
						if((sectionConfigMap = subjectConfigMap.get(section)) == null)
							subjectConfigMap.put(section, sectionConfigMap = new HashMap<>());

						sectionConfigMap.put(compareSection, section.findValidConfigurations(compareSection));
					}
				}
			}
		}
	}
	
	public int calculateCombinationCount() {
		if(validConfigs == null)
			compare();
		
		levelSizes = new ArrayList<>();
		ArrayList<LookupResult> subjects = new ArrayList<>(options.keySet());
		Collections.reverse(subjects);
		
		int total = 1;
		levelSizes.add(total);
		
		for(LookupResult subject : subjects) {
			total *= options.get(subject).size();
			levelSizes.add(total);
			
			if(total == 0) 
				throw new SchedulingException("No Sections avalible for " + subject.getClassName() + ":" + subject.getClassId());
		}

		Collections.reverse(levelSizes);
		return total;
	}
	
	public void createSchedule(int id) {
		if(levelSizes == null)
			calculateCombinationCount();
		
		if(schedules == null)
			schedules = new HashMap<>();
		
		ArrayList<Section> sections = new ArrayList<>();
		int level = 1, levelId = id;
		for(LookupResult subject : options.keySet()) {
			sections.add(options.get(subject).get(levelId / levelSizes.get(level ++)));
			levelId %= levelSizes.get(level - 1);
		}
		
		schedules.put(id, new Schedule(sections, this, pages, id));
	}
	
	public void removeInvalidSchedules(int id) {
		if(schedules == null || schedules.get(id) == null)
			createSchedule(id);
		
		if(!schedules.get(id).isValid())
			schedules.put(id, null);
	}
	
	public void calculateVariants(int id) {
		schedules.get(id).calculateVariants();
	}
	
	public void calculateWeights(int id) {
		schedules.get(id).calculateWeight(timeRankings);
	}
	
	public ArrayList<Schedule> getSchedules() {
		ArrayList<Schedule> possible = new ArrayList<>();
		for(Integer id : schedules.keySet())
			if(schedules.get(id) != null) 
				possible.add(schedules.get(id));
		
		possible.sort(Collections.reverseOrder());
		return possible;
	}
	
	public void cleanOut() {
		validConfigs = null;
	}
	
	protected HashMap<LookupResult, HashMap<Section, HashMap<Section, ArrayList<ClassConfig>>>> getValidConfigs() { return validConfigs; }
	public int posible() { return levelSizes == null ? calculateCombinationCount() :  levelSizes.get(0); }
	protected HashMap<LookupResult, Color> getColorMap() { return colorMap; }
}
