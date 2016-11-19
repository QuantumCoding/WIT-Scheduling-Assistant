package scheduling;

import java.util.ArrayList;
import java.util.ListIterator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pages.ClassOption;

public class Section implements ClassAccessor {
	public static boolean DEBUG_ALLOW_NON_REG_CLASS = false;
	
	private ClassOption subject;
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
	
	public Section(Section prevSection, ClassOption subject, Element row) {
		ArrayList<String> data = new ArrayList<>();
		
		NodeList rowNodes = row.getChildNodes(); int tableItemIndex = -1;
		while((tableItemIndex = getNext(rowNodes, "td", tableItemIndex)) != -1) {
			Element tableItem = (Element) rowNodes.item(tableItemIndex);
		
			String value = "";
			NodeList tableItemNodes = tableItem.getChildNodes();
			for(int i = 0; i < tableItemNodes.getLength(); i ++) {
				Node child = tableItemNodes.item(i);
				
				if(child == null) continue;
				if(!(child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.ELEMENT_NODE)) 
					continue;
				
				value += child.getTextContent().trim();
			}
			
			if(value.trim().isEmpty()) continue;
			data.add(value);
		}
		
		if(data.size() < 17)
			throw new IllegalArgumentException("Invalid TR! " + data);
		
		this.subject = subject;
		
		boolean carryCourse = false;
		String courseNumberStr = data.get(1);
		try { courseNumber = Integer.parseInt(courseNumberStr); }
		catch(NumberFormatException e) { carryCourse = true; }
		
		if(carryCourse) {
			String days = data.get(8);
			String time = data.get(9);
			String location = data.get(15);
		
			designations = prevSection.getDesignations();
			Campus campus = designations.get(0).getLocation().getCampus();
			designations.addAll(Designation.parse(campus, location, days, time));
			return;
		}

		isOpen = !data.get(0).equalsIgnoreCase("C");
		if(!isOpen && DEBUG_ALLOW_NON_REG_CLASS) {
			isOpen = data.get(0).equalsIgnoreCase("NR");
		}
		
		classId = data.get(3);
		sectionId = data.get(4);
		
		Campus campus = Campus.valueOf(data.get(5));
		String credStr = data.get(6);
		int slashIndex = credStr.indexOf("/");
		credits = Double.parseDouble(credStr.substring(0, slashIndex == -1 ? credStr.length() : slashIndex));
		
		className = data.get(7);
		String days = data.get(8);
		String time = data.get(9);
		
		instructor = data.get(13);
		String location = data.get(15);
		
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

		trimInstructor(this);
	}

	private int getNext(NodeList list, String nodeType, int index) {
		for(int i = index + 1; i < list.getLength(); i ++) {
			Node node = list.item(i);
			
			if(node == null) continue;
			if(!(node instanceof Element)) continue;
			
			Element element = (Element) node;
			if(!element.getTagName().equalsIgnoreCase(nodeType)) continue;
			
			return i;
		}
		
		return -1;
	}
	
	private Section() {}
	
	public static Section parse(ClassOption subject, String className, String classId, int courseNumber, String sectionId,
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
		
		trimInstructor(section);
		return section;
	}
	
	private static void trimInstructor(Section section) {
		String[] nameParts = section.instructor.split(" ");
		section.instructor = nameParts[0];
		if(nameParts.length > 0) {
			int addFrom = nameParts.length - 1;
			int endAt = nameParts.length;
			
			if(nameParts[nameParts.length - 1].contains("(")) {
				addFrom -= 1;
				endAt -= 1;
			}
			
			for(; addFrom < endAt; addFrom ++)
				section.instructor += " " + nameParts[addFrom];
		}
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
	
	public ArrayList<ClassConfig> calculateConfigurations() {
		ArrayList<ClassConfig> validConfigs = new ArrayList<>();
		
		for(int i = -1; i < (labs == null ? 0 : labs.size()); i ++) {
			if(i > -1 && !labs.get(i).isViable()) continue;
			validConfigs.add(new ClassConfig(this, i));
		}
		
		if(validConfigs.size() > 1) 
			validConfigs.remove(0);
		return validConfigs;
	}

	public ClassOption getSubject() { return subject; }
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
	public ClassOption getClassOption() { return subject; }
}
