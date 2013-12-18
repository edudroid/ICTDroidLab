package hu.edudroid.ict.sample_project;

import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.ThreadSemaphore;
import hu.edudroid.interfaces.TimeServiceInterface;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ModulExample extends Module {
	SimpleDateFormat dateFormatter = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss:SSS");

	public ModulExample(Preferences prefs, Logger logger,
			PluginCollection pluginCollection,
			TimeServiceInterface timeservice, ThreadSemaphore threadsemaphore) {
		super(prefs, logger, pluginCollection, timeservice, threadsemaphore);
	}

	private static final String TAG = "ModuleExample";
	private Plugin plugin;

	@Override
	public void init() {
		mLogger.e(TAG, "Module init...");
		mTimeService.runPeriodic(0, 4000, 0, this);
		mLogger.e(TAG, "Getting plugin...");
		plugin = mPluginCollection.getPluginByName("Sample plugin");
		mLogger.e(TAG, "Plugin: " + plugin.getName());
		}

	public void run() {
		if (plugin != null) {
			plugin.callMethodAsync("First sample method",
					Arrays.asList(new Object[] { "empty" }), this);
			plugin.callMethodAsync("Second sample method",
					Arrays.asList(new Object[] { "empty" }), this);
			plugin.callMethodAsync("Third sample method",
					Arrays.asList(new Object[] { "empty" }), this);
		} else {
			mLogger.e(TAG, "Couldn't find Sample Plugin");
		}
	}

	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, List<String> result) {
		mLogger.e(TAG, id + " " + plugin + " " + methodName + " " + result);
	}

	@Override
	public void onError(long id, String plugin, String pluginVersion,
			String methodName, String errorMessage) {
		mLogger.e(TAG, plugin + " " + methodName + " " + errorMessage);
	}

	@Override
	public void onEvent(String plugin, String version, String eventName,
			List<String> extras) {
		mLogger.e(TAG, eventName + " size: " + extras.size());

		for (int i = 0; i < extras.size(); i++) {
			mLogger.e(TAG, extras.get(i));
		}

	}

	@Override
	public void onTimerEvent() {
		setTid();
		threadSleeper();
		this.run();
		mLogger.d(TAG, "timer event");
		mLogger.i(TAG,
				"Module example run at " + dateFormatter.format(new Date()));

	}

	public void setTid() {
		mThreadSemaphore.setThreadId();
	}

	public void threadSleeper() {
		if (mThreadSemaphore.availablePermits() == 0) {
			long time = 500;
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
