package hu.edudroid.ict.utils;

import hu.edudroid.ict.R;
import hu.edudroid.ict.plugins.PluginDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public final class ServerUtilities {
	private static final int MAX_ATTEMPTS = 5;
	private static final int BACKOFF_MILLI_SECONDS = 2000;
	private static final Random random = new Random();
	
	public static final String PORTAL_URL = "http://droidlabportal.appspot.com/";
	public static final String SERVER_URL = "http://ictdroidlab.appspot.com/";
	public static final String TAG = "Server Utilities";
	private static final String USER_NAME = "email";
	private static final String PASSWORD = "pass";
	
	private static final String LOGIN_COOKIE = "";
	
	public static boolean hasUserLoginCookie(Context context) {
		PersistantCookieStore cookieStore = new PersistantCookieStore(context);
		cookieStore.getCookie(PORTAL_URL, LOGIN_COOKIE);
		return false;
	}
	
	/**
	 * Logs the user in.
	 * @param userName The user's name
	 * @param password The user's password
	 * @return True if login was successful, false otherwise
	 */
	public static boolean login(String userName, String password) {
		Log.d(TAG, "Logging in to server");
		Map<String, String> params = new HashMap<String, String>();
		params.put(USER_NAME, userName);
		params.put(PASSWORD, password);
		String result = HttpUtils.post(PORTAL_URL, params);
		Log.d(TAG, "Login result " + result);
		return (result.equals("LOGGED_IN"));
	}

	/**
	 * Register this account/device pair within the server.
	 * 
	 */
	public static void register(final Context context, String imei, String gcmId, String androidVersion, Map<String, Integer> pluginVersions) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("imei", imei);
		params.put("gcm_id", gcmId);
		params.put("androidVersion", androidVersion);
		if (pluginVersions != null) {
			for (Entry<String, Integer> pluginVersion : pluginVersions.entrySet()) {
				params.put(pluginVersion.getKey(), pluginVersion.getValue().toString());
			}
		}

		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

		for (int i = 1; i <= MAX_ATTEMPTS; i++) {
			Log.d(TAG, "Attempt #" + i + " to register");
			String result = HttpUtils.post(SERVER_URL + "registerdevice", params);
			if (result != null && result.equals("OK")) { // TODO check if register went well
				GCMRegistrar.setRegisteredOnServer(context, true);
				return;
			} else {
				Log.e(TAG, "Failed to register on attempt " + i );
				if (i == MAX_ATTEMPTS) {
					break;
				}
				try {
					Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
					Thread.sleep(backoff);
				} catch (InterruptedException e1) {
					Log.d(TAG, "Thread interrupted: abort remaining retries!");
					Thread.currentThread().interrupt();
					return;
				}
				backoff *= 2;
			}
		}
	}

	/**
	 * Unregister this account/device pair within the server.
	 */
	public static void unregister(final Context context, final String regId) {
		Log.i(TAG, "unregistering device (regId = " + regId + ")");
		Map<String, String> params = new HashMap<String, String>();
		params.put("device_id", regId);
		HttpUtils.post(SERVER_URL + "unregisterdevice", params);
		GCMRegistrar.setRegisteredOnServer(context, false);
		String message = context.getString(R.string.server_unregistered);
		// TODO check if unregister went well
		Log.e(TAG, message);
	}

	public static List<PluginDescriptor> getAvailablePlugins(Runnable runnable) {
		// Get URL for this
		String availablePluginsString = HttpUtils.get(ServerUtilities.SERVER_URL + "/jsp/ListRegisteredPlugins.jsp");
		Log.e(TAG, "Plugin string " + availablePluginsString);
		// TODO parse available plugin list
		List<PluginDescriptor> availablePlugins = new ArrayList<PluginDescriptor>();
		PluginDescriptor wifi = new PluginDescriptor("WiFi plugin", "hu.edudroid.ictpluginwifi", "A plugin for WiFi.");
		PluginDescriptor social = new PluginDescriptor("Social plugin", "hu.edudroid.ictpluginsocial", "A plugin for social stuff.");
		availablePlugins.add(wifi);
		availablePlugins.add(social);
		return availablePlugins;
	}
}