package hu.edudroid.ict.plugins;

import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResultListener;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PluginPollingBroadcast extends BroadcastReceiver {

	private final ArrayList<PluginResultListener>	mResultListeners;
	private final ArrayList<PluginEventListener>	mPluginEventListeners;

	private PluginListener							mListener;
	private static PluginPollingBroadcast			mInstance;

	private PluginPollingBroadcast() {
		mResultListeners = new ArrayList<PluginResultListener>();
		mPluginEventListeners = new ArrayList<PluginEventListener>();
	}

	public static PluginPollingBroadcast getInstance(){
		if (mInstance == null){
			synchronized (PluginPollingBroadcast.class){
				if (mInstance == null){
					mInstance = new PluginPollingBroadcast();
				}
			}
		}
		return mInstance;
	}

	public void registerPluginDetailsListener(PluginListener listener){
		mListener = listener;
	}

	public void registerResultListener(PluginResultListener listener){
		if(!mResultListeners.contains(listener)){
			mResultListeners.add(listener);
		}
	}

	public void unregisterResultListener(PluginResultListener listener){
		boolean found = false;
		for (int i = 0; i < mResultListeners.size() && !found; i++)
			if (mResultListeners.get(i) == listener){
				mResultListeners.remove(i);
				found = true;
			}
	}

	@Override
	public void onReceive(Context context, Intent intent){
		
		final Bundle extras = intent.getExtras();

		if (extras == null)
			return;

		if(intent.getAction().equals(Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER)){
			final long id = extras.getLong(Constants.INTENT_EXTRA_CALL_ID);
			final String plugin = extras.getString(Constants.INTENT_EXTRA_KEY_PLUGIN_ID);
			final String version = extras.getString(Constants.INTENT_EXTRA_KEY_VERSION);
			final String method = extras.getString(Constants.INTENT_EXTRA_METHOD_NAME);
			final List<String> result = extras.getStringArrayList(Constants.INTENT_EXTRA_VALUE_RESULT);

			for (int i = 0; i < mResultListeners.size(); i++)
				mResultListeners.get(i).onResult(	id,
													plugin,
													version,
													method,
													result);
		}
		if(intent.getAction().equals(Constants.INTENT_ACTION_DESCRIBE)){
			if(extras.getString(Constants.INTENT_EXTRA_KEY_DESCRIBE_TYPE).equals(Constants.INTENT_EXTRA_VALUE_REPORT)){
				mListener.newPlugin(new PluginAdapter(
							extras.getString(Constants.INTENT_EXTRA_KEY_PLUGIN_ID),
							extras.getString(Constants.INTENT_EXTRA_KEY_PLUGIN_AUTHOR),
							extras.getString(Constants.INTENT_EXTRA_KEY_DESCRIPTION),
							extras.getString(Constants.INTENT_EXTRA_KEY_VERSION),
							extras.getStringArrayList(Constants.INTENT_EXTRA_KEY_PLUGIN_METHODS),
							extras.getStringArrayList(Constants.INTENT_EXTRA_KEY_PLUGIN_EVENTS),
							context));
			}
			if(extras.getString(Constants.INTENT_EXTRA_KEY_DESCRIBE_TYPE).equals(Constants.INTENT_EXTRA_VALUE_ERROR)){
				final long id = extras.getLong(Constants.INTENT_EXTRA_CALL_ID);
				final String plugin = extras.getString(Constants.INTENT_EXTRA_KEY_PLUGIN_ID);
				final String method = extras.getString(Constants.INTENT_EXTRA_METHOD_NAME);
				final String version = extras.getString(Constants.INTENT_EXTRA_KEY_VERSION);
				final String error_message = extras.getString(Constants.INTENT_EXTRA_KEY_ERROR_MESSAGE);
				nofityResultListenersAboutError(id,
												plugin,
												version,
												method,
												error_message);
			}
		}
		
		if(intent.getAction().equals(Constants.INTENT_ACTION_PLUGIN_EVENT)){
			final String eventName = extras.getString("eventName");
			final List<String> eventParams = extras.getStringArrayList("eventParams");
			notifyEventListener(eventName,eventParams);
		}
		
	}

	public void nofityResultListenersAboutError(final long id,
												final String plugin,
												final String version,
												final String method,
												final String error_message){
		for (int i = 0; i < mResultListeners.size(); i++)
			mResultListeners.get(i).onError(id,
											plugin,
											version,
											method,
											error_message);
	}
	
	public void notifyEventListener(String eventName, List<String> eventParams){
		for (int i = 0; i < mPluginEventListeners.size(); i++)
			mPluginEventListeners.get(i).onEvent(eventParams);
	}

}