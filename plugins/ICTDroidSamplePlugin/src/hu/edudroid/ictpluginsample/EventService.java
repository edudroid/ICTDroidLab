package hu.edudroid.ictpluginsample;

import hu.edudroid.interfaces.Constants;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class EventService extends Service {
	private static final String TAG = EventService.class.getName();

	public static final String PLUGIN_NAME="Sample Plugin";
	
	
	private boolean started = false;
	
	private EventBinder binder = new EventBinder(this);
	
	public static class EventBinder extends Binder {
		private EventService service;
		
		public EventBinder(EventService service) {
			this.service = service;
		}
		
		public EventService getService() {
			return service;
		}
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
		if (!started) {
			Log.i(TAG, "Starting EventService!");
			
			started = true;
			SharedPreferences sharedPrefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			Map<String,?> limits = sharedPrefs.getAll();
			Set<String> methods = limits.keySet();
			for (Iterator<String> i = methods.iterator(); i.hasNext();){
				String method = (String) i.next();
				String limit = (String) limits.get(method);
				Intent myintent = new Intent();
				myintent = new Intent(Constants.INTENT_ACTION_PLUGIN_LIMITS);
				myintent.putExtra(Constants.INTENT_EXTRA_KEY_METHOD_NAME,method);
				myintent.putExtra(Constants.INTENT_EXTRA_KEY_METHOD_LIMIT,limit);
				this.sendBroadcast(myintent);
			}
			/*
			SamplePlugin samplePlugin = SamplePlugin.getInstance();
			List<String> result;
			result = new ArrayList<String>();
			result.add("something");
			while(true) {
				try {
					
					Thread.sleep((long)(Math.random() * 1000));
					samplePlugin.event(SamplePlugin.FIRST_SAMPLE_EVENT_NAME, result, this);
					Log.d(TAG, "First event fired.");
					Thread.sleep((long)(Math.random() * 1000));
					samplePlugin.event(SamplePlugin.SECOND_SAMPLE_EVENT_NAME, result, this);
					Log.d(TAG, "Second event fired.");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}*/
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
}