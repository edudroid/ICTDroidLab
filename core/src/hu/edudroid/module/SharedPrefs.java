package hu.edudroid.module;

import hu.edudroid.interfaces.Preferences;
import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefs implements Preferences {

	private SharedPreferences mPrefs;

	public SharedPrefs(Context context, String moduleInstanceId) {
		if (moduleInstanceId == null) {
			throw new IllegalArgumentException("Module instance id cannot be null.");
		}
		mPrefs = context.getSharedPreferences(moduleInstanceId, Context.MODE_PRIVATE);
	}

	@Override
	public void putString(String key, String value) {
	}

	@Override
	public String getString(String key, String defaultValue) {
		return mPrefs.getString(key, defaultValue);
	}

	@Override
	public void putBoolean(String key, boolean value) {
		mPrefs.edit().putBoolean(key, value).commit();
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return mPrefs.getBoolean(key, defaultValue);
	}

	@Override
	public void putInt(String key, int value) {
		mPrefs.edit().putInt(key, value).commit();
	}
	@Override
	public int getInt(String key, int defaultValue) {
		return mPrefs.getInt(key, defaultValue);
	}

	@Override
	public void putLong(String key, long value) {
		mPrefs.edit().putLong(key, value).commit();
	}
	@Override
	public long getLong(String key, long defaultValue) {
		return mPrefs.getLong(key, defaultValue);
	}

}
