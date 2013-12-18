package hu.edudroid.ict.plugins;

import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginListener;
import hu.edudroid.interfaces.PluginResultListener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

public class PluginIntentReceiver extends BroadcastReceiver {
	
	private final String TAG = PluginIntentReceiver.class.getName();
	
	SharedPreferences profiling;

	private final HashSet<PluginListener> pluginListeners = new HashSet<PluginListener>();
	private final HashSet<PluginResultListener> mResultListeners = new HashSet<PluginResultListener>();
	private final HashSet<PluginEventListener> mPluginEventListeners = new HashSet<PluginEventListener>();

	public void registerPluginDetailsListener(PluginListener listener){
		pluginListeners.add(listener);
	}

	public void registerResultListener(PluginResultListener listener){
		mResultListeners.add(listener);
	}
	
	public void registerEventListener(PluginEventListener listener){
		mPluginEventListeners.add(listener);
	}

	public void unregisterPluginDetailsListener(PluginListener listener){
		pluginListeners.remove(listener);
	}

	public void unregisterEventListener(PluginEventListener listener){
		mPluginEventListeners.remove(listener);
	}

	public void unregisterResultListener(PluginResultListener listener){
		mResultListeners.remove(listener);
	}

	@Override
	public void onReceive(Context context, Intent intent){
		
		profiling = context.getSharedPreferences("profilepref",Context.MODE_WORLD_READABLE);
		
		Log.d(TAG, "onReceive: " + intent.getAction());
		
		final Bundle extras = intent.getExtras();

		if (extras == null)
			return;
		
		if(intent.getAction().equals(Constants.INTENT_ACTION_PROFILING_RESET)){
			intent.getExtras().getString(Constants.INTENT_EXTRA_KEY_PROFILING_RESET);
			Map<String,?> profileddatas = profiling.getAll();
			Set<String> keys = profileddatas.keySet();
			for (Iterator<String> i = keys.iterator(); i.hasNext();){
				String data = (String) i.next();
				SharedPreferences.Editor editor = profiling.edit();
				editor.putInt(data, 0);
				editor.commit();
				
			}
			
		}
		
		if(intent.getAction().equals(Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER)){
			final long id = extras.getLong(Constants.INTENT_EXTRA_CALL_ID);
			final String plugin = extras.getString(Constants.INTENT_EXTRA_KEY_PLUGIN_ID);
			final String version = extras.getString(Constants.INTENT_EXTRA_KEY_VERSION);
			final String method = extras.getString(Constants.INTENT_EXTRA_METHOD_NAME);
			final List<String> result = extras.getStringArrayList(Constants.INTENT_EXTRA_VALUE_RESULT);

			for ( PluginResultListener listener : mResultListeners) {
				listener.onResult(id,	plugin,version, method, result);
				
				SharedPreferences.Editor editor = profiling.edit();
				
				int number = getCalledMethodNumber(method) + 1;
				editor.putInt(method, number);
				editor.commit();				
			}
		}
		if(intent.getAction().equals(Constants.INTENT_ACTION_DESCRIBE)){
			if(extras.getString(Constants.INTENT_EXTRA_KEY_DESCRIBE_TYPE).equals(Constants.INTENT_EXTRA_VALUE_REPORT)) {
				for (PluginListener listener: pluginListeners) {
				listener.newPlugin(new PluginAdapter(
							extras.getString(Constants.INTENT_EXTRA_KEY_PLUGIN_ID),
							extras.getString(Constants.INTENT_EXTRA_KEY_PLUGIN_AUTHOR),
							extras.getString(Constants.INTENT_EXTRA_KEY_DESCRIPTION),
							extras.getString(Constants.INTENT_EXTRA_KEY_VERSION),
							extras.getStringArrayList(Constants.INTENT_EXTRA_KEY_PLUGIN_METHODS),
							extras.getStringArrayList(Constants.INTENT_EXTRA_KEY_PLUGIN_EVENTS),
							this,
							context));
				}
			}
			if(extras.getString(Constants.INTENT_EXTRA_KEY_DESCRIBE_TYPE).equals(Constants.INTENT_EXTRA_VALUE_ERROR)){
				final long id = extras.getLong(Constants.INTENT_EXTRA_CALL_ID);
				final String plugin = extras.getString(Constants.INTENT_EXTRA_KEY_PLUGIN_ID);
				final String method = extras.getString(Constants.INTENT_EXTRA_METHOD_NAME);
				final String version = extras.getString(Constants.INTENT_EXTRA_KEY_VERSION);
				final String errorMessage = extras.getString(Constants.INTENT_EXTRA_KEY_ERROR_MESSAGE);
				for (PluginResultListener listener : mResultListeners) {
				listener.onError(id, plugin, version, method, errorMessage);
				}
			}
		}
		
		if(intent.getAction().equals(Constants.INTENT_ACTION_PLUGIN_EVENT)){
			final String plugin = extras.getString(Constants.INTENT_EXTRA_KEY_PLUGIN_ID);
			final String version = extras.getString(Constants.INTENT_EXTRA_KEY_VERSION);
			final String eventName = extras.getString(Constants.INTENT_EXTRA_KEY_EVENT_NAME);
			final List<String> result = extras.getStringArrayList(Constants.INTENT_EXTRA_VALUE_RESULT);
			for (PluginEventListener listener : mPluginEventListeners){
				listener.onEvent(plugin, version, eventName, result);
				SharedPreferences.Editor editor = profiling.edit();
				
				int number = getCalledMethodNumber(eventName) + 1;
				editor.putInt(eventName, number);
				editor.commit();	
			}
		}
	}
	
	private int getCalledMethodNumber(String method){
		int number = 0;
		if(profiling.contains(method)){
			number = profiling.getInt(method, 0);
		}		
		return number;
	}
}