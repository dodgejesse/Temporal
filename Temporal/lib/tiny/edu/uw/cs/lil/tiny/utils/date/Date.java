package edu.uw.cs.lil.tiny.utils.date;

/**
 * Represents a date.
 * 
 * @author Yoav Artzi
 */
public class Date {
	private final Month		month;
	private final Integer	monthDay;
	private final Day		weekDay;
	private final Integer	year;
	
	public Date(Month month, Integer monthDay, Day weekDay, Integer year) {
		this.month = month;
		this.monthDay = monthDay;
		this.weekDay = weekDay;
		this.year = year;
	}
	
	public Month getMonth() {
		return month;
	}
	
	public Integer getMonthDay() {
		return monthDay;
	}
	
	public Day getWeekDay() {
		return weekDay;
	}
	
	public Integer getYear() {
		return year;
	}
	
	@Override
	public String toString() {
		return "Date [month=" + month + ", monthDay=" + monthDay + ", weekDay="
				+ weekDay + ", year=" + year + "]";
	}
}
