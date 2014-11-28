package hu.edudroid.ict_plugin_battery;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.BatteryConstants;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

public class BatteryListener extends BroadcastReceiver {

	private static final String TAG = BatteryListener.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "Something received.");
		context.startService(new Intent(context, BatteryService.class));
		PluginCommunicationInterface communicationInterface = new PluginCommunicationInterface(new BatteryPlugin());
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		Intent batteryStatus = ((Context) context).registerReceiver(null, ifilter);
		Map<String, Object> values = BatteryPlugin.processIntent(batteryStatus);
		if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
			Log.e(TAG, "Battery changed");
			communicationInterface.fireEvent(BatteryConstants.BATTERY_LEVEL_CHANGED, values, context);
		} else if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
			Log.e(TAG, "Power connected");
			communicationInterface.fireEvent(BatteryConstants.CHARGING_STATE_CHANGED, values, context);
		} else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
			Log.e(TAG, "Power disconnected");
			communicationInterface.fireEvent(BatteryConstants.CHARGING_STATE_CHANGED, values, context);
		}else {
			communicationInterface.onReceive(context, intent);
		}
	}	
}
