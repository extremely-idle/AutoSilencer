package uk.co.rm.android.AutoSilencer.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableNotifiedException;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.calendar.model.*;


import uk.co.rm.android.AutoSilencer.R;
import uk.co.rm.android.AutoSilencer.db.CalendarDBManager;
import uk.co.rm.android.AutoSilencer.obj.GCalendar;
import uk.co.rm.android.AutoSilencer.obj.GEvent;
import uk.co.rm.android.AutoSilencer.obj.User;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.util.Log;
import android.widget.TextView;

/**
 * 
 * @author Ross Moug
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class AutoSilencerService extends Service implements OnSharedPreferenceChangeListener {

	/* Service Variables */
	private static final String TAG = "AutoSilencerService";
	private final AutoSilencerBinder binder = new AutoSilencerBinder(this);
	private Timer timer;
	private boolean first = true;
	private NotificationManager notificationManager;
	public CalendarDBManager dbManager;

	/* User-Settable Variables */
	private SharedPreferences prefs;
	// @todo
	private static final String ACCOUNT_NAME = "accountName";
	private static String accountName;
	// Calendars must be updated periodically, this variable specifies the periodicity.
	// By default: every hour
	// (other options: every 15 minutes, 30 minutes, 3 hours, 6 hours, 12 hours, 24 hours, 48 hours).
	// @todo: define all updateInterval options in an enum.
	private static final String UPDATE_INTERVAL = "notifications_enabled";
	private static int updateInterval = 60000; // 60 seconds FOR TESTING ONLY
	// When calendars are updated, what time slice size should be used
	// By default: this is 24 hours
	// (other options: 12 hours, 48 hours, 1 week, 2 weeks, 1 month, 3 months)
	// @todo: define all updateTimeSliceSize options in an enum.
	private static final String TIME_SLICE_SIZE = "time_slice_size";
	private static int updateTimeSliceSize = 86400000; 
	// @todo
	private static final String NOTIFICATIONS_ENABLED = "notifications_enabled";
	private static boolean notificationsEnabled = false;
	// Requests will have "buffer" between event start and setting
	// device to the required ringer mode
	// By default: 3 minutes
	// (other options: 15 seconds, 30 seconds, 1 minute, 2 minutes, 4 minutes, 5 mnutes)
	// @todo: define all scheduleTolerance options in an enum.
	private static final String SCHEDULE_TOLERANCE = "schedule_tolerance";
	private static int scheduleTolerance = 180000;
	// Number of events (excluding all days events) held in memory at any given time.
	// By default: 15 events per calendar
	// @todo: experiment to see where performance issues arise due to number of events.
	private static final String EVTS_PER_CAL_IN_MEM = "active_events_per_cal";
	private static int eventsPerCalendarInMemory = 15;

	/* Calendar Objects/Variables */
	private User user;
	//private CalendarService calService;
	// Amount of data stored dependent on eventsPerCalendarInMemory user-settable value
	private HashMap<Calendar, GEvent> evtMap;

	/* Android System Variables */
	private AudioManager audio;
	private int prevRingerMode;

	/* Google Objects */
	private GoogleAccountCredential credential;
	private com.google.api.services.calendar.Calendar client;
	private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
	private final JsonFactory jsonFactory = new AndroidJsonFactory();
	private GoogleAccountManager accountManager;

	/* Google Variables */
	private final String service = "cl";
	private final String comp = "RMoug";
	private final String app_name = "AutoSilencer";
	private final String version = "0.1";			
	private final String app = comp + "-" + app_name + "-" + version;

	/* Calendar Table Indices */
	public static final String[] CAL_PROJECTION = new String[] {
		Calendars._ID,                           // 0
		Calendars.ACCOUNT_NAME,                  // 1
		Calendars.CALENDAR_DISPLAY_NAME,         // 2
		Calendars.OWNER_ACCOUNT                  // 3
	};

	private static final int PROJECTION_ID_INDEX = 0;
	private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
	private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
	private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;

	/* Event Table Indices */
	public static final String[] EVENT_PROJECTION = new String[] {
		Events.CALENDAR_ID,                      // 0
		Events.TITLE,                            // 1
		Events.ORGANIZER,                        // 2
		Events.EVENT_LOCATION,                   // 3
		Events.DTSTART,                          // 4
		Events.DTEND,                            // 5
		Events.EVENT_TIMEZONE,                   // 6
		Events.EVENT_END_TIMEZONE,               // 7
		Events.ALL_DAY,                          // 8
	};

	private static final int EVT_PROJECTION_CALENDAR_ID_INDEX = 0;
	private static final int EVT_PROJECTION_TITLE_INDEX = 1;
	private static final int EVT_PROJECTION_ORGANIZER_INDEX = 2;
	private static final int EVT_PROJECTION_EVENT_LOCATION_INDEX = 3;
	private static final int EVT_PROJECTION_DTSTART_INDEX = 4;
	private static final int EVT_PROJECTION_DTEND_INDEX = 5;
	private static final int EVT_PROJECTION_EVENT_TIMEZONE_INDEX = 6;
	private static final int EVT_PROJECTION_EVENT_END_TIMEZONE_INDEX = 7;
	private static final int EVT_PROJECTION_ALL_DAY_INDEX = 8;

	/*********************************************************/

	/**
	 * 
	 * @author Ross Moug
	 */
	public class AutoSilencerBinder extends Binder {
		private AutoSilencerService ss;
		public AutoSilencerBinder(AutoSilencerService ss){
			this.ss = ss;
		}
		public AutoSilencerService getService() {
			return AutoSilencerService.this;
		}
		public User getSessionUser(){
			return user;
		}
	}


	/**
	 * 
	 */
	public AutoSilencerService(){
		Log.i(TAG, "<*** Constructor Called ***>");
	}

	/********************* ANDROID STANDARD METHODS ********************/

	/**
	 * 
	 */
	public void onCreate() {
		Log.i(TAG, "<*** On Create ***>");
		super.onCreate();
	}

	/**
	 * 
	 */
	public void onDestroy() {
		Log.i(TAG, "<*** On Destroy ***>");
		super.onDestroy();
	}

	/**
	 * 
	 * @param name
	 */
	public boolean stopService(Intent name) {
		return super.stopService(name);
	}

	/**
	 *
	 * @param intent
	 */
	public IBinder onBind(Intent intent) {
		return binder;
	}

	/**
	 * 
	 * @param intent
	 * @param startId
	 */
	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "<*** On Start ***>");
		super.onStart(intent, startId);
		parseBundle(intent);
		Log.i(TAG, "Passed account name: "+accountName);

		// Setup
		dbManager = new CalendarDBManager(this);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(this);
		// Google Accounts
		credential = GoogleAccountCredential.usingOAuth2(this, CalendarScopes.CALENDAR);
		credential.setSelectedAccountName(accountName);
		//		
		//		AccountManager am = AccountManager.get(this);
		//		Bundle options = new Bundle();
		//
		//		am.getAuthToken(
		//		    myAccount_,                     // Account retrieved using getAccountsByType()
		//		    CalendarScopes.CALENDAR         // Auth scope
		//		    options,                        // Authenticator-specific options
		//		    this,                           // Your activity
		//		    new OnTokenAcquired(),          // Callback called when a token is successfully acquired
		//		    new Handler(new OnError()));    // Callback called if an error occurs

		user = new User(accountName);
		// Calendar client
		client = new com.google.api.services.calendar.Calendar.Builder(
				transport, jsonFactory, credential)
		.setApplicationName("Google-CalendarAndroidSample/1.0")
		.build();

		setupService();
	}

	/********************* SERVICE SPECIFIC METHODS ********************/

	/**
	 * 
	 */
	private void setupService(){
		timer = new Timer();
		timer.scheduleAtFixedRate(
				new TimerTask() {
					public void run() {
						if(first){
							if(!init()){
								System.err.println("Initialisation failed.");// GIVE ERROR
							} else
								first = false;
						} else
							try {
								update();
							} catch (IOException ioe) {
								System.err.println("AuthenticationException occured: "+ioe.getMessage());
							}
					}
				},
				0,
				updateInterval);
		Log.i(getClass().getSimpleName(), "Timer started!!!");
	}

	/**
	 * 
	 */
	private boolean init() {
		try{
			update();
			return true;
		} catch(Exception e){
			System.err.println("Exception occured: "+e.getMessage());
			return false;
		}
	}

	/**
	 * Only fire notifications if notificationsEnabled is set to true.
	 */
	private void fireNotification() {
		// @todo
		Log.i(TAG, "<*** Fire Notification ***>");

		String notContext = Context.NOTIFICATION_SERVICE;
		notificationManager = (NotificationManager) getSystemService(notContext);
	}

	/**
	 * Takes the form of period update of the calendar entries...only handle the delta? (more efficient)
	 * @throws ServiceException 
	 * @throws IOException 
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private void update() throws IOException {
		Log.i(TAG, "<<< Update triggered >>>");
		// Run query
		Cursor cur = null;
		ContentResolver cr = getContentResolver();
		Uri uri = Calendars.CONTENT_URI;   
		String selection = "((account_name=" + Calendars.ACCOUNT_NAME + " = ?)" +
				" AND (account_type=" + Calendars.ACCOUNT_TYPE + " = ?) " +
				" AND (ownerAccount="+ Calendars.OWNER_ACCOUNT + " = ?))";
		String[] selectionArgs = {credential.getSelectedAccountName(), "com.google",
				credential.getSelectedAccountName()};
		Log.i(TAG, "<<< cred_acc_name: "+credential.getSelectedAccountName()+" >>>");
		// Submit the query and get a Cursor object back. 
		cur = cr.query(uri, CAL_PROJECTION, selection, selectionArgs, null);
		Log.i(TAG, "<<< Update: Calendar cursor count: "+cur.getCount()+" >>>");
		// Use the cursor to step through the returned records
		while (cur.moveToNext()) {
			long calID = 0;
			String displayName = null;
			String accountName = null;
			String ownerName = null;

			// Get the field values
			calID = cur.getLong(PROJECTION_ID_INDEX);
			displayName = cur.getString(PROJECTION_DISPLAY_NAME_INDEX);
			accountName = cur.getString(PROJECTION_ACCOUNT_NAME_INDEX);
			ownerName = cur.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
			Log.i(TAG, "<<< Update: Calendar "+displayName+" found! >>>");
			// Loop through HashMap
			// - check whether local GCalendar object exists, update if present, create new if not and add to map

			// select events for the given calendar up to the event limit    
			Cursor evtCur = null;
			ContentResolver ecr = getContentResolver();
			Uri evtUri = Events.CONTENT_URI;   
			String evtSelection = "((calendar_id" + Events.CALENDAR_ID + " = ?) AND " +
					"(allDay="+ Events.ALL_DAY + " = 0))";
			String[] evtSelectionArgs = {String.valueOf(calID)};
			// Submit the query and get a Cursor object back. 
			evtCur = ecr.query(evtUri, EVENT_PROJECTION, evtSelection, evtSelectionArgs, null);

			GCalendar cal = new GCalendar(calID, displayName, user, false);

			while(evtCur.moveToNext()){
				String evtName = null;
				String orgMail = null;
				String evtLoc = null;
				String startTime = null;
				String endTime = null;
				String evtStartTZ = null;
				String evtEndTZ = null;
				boolean allDay = false;

				// Get the field values
				calID = cur.getLong(EVT_PROJECTION_CALENDAR_ID_INDEX);
				evtName = cur.getString(EVT_PROJECTION_TITLE_INDEX);
				orgMail = cur.getString(EVT_PROJECTION_ORGANIZER_INDEX);
				evtLoc = cur.getString(EVT_PROJECTION_EVENT_LOCATION_INDEX);
				startTime = cur.getString(EVT_PROJECTION_DTSTART_INDEX);
				endTime = cur.getString(EVT_PROJECTION_DTEND_INDEX);
				evtStartTZ = cur.getString(EVT_PROJECTION_EVENT_TIMEZONE_INDEX);
				evtEndTZ = cur.getString(EVT_PROJECTION_EVENT_END_TIMEZONE_INDEX);
				switch (cur.getInt(EVT_PROJECTION_ALL_DAY_INDEX)){
				case 0: allDay = false; break;
				case 1: allDay = true;  break;
				}
				// Save the data
				// Checking if the GEvent object exists already
				GEvent evt = new GEvent(evtName, orgMail, evtLoc, new Date(), new Date(), TimeZone.getTimeZone(evtStartTZ), TimeZone.getTimeZone(evtEndTZ), cal);
				cal.addEvent(evt); // is this necessary?
				dbManager.insertEvent(evt);
			}
		}

		// To be done by scheduler?
		/*setRingerMode(AudioManager.RINGER_MODE_SILENT);
		restoreRingerMode();*/
	}

	private boolean setRingerMode(int mode){
		audio = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
		prevRingerMode = audio.getRingerMode();
		audio.setRingerMode(mode);

		if(audio.getRingerMode() == mode)
			return true;
		else
			return false; // retry?

	}

	private void restoreRingerMode(){
		setRingerMode(prevRingerMode);

	}

	/**
	 * Parse infomration received from the UI.
	 * @param intent
	 */
	private void parseBundle(Intent intent) {
		Bundle extraInfo = intent.getExtras();
		if(extraInfo.getString(ACCOUNT_NAME) != null)
			accountName = (String) extraInfo.getString(ACCOUNT_NAME);
	}

	/**
	 * Update user-settable values via SharedPreferences.
	 */
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if(key.equalsIgnoreCase(UPDATE_INTERVAL))
			updateInterval = prefs.getInt(UPDATE_INTERVAL, updateInterval);
		else if(key.equalsIgnoreCase(NOTIFICATIONS_ENABLED))
			notificationsEnabled = prefs.getBoolean(NOTIFICATIONS_ENABLED, notificationsEnabled);
		else if(key.equalsIgnoreCase(SCHEDULE_TOLERANCE))
			scheduleTolerance = prefs.getInt(SCHEDULE_TOLERANCE, scheduleTolerance);
		else if(key.equalsIgnoreCase(TIME_SLICE_SIZE))
			updateTimeSliceSize = prefs.getInt(key, updateTimeSliceSize);
		else if(key.equalsIgnoreCase(EVTS_PER_CAL_IN_MEM))
			eventsPerCalendarInMemory = prefs.getInt(EVTS_PER_CAL_IN_MEM, eventsPerCalendarInMemory);
		else if(key.equalsIgnoreCase(ACCOUNT_NAME))
			accountName = prefs.getString(ACCOUNT_NAME, null);
	}
}
