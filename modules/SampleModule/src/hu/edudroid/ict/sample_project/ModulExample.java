package hu.edudroid.ict.sample_project;

import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ModulExample extends Module {
	
	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
	
	public ModulExample(Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeservice) {
		super(prefs, logger, pluginCollection, timeservice);
	}
	
	private static final String TAG = "ModuleExample";
	
	@Override
	public void init(){
		mLogger.e(TAG, "Module init...");
		mTimeService.runPeriodic(1000, 5000, 0, this);
	}
	
	public void run(){
		mLogger.i(TAG, "Module example run at " + dateFormatter.format(new Date()));
	}

	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, List<String> result) {
	}


	@Override
	public void onError(long id, String plugin, String pluginVersion, String methodName,
			String errorMessage) {
	}

	@Override
	public void onEvent(String plugin, String version, String eventName, List<String> extras) {
	}

	@Override
	public void onTimerEvent() {
		mLogger.d(TAG, "timer event");
		this.run();		
	}
} 