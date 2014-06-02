package hu.edudroid.ict.simple_wifi_module;

import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.SimpleWiFiConstants;
import hu.edudroid.interfaces.TimeServiceInterface;
import java.util.Map;

public class SimpleWifiModule extends Module {
	
	private Plugin wifiPlugin;

	public SimpleWifiModule(Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeservice) {
		super(prefs, logger, pluginCollection, timeservice);
		wifiPlugin = pluginCollection.getPluginByName(SimpleWiFiConstants.PLUGIN_NAME);
	}
	
	private static final String TAG = SimpleWifiModule.class.getName();

	@Override
	public void init(){
		if (wifiPlugin != null) {
			wifiPlugin.registerEventListener(SimpleWiFiConstants.EVENT_WIFI_SIGNAL_STRENGTH_CHANGED, this);
		}
	}
	
	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, Map<String, Object> result) {
	}


	@Override
	public void onError(long id, String plugin, String pluginVersion, String methodName,
			String errorMessage) {
	}

	@Override
	public void onEvent(String plugin, String version, String eventName, Map<String, Object> extras) {
		mLogger.i(TAG, "" + extras.get(SimpleWiFiConstants.KEY_WIFI_STRENGTH));
	}

	@Override
	public void onTimerEvent() {
	}
}