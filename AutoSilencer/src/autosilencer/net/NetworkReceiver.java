package uk.co.rm.android.AutoSilencer.net;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

/**
 * 
 * @author Ross Moug
 */
public class NetworkReceiver extends BroadcastReceiver {

	private static final String TAG = "NetworkReceiver";
	
	public NetworkReceiver(){}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "<*** On Receive ***>");
		
		boolean offline = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
		
		if(offline){
			
		} else {
			
		}
	}

}

