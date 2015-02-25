package hu.edudroid.ict.vehicle_module;

import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VehicleModule extends Module {
	
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
	
	public VehicleModule(Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeservice) {
		super(prefs, logger, pluginCollection, timeservice);
	}
	
	private static final String TAG = VehicleModule.class.getName();

	@Override
	public void init(){
		mLogger.e(TAG, "Module init...");
		mTimeService.runPeriodic(1000, 50000, 20, this);	
	}
	
	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, Map<String, Object> result) {
		if (result != null) {
			mLogger.i(TAG, "Result received " + result);
		} else {
			mLogger.i(TAG, "Null received " + result);
		}
	}


	@Override
	public void onError(long id, String plugin, String pluginVersion, String methodName,
			String errorMessage) {
		mLogger.i(TAG, "Error in plugin execution " + errorMessage);
	}

	@Override
	public void onEvent(String plugin, String version, String eventName, Map<String, Object> extras) {
		mLogger.i(TAG, "Event received " + plugin + " " + eventName + " " + extras);
	}

	@Override
	public void onTimerEvent() {
		Plugin plugin = mPluginCollection.getPluginByName("vehicle_plugin");
		plugin.callMethodAsync("get_data", new HashMap<String, Object>(), this);
		mLogger.i(TAG, "Module vehicle 50 sec run at " + dateFormatter.format(new Date()));
	}
}