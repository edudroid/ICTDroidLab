package hu.edudroid.module;

import android.content.SharedPreferences;

public class SharedPrefs implements Preferences {

	private SharedPreferences mPrefs;

	public SharedPrefs(SharedPreferences sharedPreferences) {
		mPrefs = sharedPreferences;
	}

	@Override
	public long getLong(String key, long defaultValue) {
		return mPrefs.getLong(key, defaultValue);
	}

	@Override
	public String getString(String key, String defaultValue) {
		return mPrefs.getString(key, defaultValue);
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return mPrefs.getBoolean(key, defaultValue);
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
	public void putBoolean(String key, boolean value) {
		mPrefs.edit().putBoolean(key, value).commit();
	}

	@Override
	public void putInt(String key, int value) {
		mPrefs.edit().putInt(key, value).commit();
	}
}
