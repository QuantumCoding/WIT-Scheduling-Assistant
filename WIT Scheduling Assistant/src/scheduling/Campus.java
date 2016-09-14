package scheduling;

public enum Campus {
	WIT("Wentworth Institute Technology"), 
	CTC(null), 
	MIN(null), 
	TRI(null), 
	TCC(null), 
	MAS(null), 
	MCC(null), 
	STC(null),
	Unknown(null);
	
	private String name;
	private Campus(String name) {
		this.name = name;
	}
	
	public static Campus getByName(String name) {
		for(Campus campus : values()) {
			if(campus.name == null) continue;
			if(campus.name.toLowerCase().equals(name.toLowerCase()))
				return campus;
		}
		
		return Unknown;
	}
}
