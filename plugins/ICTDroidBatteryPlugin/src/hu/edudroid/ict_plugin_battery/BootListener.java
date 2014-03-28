package hu.edudroid.ict_plugin_battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootListener extends BroadcastReceiver {

	private static final String TAG = BootListener.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "Battery plugin received boot event");
		context.startService(new Intent(context, BatteryService.class));
	}
}