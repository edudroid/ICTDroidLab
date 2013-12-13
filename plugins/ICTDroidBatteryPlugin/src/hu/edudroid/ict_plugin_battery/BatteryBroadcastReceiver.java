package hu.edudroid.ict_plugin_battery;

import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		BatteryPlugin batteryPlugin = BatteryPlugin.getInstance();
		Map<String, Object> values = BatteryPlugin.processIntent(intent);
		batteryPlugin.event(BatteryPlugin.BATTERY_LEVEL_CHANGED, values, context);
	}
}