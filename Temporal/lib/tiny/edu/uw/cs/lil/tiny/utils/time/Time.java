package edu.uw.cs.lil.tiny.utils.time;

/**
 * Represents a specific time in 24HR format.
 * 
 * @author Yoav Artzi
 */
public class Time {
	private final int	hour;
	private final int	minute;
	
	public Time(int hour, int minute) {
		this.hour = hour;
		this.minute = minute;
	}
	
	public int getHour() {
		return hour;
	}
	
	public int getMinute() {
		return minute;
	}
	
}
