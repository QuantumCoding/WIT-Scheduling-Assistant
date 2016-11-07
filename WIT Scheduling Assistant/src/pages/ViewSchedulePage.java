package pages;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scheduling.Campus;
import scheduling.ClassConfig;
import scheduling.Designation;
import scheduling.Section;
import scheduling.TreeSchedule;
import util.References;

public class ViewSchedulePage extends Page {
	
	public static TreeSchedule getSchedule() {
		return new ViewSchedulePage().schedule;
	}

	private TreeSchedule schedule;
	
	private ViewSchedulePage() {
		super(References.Schedule_URL);
	}

	protected void init(Object[] args) {
		Element table = super.getByXPath("/HTML//TABLE[@class='datadisplaytable']");
		
		ArrayList<Element> classLinks = new ArrayList<>();
		NodeList tableNodes = table.getChildNodes(); int bodyIndex = -1;
		while((bodyIndex = getNext(tableNodes, "tBody", bodyIndex)) != -1) {
			Element body = (Element) tableNodes.item(bodyIndex);

			NodeList bodyNodes = body.getChildNodes(); int rowIndex = -1;
			while((rowIndex = getNext(bodyNodes, "tr", rowIndex)) != -1) {
				Element row = (Element) bodyNodes.item(rowIndex);
				
				NodeList rowNodes = row.getChildNodes(); int elemIndex = -1;
				while((elemIndex = getNext(rowNodes, "td", elemIndex)) != -1) {
					Element elem = (Element) rowNodes.item(elemIndex);
					
					NodeList elemNodes = elem.getChildNodes(); int linkIndex = -1;
					while((linkIndex = getNext(elemNodes, "a", linkIndex)) != -1) {
						Element link = (Element) elemNodes.item(linkIndex);
						classLinks.add(link);
					}
				}
			}
		}
		
		new Thread(() -> {
			ArrayList<ClassPage> pages = new ArrayList<>();
			for(Element element : classLinks) {
				ClassPage classP = new ClassPage(element.getAttribute("href"));
				pages.add(classP);
			}
			
			ArrayList<Section> sections = new ArrayList<>();
			ArrayList<Section> toRemove = new ArrayList<>();
			for(ClassPage labPage : pages) {
				if(labPage.isLab) {
					Section section = labPage.makeSection();
					sections.add(section);
					
					for(ClassPage checkPage : pages) {
						if(checkPage == labPage) continue; 
						
						if(checkPage.data[0].equals(labPage.data[0])) {
							if(checkPage.labs == null)
								checkPage.labs = new ArrayList<>();
							
							checkPage.labs.add(section);
							toRemove.add(section);
						}
					}
				}
			}
			
			sections.removeAll(toRemove);
			
			secPageLoop:
			for(ClassPage secPage : pages) {
				for(Section section : sections)
					if(section.getCourseNumber() == Integer.parseInt(secPage.data[4]))
						continue secPageLoop;
				
				sections.add(secPage.makeSection());
			}
			
			ArrayList<ClassConfig> configs = new ArrayList<>();
			for(Section section : sections)
				configs.addAll(section.calculateConfigurations());
			
			schedule = new TreeSchedule(configs, null);
			super.doneLoading();
		}, "Acquire Schedule Thread").start();
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
		
		

	private static class ClassPage extends Page {

		private String[] data;
		private ArrayList<Designation> designations;
		private boolean isLab;
		
		private ArrayList<Section> labs;
		
		private ClassPage(String url) {
			super(References.BaseUrl + url.substring(1));
		}
		
		protected void init(Object[] args) {
			data = new String[8];
			designations = new ArrayList<>();
			
			Element bigTable = super.getByXPath("/HTML//TABLE[@class='datadisplaytable']");
			String titleLine = bigTable.getElementsByTagName("caption").item(0).getTextContent().trim();
			
			int index = titleLine.indexOf("-");
			data[0] = titleLine.substring(0, index).trim(); // Class Name
			titleLine = titleLine.substring(index + 1).trim();
			
			index = titleLine.indexOf("-");
			String loi = titleLine.substring(0, index).trim(); // Lab or Info
			
			if(loi.toLowerCase().startsWith("lab")) {
				isLab = true;
				index = titleLine.indexOf("-");
				titleLine = titleLine.substring(index + 1).trim();
				index = titleLine.indexOf("-");
			} 
			
			String clsTypeAndNumber = titleLine.substring(0, index).trim();
			index = clsTypeAndNumber.indexOf(" ");
			data[1] = clsTypeAndNumber.substring(0, index); // Class Type
			data[2] = clsTypeAndNumber.substring(index + 1); // Class Number
			
			index = titleLine.indexOf("-");
			titleLine = titleLine.substring(index + 1);
			
			data[3] = titleLine.substring(0, titleLine.length()).trim(); // Section Number
			
			Element bigBody = (Element) bigTable.getElementsByTagName("tbody").item(0);
			NodeList rows = bigBody.getElementsByTagName("tr");
			
			data[4] = ((Element) rows.item(1)).getElementsByTagName("td").item(0).getTextContent().trim(); // Course Number
			data[5] = ((Element) rows.item(3)).getElementsByTagName("td").item(0).getTextContent().trim(); // Professor 
			data[6] = ((Element) rows.item(5)).getElementsByTagName("td").item(0).getTextContent().trim(); // Credits
			data[7] = ((Element) rows.item(7)).getElementsByTagName("td").item(0).getTextContent().trim(); // Campus
			
			Element locTable = super.getByXPath("/HTML//TABLE[@class='datadisplaytable'][2]");
			rows = ((Element) locTable.getElementsByTagName("tbody").item(0)).getElementsByTagName("tr");
			
			for(int i = 1; i < rows.getLength(); i ++) {
				Node rowNode = rows.item(i);
				if(rowNode.getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				NodeList units = ((Element) rowNode).getElementsByTagName("td");
				
				String[] destPart = units.item(3).getTextContent().trim().split(" ");
				
				designations.addAll(Designation.parse(Campus.getByName(data[7]), 
						destPart[0] + " " + destPart[destPart.length - 1], 
						units.item(2).getTextContent().trim(), 
						units.item(1).getTextContent().trim()
					));
			}
			
			super.doneLoading();
		}
		
		public Section makeSection() {
			return Section.parse(new ClassOption(null, null, data[0], Integer.parseInt(data[2]), null), data[0], data[2],   
					Integer.parseInt(data[4]), data[3], Double.parseDouble(data[6]), designations, data[5], isLab, labs);
		}
	}
}
