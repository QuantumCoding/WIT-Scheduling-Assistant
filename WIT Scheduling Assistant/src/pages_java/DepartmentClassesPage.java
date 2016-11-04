package pages_java;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javafx.application.Platform;
import util.References;

public class DepartmentClassesPage extends Page {
	
	private ArrayList<ClassOption> classList;
	private SectionsPage loadedSectionPage;
	private boolean invalid;

	protected DepartmentClassesPage(Document doc, boolean... skipListing) {
		super(doc);
	}

	protected void init(Object[] args) {
		if(args.length > 0 && (Boolean) args[1]) { doneLoading(); return; }
		
		classList = new ArrayList<>();
		Element table = super.getByXPath(References.Classes_Table_Path);
		
		do {
			if(!table.getAttribute("class").equalsIgnoreCase("datadisplaytable"))
				continue;
			
			NodeList tableNodes = table.getChildNodes(); int bodyIndex = -1;
			while((bodyIndex = getNext(tableNodes, "tBody", bodyIndex)) != -1) {
				Element body = (Element) tableNodes.item(bodyIndex);
				
				NodeList bodyNodes = body.getChildNodes(); int roxIndex = -1;
				while((roxIndex = getNext(bodyNodes, "tr", roxIndex)) != -1) {
					Element row = (Element) bodyNodes.item(roxIndex);

					ArrayList<String> data = new ArrayList<>();
					
					NodeList rowNodes = row.getChildNodes(); int tableItemIndex = -1;
					while((tableItemIndex = getNext(rowNodes, "td", tableItemIndex)) != -1) {
						Element tableItem = (Element) rowNodes.item(tableItemIndex);
					
						NodeList tableItemNodes = tableItem.getChildNodes();
						for(int i = 0; i < tableItemNodes.getLength(); i ++) {
							Node child = tableItemNodes.item(i);
							
							if(child == null) continue;
							if(!(child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.ELEMENT_NODE)) 
								continue;
							
							if(child.getNodeType() == Node.TEXT_NODE) {
								String value = child.getTextContent().trim();
								
								if(value.isEmpty())
									continue;
								data.add(value);
								
							} else if(((Element) child).getTagName().equalsIgnoreCase("form")) {
								data.add(getXPath((Element) child));
							}
						}
					}
					
					if(data.isEmpty()) continue;
					classList.add(new ClassOption(data.get(1), Integer.parseInt(data.get(0)), data.get(2)));
				}
			}
			
		} while((table = getNext(table, "table", false)) != null);
		
		super.doneLoading();
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
	
	private String getXPath(Element node) {
	    Element parent = node.getParentNode() instanceof Element ? (Element) node.getParentNode() : null;
	    
	    if (parent == null)
	        return "/" + node.getTagName();
	    
	    NodeList siblings = parent.getChildNodes();
	    for(int i = 0; i < siblings.getLength(); i ++)
	    	if(siblings.item(i).isEqualNode(node))
	    		return getXPath(parent) + "/node()" + "[" + (i + 1) + "]";
	    
	    throw new IllegalArgumentException("Node is not an Child of its Parent?!");
	}
	
	public ArrayList<ClassOption> getClassList() { return classList; }
	
	public SectionsPage selectClass(ClassOption option) {
		if(invalid) throw new IllegalStateException("Selection has already been made!");
		invalid = true;
		
		Platform.runLater(() -> {
			super.callJavaScript("getElementByXpath(\"" + option.getFormXPath() + "/input[@type='submit']\").type = 'hidden'");
			
			super.submitForm(option.getFormXPath(), doc -> {
				loadedSectionPage = new SectionsPage(doc, option);
				super.doneLoading();
			});
		});
		
		synchronized (this) {
			try { this.wait(); } 
			catch(InterruptedException e) { }
		}
		
		return loadedSectionPage;
	}
}
