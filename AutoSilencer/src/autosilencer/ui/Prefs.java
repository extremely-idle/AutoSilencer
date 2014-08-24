package uk.co.rm.android.AutoSilencer.ui;

import uk.co.rm.android.AutoSilencer.R;
import uk.co.rm.android.AutoSilencer.R.xml;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * 
 * @author Ross Moug
 * Created using http://www.kaloer.com/android-preferences, this also helped 
 * in the creation of the arrays.xml file under res/values/.
 */
public class Prefs extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}
}
