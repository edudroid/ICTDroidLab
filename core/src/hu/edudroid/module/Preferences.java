package hu.edudroid.module;

public interface Preferences {

	long getLong(String key, long defaultValue);

	String getString(String key, String defaultValue);

	boolean getBoolean(String key, boolean defaultValue);

	int getInt(String key, int defaultValue);

	void putLong(String key, long value);

	void putBoolean(String key, boolean value);

	void putInt(String key, int value);

}
