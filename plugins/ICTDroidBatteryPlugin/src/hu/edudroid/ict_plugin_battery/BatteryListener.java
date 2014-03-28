package hu.edudroid.ict_plugin_battery;

import hu.edudroid.ictplugin.PluginCommunicationInterface;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BatteryListener extends BroadcastReceiver {

	private static final String TAG = BatteryListener.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "Something received.");
		context.startService(new Intent(context, BatteryService.class));
		PluginCommunicationInterface communicationInterface = new PluginCommunicationInterface(BatteryPlugin.getInstance());
		// Subscribe to screen event when any broadcast is received
		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
			Log.e(TAG, "Battery changed");
			Map<String, Object> values = BatteryPlugin.processIntent(intent);
			communicationInterface.fireEvent(BatteryPlugin.BATTERY_LEVEL_CHANGED, values, context);
		} else if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
			Log.e(TAG, "Power connected");
			Map<String, Object> values = BatteryPlugin.processIntent(intent);
			communicationInterface.fireEvent(BatteryPlugin.CHARGING_STATE_CHANGED, values, context);
		} else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
			Log.e(TAG, "Power disconnected");
			Map<String, Object> values = BatteryPlugin.processIntent(intent);
			communicationInterface.fireEvent(BatteryPlugin.CHARGING_STATE_CHANGED, values, context);
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
			Log.e(TAG, "Screen on");
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(BatteryPlugin.SCREEN_STATE, BatteryPlugin.SCREEN_STATE_ON);
			communicationInterface.fireEvent(BatteryPlugin.SCREEN_STATE_CHANGED, values, context);
		} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
			Log.e(TAG, "Screen off");
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(BatteryPlugin.SCREEN_STATE, BatteryPlugin.SCREEN_STATE_OFF);
			communicationInterface.fireEvent(BatteryPlugin.SCREEN_STATE_CHANGED, values, context);
		}else {
			communicationInterface.onReceive(context, intent);
		}
	}	
}
