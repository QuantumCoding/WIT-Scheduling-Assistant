package pages;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scheduling.Section;
import util.References;

public class SectionsPage extends Page {

	private ClassOption classGroup;
	private ArrayList<Section> sections;
	
	protected SectionsPage(Document doc, ClassOption classGroup) {
		super(doc, classGroup);
	}

	protected void init(Object[] args) {
		this.classGroup = (ClassOption) args[0];
		sections = new ArrayList<>();
		
		Element table = super.getByXPath(References.Sections_Table_Path);
		
		do {
			if(!table.getAttribute("class").equalsIgnoreCase("datadisplaytable"))
				continue;
			
			NodeList tableNodes = table.getChildNodes(); int bodyIndex = -1;
			while((bodyIndex = getNext(tableNodes, "tBody", bodyIndex)) != -1) {
				Element body = (Element) tableNodes.item(bodyIndex);

				Section prevSection = null;
				NodeList bodyNodes = body.getChildNodes(); int rowIndex = -1;
				while((rowIndex = getNext(bodyNodes, "tr", rowIndex)) != -1) {
					Element row = (Element) bodyNodes.item(rowIndex);

					try {
						Section section = new Section(prevSection, classGroup, row);
						
						if(section.getLabs() != null)
							prevSection = section;
						
						if(section.getLabs() != null || prevSection == null) {
							if(section.isViable())
								sections.add(section);
						}
					} catch(IllegalArgumentException e) { }
				}
			}
			
		} while((table = getNext(table, "table", false)) != null);
	}
	
	private static Element getNext(Node node, String nodeType, boolean first) {
		while(first || node != null && (node = node.getNextSibling()) != null) {
			if(node instanceof Element && ((Element) node).getNodeName().equalsIgnoreCase(nodeType))
				return (Element) node;
			first = false;
		}
		
		return null;
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
	
	public ClassOption getClassGroup() { return classGroup; }
	public ArrayList<Section> getSections() { return sections; }
	
	public ArrayList<Section> getViableSections() {
		ArrayList<Section> viableSections = new ArrayList<>();
		
		for(Section section : sections) {
			if(!section.isViable()) continue;
			viableSections.add(section);
		}
		
		return viableSections;
	}
}
