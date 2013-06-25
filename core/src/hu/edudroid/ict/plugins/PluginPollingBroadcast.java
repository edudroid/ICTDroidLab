package hu.edudroid.ict.plugins;

import java.util.ArrayList;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class PluginPollingBroadcast extends BroadcastReceiver {

	private final ArrayList<PluginResultListener>	mResultListeners;

	private PluginListener							mListener;
	private static PluginPollingBroadcast			mInstance;

	private PluginPollingBroadcast() {
		mResultListeners = new ArrayList<PluginResultListener>();
	}

	public static PluginPollingBroadcast getInstance(){
		if (mInstance == null){
			synchronized (PluginPollingBroadcast.class){
				if (mInstance == null)
					mInstance = new PluginPollingBroadcast();
			}
		}
		return mInstance;
	}

	public void registerPluginDetailsListener(PluginListener listener){
		mListener = listener;
	}

	public void registerResultListener(PluginResultListener listener){
		mResultListeners.add(listener);
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
		final String action = extras.getString("action");

		if (action.equals("reportSelf"))
			mListener.newPlugin(new Plugin(	extras.getString("title"),
											extras.getString("author"),
											extras.getString("description"),
											extras.getString("version"),
											context));
		if (action.equals("reportMethods")){
			Log.d("CORE::PluginPollingBroadcast:onReceive","ReportMethods broadcast received - " + extras.getString("name"));
			mListener.newPluginMethod(new PluginMethod(	extras.getInt("order"),
					extras.getString("name"),
					extras.getString("description")));
		}
			
		if (action.equals("reportResult")){
			final String plugin = extras.getString("plugin");
			final String version = extras.getString("version");
			final String method = extras.getString("sender");
			final String result = extras.getString("result");
			final String metadata = extras.containsKey("meta") ? extras.getString("meta") : "";

			for (int i = 0; i < mResultListeners.size(); i++)
				mResultListeners.get(i).onResult(	plugin,
													version,
													method,
													result,
													metadata);
		}

		if (action.equals("reportError")){
			final String plugin = extras.getString("plugin");
			final String version = extras.getString("version");
			final String method = extras.getString("sender");
			final String error = extras.getString("message");
			final String metadata = extras.getString("meta");
			nofityResultListenersAboutError(plugin,
											version,
											method,
											error,
											metadata);
		}
	}

	public void nofityResultListenersAboutError(final String plugin,
												final String version,
												final String method,
												final String error,
												final String metadata){
		for (int i = 0; i < mResultListeners.size(); i++)
			mResultListeners.get(i).onError(plugin,
											version,
											method,
											error,
											metadata);
	}

}