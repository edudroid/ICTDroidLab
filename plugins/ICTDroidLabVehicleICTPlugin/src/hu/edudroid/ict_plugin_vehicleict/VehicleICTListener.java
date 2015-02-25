package hu.edudroid.ict_plugin_vehicleict;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class VehicleICTListener extends BroadcastReceiver {

	private static final String TAG = VehicleICTListener.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "Event received: " + intent.getAction());
		PluginCommunicationInterface communicationInterface = new PluginCommunicationInterface(VehicleICTPlugin.getInstance());
		// Subscribe to screen event when any broadcast is received
		communicationInterface.onReceive(context, intent);
	}
}
