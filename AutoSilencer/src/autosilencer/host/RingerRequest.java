package uk.co.rm.android.AutoSilencer.obj;

import java.util.Date;

public class RingerRequest {

	private int ringerMode;
	private Date requestedTime;
	//@todo: duration?
	
	public RingerRequest(int mode, Date time){
		this.ringerMode = mode;
		this.requestedTime = time;
	}

	/********************* GETTERS AND SETTERS ********************/
	
	public int getRingerMode() {
		return ringerMode;
	}

	public Date getRequestedTime() {
		return requestedTime;
	}	
}
