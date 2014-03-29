package hu.edudroid.ict_plugin_battery;

import hu.edudroid.ictplugin.PluginCommunicationInterface;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenListener extends BroadcastReceiver {

	private static final String TAG = ScreenListener.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		PluginCommunicationInterface communicationInterface = new PluginCommunicationInterface(BatteryPlugin.getInstance());
		// Subscribe to screen event when any broadcast is received
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
			Log.d(TAG, "Screen off");
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(BatteryPlugin.SCREEN_STATE, BatteryPlugin.SCREEN_STATE_OFF);
			communicationInterface.fireEvent(BatteryPlugin.SCREEN_STATE_CHANGED, values, context);
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
			Log.d(TAG, "Screen on");
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(BatteryPlugin.SCREEN_STATE, BatteryPlugin.SCREEN_STATE_ON);
			communicationInterface.fireEvent(BatteryPlugin.SCREEN_STATE_CHANGED, values, context);
		}else {
			Log.e(TAG, "Unknown event received: " + intent.getAction());
		}
	}	
}
