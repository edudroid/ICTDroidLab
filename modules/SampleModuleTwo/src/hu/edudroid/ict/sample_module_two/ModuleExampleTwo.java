package hu.edudroid.ict.sample_module_two;

import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ModuleExampleTwo extends Module {
	
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
	
	private static final String SAMPLE_PLUGIN_NAME = "Sample plugin";
	
	public ModuleExampleTwo(Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeservice) {
		super(prefs, logger, pluginCollection, timeservice);
	}
	
	private static final String TAG = "ModuleExample";

	private static final String FIRST_METHOD = "First sample method";
	
	@Override
	public void init(){
		mLogger.e(TAG, "Module init...");
		mTimeService.runPeriodic(1000, 5000, 0, this);
	}
	
	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, Map<String, Object> result) {
		if (result != null) {
			mLogger.i(TAG, "Result received " + result.size());
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
		mLogger.i(TAG, "Event received");
	}

	@Override
	public void onTimerEvent() {
		mLogger.d(TAG, "timer event");
		mLogger.i(TAG, "TWO module example run at " + dateFormatter.format(new Date()));
		Plugin plugin = mPluginCollection.getPluginByName(SAMPLE_PLUGIN_NAME);
		if (plugin != null) {
			plugin.callMethodAsync(FIRST_METHOD, null, this);
		}
	}
} 