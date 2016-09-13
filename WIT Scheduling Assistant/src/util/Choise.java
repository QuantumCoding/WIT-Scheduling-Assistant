package util;

import java.util.ArrayList;
import java.util.ListIterator;

import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;

public final class Choise {

	private HtmlOption option;
	private String visableName;
	private String value;
	
	public Choise(HtmlOption option) {
		this.option = option;
		visableName = option.getTextContent().trim();
		value = option.getValueAttribute();
	}
	
	public static ArrayList<Choise> collectChoices(HtmlSelect selecter, String... filters) {
		ArrayList<HtmlOption> options = new ArrayList<>(selecter.getOptions());
		ArrayList<Choise> choises = new ArrayList<>();
		
		if(filters.length == 0) {
			for(HtmlOption option : options)
				choises.add(new Choise(option));
			return choises;
		}
		
		for(String filter : filters) {
			ListIterator<HtmlOption> iter = options.listIterator();
			
			while(iter.hasNext()) {
				HtmlOption option = iter.next();
				if(option.getText().contains(filter)) continue;
				choises.add(new Choise(option));
			}
		}
		
		return choises;
	}

	public HtmlOption getOption() { return option; 	}
	public String getVisableName() { return visableName; }
	public String getValue() { return value; }
	
	public String toString() { return getVisableName(); }
}
