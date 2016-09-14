package scheduling;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

public class Period {
	private WeekDay day;
	
	private LocalTime startTime;
	private LocalTime endTime;
	private Duration duration;
	
	private Period(WeekDay day, Period timeClone) {
		this.day = day;
		
		this.startTime = timeClone.startTime;
		this.endTime = timeClone.endTime;
		this.duration = timeClone.duration;
	}
	
	public Period(WeekDay day, String time) {
		this.day = day;
		
		if(time.equals("TBA"))
			return;
			
		int splitPoint = time.indexOf("-");
		String start = time.substring(0, splitPoint).trim().toLowerCase();
		String end = time.substring(splitPoint + 1).trim().toLowerCase();
		
		splitPoint = start.indexOf(":");
		int hour = Integer.parseInt(start.substring(0, splitPoint));
		startTime = LocalTime.of(
				hour + (start.endsWith("pm") && hour != 12 ? 12 : 0), 
				Integer.parseInt(start.substring(splitPoint + 1, splitPoint + 3)));
		
		splitPoint = end.indexOf(":");
		hour = Integer.parseInt(end.substring(0, splitPoint));
		endTime = LocalTime.of(
				hour + (end.endsWith("pm") && hour != 12 ? 12 : 0),  
				Integer.parseInt(end.substring(splitPoint + 1, splitPoint + 3)));
		
		duration = Duration.between(startTime, endTime);
	}
	
	public static ArrayList<Period> parseMultiFormat(String days, String time) {
		ArrayList<Period> periods = new ArrayList<>();
		
		Period timeClone = new Period(WeekDay.getDay(days.charAt(0)), time);
		for(int i = 1; i < days.length(); i ++)
			periods.add(new Period(WeekDay.getDay(days.charAt(i)), timeClone));
		periods.add(timeClone);
		
		return periods;
	}
	
	public boolean intersect(Period other) {
		if(other.day != day) return false;
		
		if(startTime == null || endTime == null) return false;
		if(other.startTime == null || other.endTime == null) return false;
		
		return (startTime.isBefore(other.endTime) || startTime.equals(other.endTime)) && 
				(endTime.isAfter(other.startTime) || endTime.equals(other.startTime));
	}
	
	public WeekDay getDay() { return day; }

	public LocalTime getStartTime() { return startTime; }
	public LocalTime getEndTime() { return endTime; }

	public Duration getDuration() { return duration; }
	
	public String toString() {
		return "(" + day + " at " + startTime + " to " + endTime + ")";
	}

	public static enum WeekDay {
		Monday('M'), Tuesday('T'), Wednesday('W'), Thursday('R'), Friday('F'), Saturday('S');
		
		private char id; WeekDay(char id) { this.id = id; }
		public char id() { return id; }
		
		public static WeekDay getDay(char dayId) {
			for(WeekDay day : values()) {
				if(Character.toUpperCase(dayId) == day.id)
					return day;
			}
			
			return null;
		}
	}
}
