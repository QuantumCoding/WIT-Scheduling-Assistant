package scheduling;

import java.util.ArrayList;

public class Designation {
	private Period period;
	private Location location;
	
	private boolean isViable;
	
	public Designation(Period period, Location location) {
		this.period = period;
		this.location = location;
		
		isViable = !(location.getBuilding() == null || location.getRoomNumber() == null ||
					period.getStartTime() == null || period.getEndTime() == null);
	}
	
	public static ArrayList<Designation> parse(Campus campus, String loc, String days, String time) {
		ArrayList<Designation> designations = new ArrayList<>();
		for(Period period : Period.parseMultiFormat(days, time))
			designations.add(new Designation(period, Location.parseSymbol(campus, loc)));
		return designations;
	}
	
	public Period getPeriod() { return period; }
	public boolean isViable() { return isViable; }
	public Location getLocation() { return location; }

	public String toString() {
		return "\n\t\t" + period + " " + location;
	}
}
