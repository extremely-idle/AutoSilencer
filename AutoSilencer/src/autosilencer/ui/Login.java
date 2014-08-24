package uk.co.rm.android.AutoSilencer.obj;

import uk.co.rm.android.AutoSilencer.R;
import uk.co.rm.android.AutoSilencer.R.drawable;
import uk.co.rm.android.AutoSilencer.R.id;
import uk.co.rm.android.AutoSilencer.R.layout;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @author Ross Moug
 */
public class Login extends Activity {

	private static final String TAG = "AutoSilencerMain";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_list);
        
        init();
    }

	private void init() {
		// Setup login mode.
		TextView userName = (TextView) findViewById(R.id.userName);
        userName.setText("Username", TextView.BufferType.SPANNABLE);
        ImageView img = (ImageView) findViewById(R.id.userAvatar);
        Drawable avatarDraw = getApplicationContext().getResources().getDrawable(R.drawable.android_chat_avatar);
        img.setImageDrawable(avatarDraw);
        
        Spannable span = (Spannable) userName.getText();
        span.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        
        RelativeLayout userData = (RelativeLayout) findViewById(R.id.userData);
        userData.setBackgroundColor(Color.DKGRAY);
	}
	
	@Override
	public void onDestroy(){
		Log.i(TAG, "<*** On Destroy ***>");
		//this.unbindService(sc);
		super.onDestroy();
	}
	
	@Override
	public void onPause(){
		Log.i(TAG, "<*** On Pause ***>");
		super.onPause();
	}
	
	@Override
	public void onResume(){
		Log.i(TAG, "<*** On Resume ***>");
		super.onResume();
	}	
	
	@Override
	public void onStop(){
		Log.i(TAG, "<*** On Stop ***>");
		super.onStop();
	}
}
