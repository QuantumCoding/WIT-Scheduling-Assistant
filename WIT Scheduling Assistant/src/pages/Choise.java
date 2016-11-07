package pages;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Choise {
	private String visableName;
	private String value;
	
	public Choise(Element option) {
		visableName = option.getTextContent().trim();
		value = option.getAttribute("value");
	}
	
	public static ArrayList<Choise> collectChoices(Element selecter, String... filters) {
		ArrayList<Choise> choises = new ArrayList<>();
		
		if(filters.length == 0) {
			Element option = getNextElement(selecter.getFirstChild(), true);
			do {
				
				while(option != null && !option.getTagName().equalsIgnoreCase("option"))
					option = getNextElement(option, false);
				if(option == null) break;
				
				choises.add(new Choise(option));
			} while((option = getNextElement(option, false)) != null);
			
			return choises;
		}
		
		Element option = getNextElement(selecter.getFirstChild(), true);
		
		optionLoop:
		do {
			while(option != null && !option.getTagName().equalsIgnoreCase("option"))
				option = getNextElement(option, false);
			if(option == null) break;
			
			for(String filter : filters)
				if(option.getTextContent().contains(filter)) 
					break optionLoop;
			choises.add(new Choise(option));
		} while((option = getNextElement(option, false)) != null);
		
		return choises;
	}
	
	private static Element getNextElement(Node node, boolean current) {
		if(current && node instanceof Element) return (Element) node; 
		while((node = node.getNextSibling()) != null && !(node instanceof Element));
		return (Element) node;
	}
	
	public String getVisableName() { return visableName; }
	public String getValue() { return value; }
	
	public String toString() { return getVisableName(); }
}
