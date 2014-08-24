package uk.co.rm.android.AutoSilencer.service;

import java.util.Collection;
import java.util.PriorityQueue;
import java.util.Queue;

import uk.co.rm.android.AutoSilencer.obj.RingerRequest;

/**
 * Schedule ringer mode modification requests.
 * Based on a queue, so this must maintain state correctly for
 * scheduled requests to be processed, so meaning that all requests
 * must be ordered even when requests are scehduled/descheduled/amalgamated.
 * @author Ross Moug
 */
public class Scheduler extends Thread{

	private Queue<RingerRequest> scheduledRequests;
	
	public Scheduler(){
		this.scheduledRequests = new PriorityQueue<RingerRequest>();
	}
	
	/********************* MAIN ENTRY POINT ********************/
	public void run(){
		
	}
	
	/********************* SCHEDULER METHODS ********************/
	/**
	 * Enact the request that is first in the queue
	 * Assumption: the first queue entry is the request which
	 * should triggered first.
	 */
	private void triggerRequest(){
		
	}
	
	
	/*********************** QUEUE METHODS **********************/
	/**
	 * Schedule a ringer request.
	 * @param request request to be scheduled.
	 */
	public boolean schedule(RingerRequest request){
		return false;
	}
	
	/**
	 * Deschedule a ringer request (remove).
	 * @param request request to be descheduled.
	 */
	public void deschedule(RingerRequest request){
		
	}
	
	/********************* HELPER METHODS ********************/
	/**
	 * Merge requests together, case where:
	 * R1 mode=silent, date=12:00, duration=1hr
	 * R2 mode=silent, date=13:00, duration=1hr
	 * R3 mode=silent, date=14:00, duration=1hr
	 * @param requests requests to be merged
	 */
	private void amalgamateRequests(Collection<RingerRequest> requests){
		
	}
}
