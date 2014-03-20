package hu.edudroid.ict.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CoreConstants {
	public static final String DEVICE_NAME_KEY = "device name";
	
	public static final String PREFS_NAME = "preferences";
	public static final String USER_NAME_KEY = "user_name";
	public static final String PASSWORD_KEY = "password";

	public static final String DEFAULT_DEVICE_NAME = "default_device";

	
	public static String getString(String key, String defaultValue, Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefs.getString(key, defaultValue);
	}


	public static void saveString(String key, String value, Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		prefs.edit().putString(key, value).commit();
	}
}