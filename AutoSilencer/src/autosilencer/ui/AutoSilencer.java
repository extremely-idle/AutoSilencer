package uk.co.rm.android.AutoSilencer.ui;

import java.io.IOException;
import java.util.ArrayList;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableNotifiedException;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.CalendarScopes;

import uk.co.rm.android.AutoSilencer.R;
import uk.co.rm.android.AutoSilencer.R.drawable;
import uk.co.rm.android.AutoSilencer.R.id;
import uk.co.rm.android.AutoSilencer.R.layout;
import uk.co.rm.android.AutoSilencer.obj.GCalendar;
import uk.co.rm.android.AutoSilencer.obj.User;
import uk.co.rm.android.AutoSilencer.service.AutoSilencerService;
import uk.co.rm.android.AutoSilencer.service.AutoSilencerService.AutoSilencerBinder;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Ross Moug
 */
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class AutoSilencer extends ListActivity {

	/**
	 * TODO:-
	 * 	> Create login page layout and logic.
	 * 		- login needed? just use Google account on phone?
	 * 	> Deal with cross activity/service communication.
	 * 	> Deal with user authentication.
	 * 	> Fully create the main auto silencer layout. ++
	 * 	> Add main logic.
	 * 	> Add in the calendar retrieval code.
	 */

	private static final String TAG = "AutoSilencerMain";
	private User user;
	private static ArrayList<GCalendar> calendars;
	private CalendarAdapter<GCalendar> adapter;
	private ListView calendarList;
	private ServiceConnection sc;
	private AutoSilencerService boundService;

	private static String accountName;

	private GoogleAccountCredential credential;

	private static final String ACCOUNT_NAME = "accountName";
	
	private boolean serviceBound = false;
	
	/* Google Constants */
	static final int REQUEST_AUTHORIZATION = 1;
	static final int REQUEST_ACCOUNT_PICKER = 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "<<< onCreate >>>");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calendar_list);

		calendars = new ArrayList<GCalendar>();
		user = new User();
		// Test data
		//		calendars.add(new GCalendar(1, "Calendar 1", user, false, Color.RED));
		//		calendars.add(new GCalendar(2, "Calendar 2", user, false, Color.BLUE));
		//		calendars.add(new GCalendar(3, "Calendar 3", user, false, Color.GREEN));

		// Google Accounts
		credential = GoogleAccountCredential.usingOAuth2(this, CalendarScopes.CALENDAR);

		chooseAccount();
	}

	@TargetApi(Build.VERSION_CODES.ECLAIR)
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "<<< onActivityResult(req: "+requestCode+", res: "+resultCode+") >>>");
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "<<< onActivityResult pre-switch >>>");
		switch (requestCode) {
		case REQUEST_AUTHORIZATION:
			if (resultCode == Activity.RESULT_OK) {
				Log.i(TAG, "user authenticated, proceed to init");
				// do stuff - pass to service?
				Log.i(TAG, "username: "+credential.getSelectedAccountName());
				init();
			} else {
				Log.i(TAG, "user not authenticated");
				chooseAccount();
			}
			break;
		case REQUEST_ACCOUNT_PICKER:
			Log.i(TAG, "<<< onActivityResult: request picker >>>");
			if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
				String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
				if (accountName != null) {
					credential.setSelectedAccountName(accountName);
					SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(ACCOUNT_NAME, accountName);
					editor.commit();
					Log.i(TAG, "<<< onActivityResult: accoutnName != null: name: "+accountName+" >>>");
					init();
					// do stuff
				}
			}
			break;
		}
	}

	/**
	 * 
	 */
	private void init() {
		Log.i(TAG, "init()");
		user.setUsername(credential.getSelectedAccountName());
		Log.i(TAG, user.getUsername());
		// Start service
		Intent service = new Intent(this, AutoSilencerService.class);
		service.putExtra(ACCOUNT_NAME, user.getUsername());
		startService(service);

		// Bind activity to service, retrieving the user details.
		sc = new ServiceConnection(){
			
			public void onServiceConnected(ComponentName name, IBinder service) {
				boundService = ((AutoSilencerService.AutoSilencerBinder)service).getService();
				//user = ((AutoSilencerService.AutoSilencerBinder)service).getSessionUser();
				serviceBound = true;
			}

			public void onServiceDisconnected(ComponentName name) {
				boundService = null;
				serviceBound = false;
			}

		};

		this.bindService(new Intent(this, AutoSilencerService.class), sc, Context.BIND_AUTO_CREATE);
		
		LinearLayout main = (LinearLayout) findViewById(R.id.main);
		main.setBackgroundColor(Color.WHITE);

		TextView userName = (TextView) findViewById(R.id.userName);
		userName.setText(user.getUsername(), TextView.BufferType.SPANNABLE);
		ImageView img = (ImageView) findViewById(R.id.userAvatar);
		Drawable avatarDraw = getApplicationContext().getResources().getDrawable(R.drawable.android_chat_avatar);
		img.setImageDrawable(avatarDraw);

		Spannable span = (Spannable) userName.getText();
		span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		RelativeLayout userData = (RelativeLayout) findViewById(R.id.userData);
		userData.setBackgroundColor(Color.DKGRAY);

		TextView calBlurb = (TextView) findViewById(R.id.calendarBlurb);
		calBlurb.setText("Available calendars for "+userName.getText(), TextView.BufferType.SPANNABLE);
		calBlurb.setTextColor(Color.BLACK);
		Spannable calSpan = (Spannable) calBlurb.getText();
		calSpan.setSpan(new StyleSpan(android.graphics.Typeface.NORMAL), 0, calBlurb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		calBlurb.setBackgroundColor(Color.WHITE);

		// Init calendar list.
		adapter = new CalendarAdapter<GCalendar>(this, R.layout.calendar, 
				calendars);
		this.setListAdapter(adapter);
		calendarList = getListView();
		calendarList.setTextFilterEnabled(true);
		calendarList.setDivider(null);
		calendarList.setDividerHeight(0);
		calendarList.setBackgroundColor(Color.WHITE);
		calendarList.setCacheColorHint(Color.TRANSPARENT);

		calendarList.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Toast.makeText(getApplicationContext(), calendars.get(pos).getName()+" was clicked", 
						Toast.LENGTH_LONG).show();
				if(calendars.get(pos).isSync())
					calendars.get(pos).setSync(false);
				else
					calendars.get(pos).setSync(true);
				adapter.getView(pos, view, parent);
			}
		});

	}

	@Override
	/**
	 * 
	 */
	public void onDestroy(){
		Log.i(TAG, "<*** onDestroy ***>");
		stopService(new Intent(getApplicationContext(), AutoSilencerService.class));
		super.onDestroy();
	}

	@Override
	/**
	 * 
	 */
	public void onPause(){
		Log.i(TAG, "<*** On Pause ***>");
		super.onPause();
		if (serviceBound) {
            unbindService(sc);
            serviceBound = false;
        }
	}

	@Override
	/**
	 * 
	 */
	public void onResume(){
		Log.i(TAG, "<*** On Resume ***>");
		super.onResume();
		if(serviceBound)
			this.bindService(new Intent(this, AutoSilencerService.class), sc, Context.BIND_AUTO_CREATE);
		if(user.getUsername() == null){
			Log.i(TAG, "<<< onResume: accountName is null >>>");
			chooseAccount();
		} else
			Log.i(TAG, "<<< onResume: accountName: "+user.getUsername()+" >>>");
	}	

	@Override
	/**
	 * 
	 */
	public void onStop(){
		Log.i(TAG, "<*** On Stop ***>");
		super.onStop();
		if (serviceBound) {
            unbindService(sc);
            serviceBound = false;
        }
	}

	private void chooseAccount() {
		Log.i(TAG, "<<< chooseAccount >>>");
		startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
	}

	/**
	 * 
	 * @param calendars
	 */
	public void updateResults(ArrayList<GCalendar> calendars){
		//		if(inetFail)
		//			results.add(new TweetHolder(new Tweet("Twitter could not be reached.\nPlease check your Internet connection.", "", null, false, 0, false, images.get(17), ""), images, this));
		//		else if(tweets.size() == 0)
		//			results.add(new TweetHolder(new Tweet("No tweets found for search query.", "", null, false, 0, false, images.get(17), ""), images, this));

		for(int i=0; i<calendars.size(); i++){
			adapter.getView(i, this.getListView(), this.getListView());
		}
	}

	// Need an action listener to allow the user data to be grabbed and sent over to the 
	// service.
	/**
	 * 
	 */
	public void receiveErrorMessage(){
		// Receive and display error message from the service. 
	}
	
	/********************** GOOGLE AUTHENTICATION **********************/
	
    protected void onError(String msg, Exception e) {
        if (e != null) {
          Log.e(TAG, "Exception: ", e);
        }
//        mActivity.show(msg);  // will be run in UI thread
    }
	
	private String fetchToken() throws IOException {
		try {
			return GoogleAuthUtil.getTokenWithNotification(
					this, credential.getSelectedAccount().name, credential.getScope(), null, makeCallback());
		} catch (UserRecoverableNotifiedException userRecoverableException) {
			// Unable to authenticate, but the user can fix this.
			// Forward the user to the appropriate activity.
			onError("Could not fetch token.", null);
		} catch (GoogleAuthException fatalException) {
			onError("Unrecoverable error " + fatalException.getMessage(), fatalException);
		}
		return null;
	}

	private Intent makeCallback() {
		return null;
//		Intent intent = new Intent();
//		intent.setAction("com.google.android.gms.auth.sample.helloauth.Callback");
//		intent.putExtra(HelloActivity.EXTRA_ACCOUNTNAME, accountName);
//		intent.putExtra(HelloActivity.TYPE_KEY, HelloActivity.Type.BACKGROUND.name());
//		return intent;
	}
}