package hu.edudroid.ictpluginsample;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class LimitSettingActivity extends PreferenceActivity {
	
	 @Override
		    public void onCreate(Bundle savedInstanceState) {
		        super.onCreate(savedInstanceState);
		 
		        addPreferencesFromResource(R.xml.settings);
		 
		    }


}
