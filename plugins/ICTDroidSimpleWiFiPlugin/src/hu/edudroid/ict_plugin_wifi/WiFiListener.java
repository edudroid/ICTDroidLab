package hu.edudroid.ict_plugin_wifi;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.SimpleWiFiConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WiFiListener extends BroadcastReceiver {

	private static final String TAG = WiFiListener.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e(TAG, "Event received: " + intent.getAction());
		PluginCommunicationInterface communicationInterface = new PluginCommunicationInterface(new WiFiPlugin());
		// Subscribe to screen event when any broadcast is received
		if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			Log.e(TAG, "WiFi state changed");
			SupplicantState state = (SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
			Map<String, Object> values = new HashMap<String, Object>();
			if (state != null) { 
				values.put(SimpleWiFiConstants.KEY_WIFI_STATE, state.name());
			} else {
				values.put(SimpleWiFiConstants.KEY_WIFI_STATE, "Unknown state");
			}
			communicationInterface.fireEvent(SimpleWiFiConstants.EVENT_WIFI_STATE_CHANGED, values, context);
		}
		
		else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				Log.e(TAG, "Network state changed");
				NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				Map<String, Object> values = new HashMap<String, Object>();
				if (networkInfo != null) { 
					values.put(SimpleWiFiConstants.KEY_NETWORK_STATE, networkInfo.getState());
				} else {
					values.put(SimpleWiFiConstants.KEY_NETWORK_STATE, "Unknown state");
				}
				communicationInterface.fireEvent(SimpleWiFiConstants.EVENT_WIFI_STATE_CHANGED, values, context);
		}
		
		else if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
			Log.e(TAG, "Signal strength changed");
			int newRssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, 0);
			Map<String, Object> values = new HashMap<String, Object>();
			values.put(SimpleWiFiConstants.KEY_WIFI_STRENGTH, newRssi);
			communicationInterface.fireEvent(SimpleWiFiConstants.EVENT_WIFI_SIGNAL_STRENGTH_CHANGED, values, context);
		}
		
		else if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
			Log.e(TAG, "Scan result received");
			Map<String, Object> values = new HashMap<String, Object>();
			ArrayList<String> networkList = new ArrayList<String>();
			WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			List<ScanResult> scanResults = wifiManager.getScanResults();
			for (ScanResult scanResult : scanResults) {
				networkList.add(scanResult.SSID);
			}
			values.put(SimpleWiFiConstants.KEY_WIFI_NETWORKS, networkList );
			communicationInterface.fireEvent(SimpleWiFiConstants.EVENT_WIFI_SCAN_RESULT_RECEIVED, values, context);

		}

		else {
			communicationInterface.onReceive(context, intent);
		}
	}	
}
