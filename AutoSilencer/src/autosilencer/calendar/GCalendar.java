package uk.co.rm.android.AutoSilencer.obj;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;

/**
 * 
 * @author Ross Moug
 */
public class GCalendar {
	
	private long id;
	private String name;
	private User owner;
	private List<GEvent> events;
	private int colour;
	private boolean sync;

	public GCalendar(long id, String name, User user, boolean sync){
		this.id = id;
		this.name = name;
		this.owner = user;
		this.sync = sync;
		this.events = new ArrayList<GEvent>();
	}
	
	public GCalendar(long id, String name, User user, boolean sync, int colour){
		this.id = id;
		this.name = name;
		this.owner = user;
		this.sync = sync;
		this.colour = colour;
		this.events = new ArrayList<GEvent>();
	}
	
	/********************* GETTERS AND SETTERS ********************/

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}
	
	public int getColour() {
		return colour;
	}

	public void setColour(int colour) {
		this.colour = colour;
	}

	public boolean isSync() {
		return sync;
	}

	public void setSync(boolean sync) {
		this.sync = sync;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User user) {
		this.owner = user;
	}

	public List<GEvent> getEvents() {
		return events;
	}
	
	public boolean addEvent(GEvent e){
		return events.add(e);
	}
}
