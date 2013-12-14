package hu.edudroid.ictpluginsample;

import hu.edudroid.interfaces.Constants;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends Activity {

	private static final int RESULT_SETTINGS = 1;
	TextView tv1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv1 = (TextView) findViewById(R.id.textView1);
		getLimit();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getLimit();
	}	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:
			Intent i = new Intent(this, LimitSettingActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			break;
		}
		return true;
	}
	
	public void getLimit(){
		StringBuilder sb = new StringBuilder();
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Map<String,?> limits = sharedPrefs.getAll();
		Set<String> methods = limits.keySet();
		for (Iterator<String> i = methods.iterator(); i.hasNext();){
			String method = (String) i.next();
			String limit = (String) limits.get(method);
			Intent intent = new Intent();
			intent = new Intent(Constants.INTENT_ACTION_PLUGIN_LIMITS);
			intent.putExtra(Constants.INTENT_EXTRA_KEY_METHOD_NAME,method);
			intent.putExtra(Constants.INTENT_EXTRA_KEY_METHOD_LIMIT,limit);
			this.sendBroadcast(intent);
			sb.append(method + ": ");
			if (limit.equals("5"))
			sb.append("Strict Limit\n");
			else if (limit.equals("50"))
				sb.append("Medium Limit\n");
			else if (limit.equals("200"))
				sb.append("Light Limit\n");
			else if (limit.equals("0"))
					sb.append("Limitless\n");
			else{
					sb.append(limit + "\n");
				}
		}
		tv1.setText(sb.toString());
	}
}