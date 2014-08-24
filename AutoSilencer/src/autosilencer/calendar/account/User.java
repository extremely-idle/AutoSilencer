package uk.co.rm.android.AutoSilencer.obj;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ross Moug
 */
public class User {

	private String username;
	private List<GCalendar> calendars;
	
	public User(){
		this.username = null;
		this.calendars = new ArrayList<GCalendar>();
	}
	
	public User(String username){
		this.username = username;
		this.calendars = new ArrayList<GCalendar>();
	}

	/********************* GETTERS AND SETTERS ********************/
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<GCalendar> getCalendars() {
		return calendars;
	}

	public void setCalendars(List<GCalendar> calendars) {
		this.calendars = calendars;
	}
}
