package uk.co.rm.android.AutoSilencer.obj;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import android.app.NotificationManager;

/**
 * 
 * @author Ross Moug
 */
public class GEvent {

	private String name;
	private String organiser;
	private String location;
	private Date startDate;
	private Date endDate;
	private TimeZone startTimezone;
	private TimeZone endTimezone;
	private GCalendar parentCalendar;
	
	public GEvent(String name, String organiser, String location, Date startDate, Date endDate, 
			TimeZone startTimezone, TimeZone endTimezone, GCalendar parent){
		this.name = name;
		this.organiser = organiser;
		this.location = location;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startTimezone = startTimezone;
		this.endTimezone = endTimezone;
		this.parentCalendar = parent;
	}

	/********************* GETTERS AND SETTERS ********************/

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public GCalendar getCalendar() {
		return parentCalendar;
	}

	public void setCalendar(GCalendar parentCalendar) {
		this.parentCalendar = parentCalendar;
	}

	public String getOrganiser() {
		return organiser;
	}

	public void setOrganiser(String organiser) {
		this.organiser = organiser;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startTime) {
		this.startDate = startTime;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endTime) {
		this.endDate = endTime;
	}

	public TimeZone getStartTimezone() {
		return startTimezone;
	}

	public void setStartTimezone(TimeZone startTimezone) {
		this.startTimezone = startTimezone;
	}

	public TimeZone getEndTimezone() {
		return endTimezone;
	}

	public void setEndTimezone(TimeZone endTimezone) {
		this.endTimezone = endTimezone;
	}
}
