package hu.edudroid.ict_plugin_cell;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CellListener extends BroadcastReceiver {

	private static final String TAG = CellListener.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "Event received: " + intent.getAction());
		PluginCommunicationInterface communicationInterface = new PluginCommunicationInterface(new CellPlugin());
		// Subscribe to screen event when any broadcast is received
		communicationInterface.onReceive(context, intent);
	}	
}
