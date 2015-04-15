package hu.edudroid.ict.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CoreConstants {
	public static final String DEVICE_NAME_KEY = "device name";
	
	public static final String PREFS_NAME = "preferences";
	public static final String USER_NAME_KEY = "user_name";
	public static final String PASSWORD_KEY = "password";

	public static final String DEFAULT_DEVICE_NAME = "default_device";
	public static final String LAST_LOG_UPDATE_DATE = "last_log_update_date";
	public static final String UPLOAD_MODE = "upload_mode";

	
	public static String getString(String key, String defaultValue, Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefs.getString(key, defaultValue);
	}

	public static void saveString(String key, String value, Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		prefs.edit().putString(key, value).commit();
	}
	
	public static int getInt(String key, int defaultValue, Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		return prefs.getInt(key, defaultValue);
	}

	public static void saveInt(String key, int value, Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		prefs.edit().putInt(key, value).commit();
	}
}
