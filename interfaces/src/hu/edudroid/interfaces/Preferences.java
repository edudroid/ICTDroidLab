package hu.edudroid.interfaces;

public interface Preferences {
	
	void putString(String key, String value);

	String getString(String key, String defaultValue);

	void putInt(String key, int value);

	int getInt(String key, int defaultValue);

	long getLong(String key, long defaultValue);

	void putLong(String key, long value);

	boolean getBoolean(String key, boolean defaultValue);

	void putBoolean(String key, boolean value);

}
