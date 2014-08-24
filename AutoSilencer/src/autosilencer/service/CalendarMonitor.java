package uk.co.rm.android.AutoSilencer.service;

import java.util.List;

import uk.co.rm.android.AutoSilencer.obj.GCalendar;
import uk.co.rm.android.AutoSilencer.obj.GEvent;

/**
 * @todo: implement logic for monitoring calendars
 * @author Ross Moug
 */
public class CalendarMonitor {

	private GCalendar calendar;
	private List<GEvent> evts;
	
	public CalendarMonitor(GCalendar c, List<GEvent> e){
		this.calendar = c;
		this.evts = e;
	}
	
	/********************* CALENDAR MONITORING ********************/
	
	
	/********************* HELPER METHODS ********************/
	
	/********************* GETTERS AND SETTERS ********************/
}
