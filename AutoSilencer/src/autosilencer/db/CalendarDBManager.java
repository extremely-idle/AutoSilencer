package uk.co.rm.android.AutoSilencer.db;

import java.util.ArrayList;
import java.util.List;

import uk.co.rm.android.AutoSilencer.obj.GEvent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * 
 * @author Ross Moug
 * Used the following tutorial to buld this class for database management.
 * http://ferasferas.wordpress.com/2010/08/16/android-tutorial-%E2%80%93-using-sqlite-databases/ 
 */
public class CalendarDBManager {

	private static final String TAG = "CalendarDBManager";
	private CalendarDatabaseOpener dbAdapter;

	public CalendarDBManager(Context context){
		dbAdapter = new CalendarDatabaseOpener(context);
		dbAdapter.onUpgrade(dbAdapter.getWritableDatabase(), 2, 2);

	}
	
	
	/********************* CALENDAR EVENT METHODS ********************/

	/**
	 * 
	 * @param e
	 * @param ip
	 * @param port
	 */
	public void insertEvent(GEvent e){
		/*********** TODO ***********/
		//Log.i(TAG, "Inserting contact - "+name);
		SQLiteDatabase db = dbAdapter.getWritableDatabase();

		ContentValues initialValues = new ContentValues();
		initialValues.put(CalendarDatabaseOpener.KEY_CALENDAR_ID, e.getCalendar().getId());
		initialValues.put(CalendarDatabaseOpener.KEY_TITLE, e.getName());
		initialValues.put(CalendarDatabaseOpener.KEY_ORGANISER, e.getOrganiser());
		initialValues.put(CalendarDatabaseOpener.KEY_LOCATION, e.getLocation());
		initialValues.put(CalendarDatabaseOpener.KEY_START_TIME, String.valueOf(e.getStartDate()));
		initialValues.put(CalendarDatabaseOpener.KEY_END_TIME, String.valueOf(e.getEndDate()));
		initialValues.put(CalendarDatabaseOpener.KEY_START_TIME, String.valueOf(e.getStartTimezone()));
		initialValues.put(CalendarDatabaseOpener.KEY_END_TIME, String.valueOf(e.getEndTimezone()));

		Log.i(TAG, "Calendar Event DB ID - " + String.valueOf(db.insert(CalendarDatabaseOpener.TABLE, null, initialValues)));
	}
	
	/**
	 * 
	 * @param evts
	 */
	public void updateEvents(List<GEvent> evts){
		for(GEvent e : evts) {
			updateEvent(e);
		}
	}
	
	/**
	 * 
	 * @param e
	 */
	public void updateEvent(GEvent e){
		/*********** TODO ***********/
//		Log.i(TAG, "Updating contact - "+name);
//		SQLiteDatabase db = dbAdapter.getReadableDatabase();
//		Cursor c = db.rawQuery("SELECT * FROM "+dbAdapter.TABLE, null);
//		c.moveToFirst();
//		String current = "";
//		for(int i=0; i<c.getCount(); i++){
//			if(c.getString(1).equalsIgnoreCase(name)){
//				current = c.getString(4);
//				break;
//			}
//			c.moveToNext();
//		}
//		db = dbAdapter.getWritableDatabase();
//
//		ContentValues initialValues = new ContentValues();
//		initialValues.put(dbAdapter.KEY_NAME, name);
//		if(current != null)
//			initialValues.put(dbAdapter.KEY_HISTORY, current+messages);
//		else
//			initialValues.put(dbAdapter.KEY_HISTORY, messages);
//
//		Log.i(TAG, current+messages);
//
//		Log.i(TAG, "Update - "+String.valueOf(db.update(dbAdapter.TABLE, initialValues, dbAdapter.KEY_NAME+"=?", new String[] {name})));
//
//		db = dbAdapter.getReadableDatabase();
//		c = db.rawQuery("SELECT * FROM "+dbAdapter.TABLE, null);
//		c.moveToFirst();
//		current = "";
//		for(int i=0; i<c.getCount(); i++){
//			if(c.getString(1).equalsIgnoreCase(name)){
//				current = c.getString(4);
//				break;
//			}
//			c.moveToNext();
//		}
//		Log.i(TAG, current);
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<GEvent> retrieveEvents(){
		/*********** TODO ***********/
		Log.i(TAG, "Retrieving contacts");
		ArrayList<GEvent> contacts = new ArrayList<GEvent>();
		SQLiteDatabase db = dbAdapter.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * FROM "+dbAdapter.TABLE, null);
		if(c==null)
			Log.e(TAG, "DB FAIL");
		c.moveToFirst();
//		for(int i=0; i<c.getCount(); i++){
//			Log.i(TAG, "Count - "+i);
//			contacts.add(new Friend(null, c.getString(1), true, c.getString(2), c.getInt(3), c.getString(4)));
//			c.moveToNext();
//		}
		return contacts;
	}

	/**
	 * 
	 */
	public void stop(){
		dbAdapter.close();
	}

	/**
	 * 
	 * @author Ross Moug
	 */
	private class CalendarDatabaseOpener extends SQLiteOpenHelper {

		private SQLiteDatabase db;
		private final static String TAG = "CalendarDatabaseOpener";
		private final static int DATABASE_VERSION = 2;
		private final static String TABLE = "auto_silencer_calendar_events";
		private final static String KEY_CALENDAR_ID = "calendar";
		private final static String KEY_TITLE = "title";
		private final static String KEY_ORGANISER = "organiser";
		private final static String KEY_LOCATION =  "location";
		private final static String KEY_START_TIME = "start_time";
		private final static String KEY_END_TIME = "end_time";
		private final static String KEY_START_TIMEZONE = "start_timezone";
		private final static String KEY_END_TIMEZONE = "end_timezone";


		public CalendarDatabaseOpener(Context context) {
			super(context, TABLE, null, DATABASE_VERSION);
		}

		@Override
		/**
		 * 
		 */
		public void onCreate(SQLiteDatabase db) {
			Log.i(TAG, "Creating DB");
			db.execSQL("create table "+TABLE+" (" +
//					BaseColumns._ID + " integer primary key autoincrement, " 
					 KEY_CALENDAR_ID +" TEXT, "
					+ KEY_TITLE+" TEXT, "
					+ KEY_ORGANISER+" TEXT, "
					+ KEY_LOCATION+" TEXT, "
					+ KEY_START_TIME+" TEXT, "
					+ KEY_END_TIME+" TEXT, "
					+ KEY_START_TIMEZONE+" TEXT, "
					+ KEY_END_TIMEZONE+" TEXT);");
		}

		@Override
		/**
		 * 
		 */
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// drop tables.
			// recrete DB.
			db.execSQL("DROP TABLE if exists " +TABLE);
			onCreate(db);
		}
	}
}