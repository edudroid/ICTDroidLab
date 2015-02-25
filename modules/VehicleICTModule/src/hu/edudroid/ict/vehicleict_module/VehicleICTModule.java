package hu.edudroid.ict.vehicleict_module;

import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;
import hu.edudroid.interfaces.VehicleICTConstants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class VehicleICTModule extends Module {

	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

	private static final String VEHICLEICT_PLUGIN_NAME = VehicleICTConstants.PLUGIN_NAME;

	public VehicleICTModule(Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeservice) {
		super(prefs, logger, pluginCollection, timeservice);
	}

	private static final String TAG = VehicleICTModule.class.getName();

	private static final String SUBSCRIBE_METHOD = VehicleICTConstants.METHOD_SUBSCRIBE;
	//private static final String UNSUBSCRIBE_METHOD = VehicleICTConstants.METHOD_UNSUBSCRIBE;//TODO there's no onFinish() callback

	private boolean subscribed = false;

	@Override
	public void init(){
		mLogger.e(TAG, "Module init...");
		mTimeService.runPeriodic(1000, 30000, 0, this);
	}

	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, Map<String, Object> result) {
		//TODO we don't expect result for the subscribe
		if (result != null) {
			mLogger.i(TAG, "Result received " + result.size());
		} else {
			mLogger.i(TAG, "Null received " + result);
		}
	}


	@Override
	public void onError(long id, String plugin, String pluginVersion, String methodName,
			String errorMessage) {
		//TODO this is never called
		mLogger.i(TAG, "Error in plugin execution " + errorMessage);
	}

	@Override
	public void onEvent(String plugin, String version, String eventName, Map<String, Object> extras) {
		// this is where we receive the knowledge
		mLogger.i(TAG, "Event received " + plugin + " " + version + " " + eventName + " " + extras);
	}

	@Override
	public void onTimerEvent() {
		if (!subscribed) {
			mLogger.i(TAG, "VehicleICT module looking for the plugin " + dateFormatter.format(new Date()));
			Plugin plugin = mPluginCollection.getPluginByName(VEHICLEICT_PLUGIN_NAME);
			if (plugin != null) {
				mLogger.i(TAG, "VehicleICT module trying to subscribe " + dateFormatter.format(new Date()));
				plugin.callMethodAsync(SUBSCRIBE_METHOD, null, this);
				subscribed = true; //TODO there is no feedback on failure
				//TODO there's no way of canceling this timer
			}
		}
	}
}
