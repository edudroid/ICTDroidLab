package hu.edudroid.module;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.Log;

import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.ModuleTimerListener;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;

/**
 * ModuleWrapper collects and stores information about the wrapped module's operation. In order to do that, inserts itself as a proxy between the 
 * module and the framework. Only module manager has to be modified in order to use the ModuleWrapper's features.
 * @author lajthabalazs
 *
 */
public class ModuleWrapper extends Module implements Preferences, Logger, PluginCollection, TimeServiceInterface {
	
	private static final String TAG = ModuleWrapper.class.getName();
	private final Context context;
	private final Module module;
	
	public ModuleWrapper(String className, Constructor<Module> constructor, Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeService, Context context) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		super(prefs, logger, pluginCollection, timeService);
		Log.i(TAG,"Calling module constructor");
		module = constructor.newInstance(new SharedPrefs(context, className),
				new AndroidLogger(className),
				pluginCollection,
				timeService);
		this.context = context;
	}
	
	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, List<String> result) {
		module.onResult(id, plugin, pluginVersion, methodName, result);
	}

	@Override
	public void onError(long id, String plugin, String pluginVersion,
			String methodName, String errorMessage) {
		module.onError(id, plugin, pluginVersion, methodName, errorMessage);
	}

	@Override
	public void onEvent(String plugin, String version, String eventName,
			List<String> extras) {
		module.onEvent(plugin, version, eventName, extras);
	}

	@Override
	public void onTimerEvent() {
		module.onTimerEvent();
	}

	@Override
	public void run() {
		module.run();
	}

	@Override
	public void init() {
		module.init();
	}

	@Override
	public void cancelAll() {
		mTimeService.cancelAll();
	}

	@Override
	public void runAt(int delay, ModuleTimerListener listener) {
		mTimeService.runAt(delay, listener);
	}

	@Override
	public void runAt(Date when, ModuleTimerListener listener) {
		mTimeService.runAt(when, listener);
	}

	@Override
	public void runPeriodic(int delay, int periodicity, int tickCount,
			ModuleTimerListener listener) {
		mTimeService.runPeriodic(delay, periodicity, tickCount, listener);
	}

	@Override
	public void runPeriodic(Date when, int periodicity, int tickCount,
			ModuleTimerListener listener) {
		mTimeService.runPeriodic(when, periodicity, tickCount, listener);
	}

	@Override
	public Plugin getPluginByName(String string) {
		return mPluginCollection.getPluginByName(string);
	}

	@Override
	public List<Plugin> getAllPlugins() {
		return mPluginCollection.getAllPlugins();
	}

	@Override
	public void e(String tag, String message) {
		mLogger.e(tag, message);
	}

	@Override
	public void d(String tag, String message) {
		mLogger.d(tag, message);
	}

	@Override
	public void i(String tag, String message) {
		mLogger.i(tag, message);
	}

	@Override
	public void putString(String key, String value) {
		mPrefs.putString(key, value);
	}

	@Override
	public String getString(String key, String defaultValue) {
		return mPrefs.getString(key, defaultValue);
	}

	@Override
	public void putInt(String key, int value) {
		mPrefs.putInt(key, value);
	}

	@Override
	public int getInt(String key, int defaultValue) {
		return mPrefs.getInt(key, defaultValue);
	}

	@Override
	public long getLong(String key, long defaultValue) {
		return mPrefs.getLong(key, defaultValue);
	}

	@Override
	public void putLong(String key, long value) {
		mPrefs.putLong(key, value);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return false;
	}

	@Override
	public void putBoolean(String key, boolean value) {
	}

}
