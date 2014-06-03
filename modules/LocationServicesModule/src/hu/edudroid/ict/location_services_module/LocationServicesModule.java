package hu.edudroid.ict.location_services_module;

import hu.edudroid.interfaces.LocationServicesConstants;
import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.SimpleWiFiConstants;
import hu.edudroid.interfaces.TimeServiceInterface;
import java.util.Map;

public class LocationServicesModule extends Module {
	
	private Plugin locationServicesPlugin;

	public LocationServicesModule(Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeservice) {
		super(prefs, logger, pluginCollection, timeservice);
		locationServicesPlugin = pluginCollection.getPluginByName(LocationServicesConstants.PLUGIN_NAME);
	}
	
	private static final String TAG = LocationServicesModule.class.getName();

	@Override
	public void init(){
		mTimeService.runPeriodic(1000, 60000, 0, this);
	}
	
	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, Map<String, Object> result) {
		mLogger.i(TAG, result.get(LocationServicesConstants.KEY_LATITUDE) + "," + result.get(LocationServicesConstants.KEY_LONGITUDE));
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
		if (locationServicesPlugin != null) {
			locationServicesPlugin.callMethodAsync(LocationServicesConstants.METHOD_GET_LOCATION, null, this);
		} else {
			mLogger.i(TAG, "Location plugin not available.");
		}
	}
}