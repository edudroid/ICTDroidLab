package hu.edudroid.ictpluginwifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class MainActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		Intent startServiceIntent = new Intent(this, WiFiPluginService.class);
        this.startService(startServiceIntent);
	}
} 