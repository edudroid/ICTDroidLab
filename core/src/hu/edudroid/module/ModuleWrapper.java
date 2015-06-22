package hu.edudroid.module;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import hu.edudroid.ict.ModuleStatsListener;
import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;

/**
 * ModuleWrapper collects and stores information about the wrapped module's operation. In order to do that, inserts itself as a proxy between the 
 * module and the framework. Only module manager has to be modified in order to use the ModuleWrapper's features.
 * @author lajthabalazs
 *
 */
public class ModuleWrapper extends Module implements TimeServiceInterface {
	
	private static final String TAG = ModuleWrapper.class.getName();
	private static final String SHARED_PREF_PREFIX = "STATS_";
	private final Module module;
	private HashSet<ModuleStatsListener> moduleStatsListeners = new HashSet<ModuleStatsListener>();
	private SharedPreferences statPrefs;
	private ModuleDescriptor descriptor;
	
	public ModuleWrapper(ModuleDescriptor descriptor, Constructor<Module> constructor, Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeService, Context context) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		super(prefs, logger, pluginCollection, timeService);
		Log.i(TAG,"Calling module constructor");
		module = constructor.newInstance(prefs, logger, pluginCollection, this);
		statPrefs = context.getSharedPreferences(SHARED_PREF_PREFIX + descriptor.moduleId, Context.MODE_PRIVATE);
		this.descriptor = descriptor;
	}

	public Map<String, String> getStats() {
		int timerEventCount = statPrefs.getInt(ModuleStatsListener.STAT_KEY_TIMERS_FIRED, 0);
		long lastTimerEvent = statPrefs.getLong(ModuleStatsListener.STAT_KEY_LAST_TIMER_EVENT, 0);
		
		Map<String, String> stats = new HashMap<String, String>();
		stats.put(ModuleStatsListener.STAT_KEY_TIMERS_FIRED, Integer.toString(timerEventCount));
		stats.put(ModuleStatsListener.STAT_KEY_LAST_TIMER_EVENT, Long.toString(lastTimerEvent));
		return stats;
	}

	private void statsChangted() {
		Log.e(TAG, "Stats changed");
		Map<String, String> unmodifiableStats = Collections.unmodifiableMap(getStats());
		for (ModuleStatsListener listener : moduleStatsListeners) {
			listener.moduleStatsChanged(descriptor.moduleId, unmodifiableStats);
		}
	}


	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, Map<String, Object> result) {
		module.onResult(id, plugin, pluginVersion, methodName, result);
	}

	@Override
	public void onError(long id, String plugin, String pluginVersion,
			String methodName, String errorMessage) {
		module.onError(id, plugin, pluginVersion, methodName, errorMessage);
	}

	@Override
	public void onEvent(String plugin, String version, String eventName,
			Map<String, Object> extras) {
		Editor editor = statPrefs.edit();
		editor.putLong(ModuleStatsListener.STAT_KEY_LAST_TIMER_EVENT, System.currentTimeMillis());
		editor.commit();
		statsChangted();
		module.onEvent(plugin, version, eventName, extras);
	}

	@Override
	public void onTimerEvent() {
		int timerEventCount = statPrefs.getInt(ModuleStatsListener.STAT_KEY_TIMERS_FIRED, 0);
		timerEventCount ++;
		Editor editor = statPrefs.edit();
		editor.putLong(ModuleStatsListener.STAT_KEY_LAST_TIMER_EVENT, System.currentTimeMillis());
		editor.putInt(ModuleStatsListener.STAT_KEY_TIMERS_FIRED, timerEventCount);
		editor.commit();
		statsChangted();
		try {
			module.onTimerEvent();
		} catch (Exception e) {
			Log.e(TAG, "Error running timer event " + e, e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() {
		module.init();
	}
	
	public void registerModuleStatsListener(ModuleStatsListener listener) {
		moduleStatsListeners.add(listener);
	}

	public void unregisterModuleStatsListenerListener(ModuleStatsListener listener) {
		moduleStatsListeners.remove(listener);
	}

	public ModuleDescriptor getDescriptor() {
		return descriptor;
	}


}
