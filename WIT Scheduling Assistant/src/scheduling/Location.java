package scheduling;

public class Location {
	private Campus campus;
	private String building;
	private String roomNumber;
	
	public Location(Campus campus, String building, String roomNumber) {
		this.campus = campus;
		this.building = building;
		this.roomNumber = roomNumber;
	}
	
	public static Location parseSymbol(Campus campus, String loc) {
		int index = loc.indexOf(" ");
		if(loc.equals("TBA")) return new Location(campus, null, null);
		return new Location(campus, loc.substring(0, index), loc.substring(index + 1));
	}

	public String toString() {
		return "On " + campus + " Campus in " + building + " Room: " + roomNumber;
	}

	public Campus getCampus() { return campus; }
	public String getBuilding() { return building; }
	public String getRoomNumber() { return roomNumber; }
}
