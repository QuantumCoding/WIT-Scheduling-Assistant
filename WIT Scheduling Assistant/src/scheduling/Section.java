package scheduling;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

import pages.LookupResultsPage.LookupResult;

public class Section {
	private static final boolean DEBUG_ALLOW_NON_REG_CLASS = true;
	
	private LookupResult subject;
	private String className;
	private String classId;

	private int courseNumber;
	private String sectionId;
	private double credits;
	
	private ArrayList<Designation> designations;
	
	private String instructor;//class
	
	private boolean isLab, hasLab;
	private boolean isOpen, isViable;
	
	private ArrayList<Section> labs;
	
	public Section(Section prevSection, LookupResult subject, HtmlTableRow row) {
		List<HtmlElement> elements = row.getElementsByTagName("td");
		if(elements.size() < 17) throw new IllegalArgumentException("Invalid TR! " + row);
		
		this.subject = subject;
		
		boolean carryCourse = false;
		String courseNumberStr = elements.get(1).getFirstChild().getTextContent().trim();
		try { courseNumber = Integer.parseInt(courseNumberStr); }
		catch(NumberFormatException e) { carryCourse = true; }
		
		if(carryCourse) {
			String days = elements.get(8).getTextContent();
			String time = elements.get(9).getTextContent();
			String location = elements.get(15).getTextContent();
		
			designations = prevSection.getDesignations();
			Campus campus = designations.get(0).getLocation().getCampus();
			designations.addAll(Designation.parse(campus, location, days, time));
			return;
		}

		isOpen = elements.get(0).getChildElementCount() > 1;
		if(!isOpen && DEBUG_ALLOW_NON_REG_CLASS) {
			Iterator<DomElement> iter = elements.get(0).getChildElements().iterator();
			if(iter.hasNext())
				isOpen = iter.next().getTextContent().equalsIgnoreCase("NR");
		}
		
		classId = elements.get(3).getTextContent();
		sectionId = elements.get(4).getTextContent();
		
		Campus campus = Campus.valueOf(elements.get(5).getTextContent());
		String credStr = elements.get(6).getTextContent();
		int slashIndex = credStr.indexOf("/");
		credits = Double.parseDouble(credStr.substring(0, slashIndex == -1 ? credStr.length() : slashIndex));
		
		className = elements.get(7).getTextContent();
		String days = elements.get(8).getTextContent();
		String time = elements.get(9).getTextContent();
		
		instructor = elements.get(13).getTextContent();
		String location = elements.get(15).getTextContent();
		
		designations = new ArrayList<>();
		designations.addAll(Designation.parse(campus, location, days, time));
		
		isLab = className.toLowerCase().endsWith("lab");
		
		if(isLab) {
			if(prevSection == null) return;
			prevSection.labs.add(this);
			prevSection.hasLab = true;
		} else {
			labs = new ArrayList<>();
		}
		
		notViable:
		if(isOpen) {
			isViable = false;
			
			for(Designation designation : designations)
				if(!designation.isViable()) break notViable;
			
			if(hasLab) {
				for(Section lab : labs) {
					if(lab.isViable) {
						isViable = true;
						break notViable;
					}
				}
				
				break notViable;
			}
			
			isViable = true;
		}
	}

	private Section() {}
	
	public static Section parse(LookupResult subject, String className, String classId, int courseNumber, String sectionId,
			double credits, ArrayList<Designation> designations, String instructor, boolean isLab, ArrayList<Section> labs) {
		Section section = new Section();

		section.subject = subject;
		section.className = className;
		section.classId = classId;
		section.courseNumber = courseNumber;
		section.sectionId = sectionId;
		section.credits = credits;
		section.designations = designations;
		section.instructor = instructor;
		section.isLab = isLab;
		section.hasLab = labs != null;
		section.isOpen = true;
		section.isViable = true;
		section.labs = labs;
		
		return section;
	}
	
	public String toString() {
		return "Section [\n\tsubject=" + subject + "\n\t className=" + className + "\n\t classId=" + classId + "\n\t courseNumber="
				+ courseNumber + "\n\t sectionId=" + sectionId + "\n\t credits=" + credits
				 + "\n\t instructor=" + instructor + "\n\t" + designations
				+ "\n\t isLab=" + isLab + "\n\t hasLab=" + hasLab + "\n\t isOpen=" + isOpen + "\n]\n";
	}
	
	public ArrayList<ClassConfig> findValidConfigurations(Section other) {
		ArrayList<ClassConfig> validConfigs = new ArrayList<>();
		ArrayList<ClassConfig> otherConfigs = new ArrayList<>();
		
		for(int i = -1; i < (labs == null ? 0 : labs.size()); i ++) {
			if(i > -1 && !labs.get(i).isViable()) continue;
			validConfigs.add(new ClassConfig(this, i));
		}
		
		for(int i = -1; i < (other.labs == null ? 0 : other.labs.size()); i ++) {
			if(i > -1 && !other.labs.get(i).isViable()) continue;
			otherConfigs.add(new ClassConfig(other, i));
		}
		
		for(ClassConfig otherConfig : otherConfigs) {
			ArrayList<Designation> otherDesignations = otherConfig.getLab() == ClassConfig.NO_LAB ? 
					other.designations : other.getLabs().get(otherConfig.getLab()).designations;
			
			ListIterator<ClassConfig> iter = validConfigs.listIterator();
			
			collisionsLoop:
			while(iter.hasNext()) {
				ClassConfig config = iter.next();
				ArrayList<Designation> currentDesignations = config.getLab() == ClassConfig.NO_LAB ? 
						designations : labs.get(config.getLab()).designations;
				
				for(Designation designation : currentDesignations) {
				for(Designation otherDesig : otherDesignations) {
					if(designation.getPeriod().intersect(otherDesig.getPeriod())) {
						iter.remove();
						continue collisionsLoop;
					}
				}}
			}
		}
		
		if(hasLab && !validConfigs.isEmpty()) {
			if(validConfigs.get(0).getLab() == ClassConfig.NO_LAB)
				validConfigs.remove(0);
			else
				validConfigs.clear();
		}
		
		return validConfigs;
	}

	public LookupResult getSubject() { return subject; }
	public String getClassName() { return className; }
	public String getClassId() { return classId; }
	
	public int getCourseNumber() { return courseNumber; }
	public String getSectionId() { return sectionId; }
	
	public double getCredits() { return credits; }
	public ArrayList<Designation> getDesignations() { return designations; }
	
	public String getInstructor() { return instructor; }
	
	public boolean isLab() { return isLab; }
	public boolean hasLab() { return hasLab; }
	public boolean isOpen() { return isOpen; }
	public boolean isViable() { return isViable; }
	
	public ArrayList<Section> getLabs() { return labs; }
}
