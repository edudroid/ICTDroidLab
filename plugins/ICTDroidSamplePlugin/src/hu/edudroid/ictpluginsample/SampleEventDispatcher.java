package hu.edudroid.ictpluginsample;

import hu.edudroid.ictplugin.PluginCommunicationInterface;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SampleEventDispatcher extends BroadcastReceiver {

	private static final String TAG = SampleEventDispatcher.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "Intent received " + intent.getAction());		
		context.startService(new Intent(context, SampleService.class));
		PluginCommunicationInterface communicationInterface =
				new PluginCommunicationInterface(new SamplePlugin());
		communicationInterface.onReceive(context, intent);
		communicationInterface.fireEvent("SampleEvent", new HashMap<String, Object>(), context);
	}	
}