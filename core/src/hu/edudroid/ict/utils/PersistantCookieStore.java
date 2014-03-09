package hu.edudroid.ict.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PersistantCookieStore implements CookieStore {
	
	private static final String PREFS = "CookieStore";
	private static final String TAG = PersistantCookieStore.class.getName();
	SharedPreferences prefs;
	
	public PersistantCookieStore(Context context) {
		prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
	}

	@Override
	public void addCookie(Cookie cookie) {
		Log.i(TAG, "Adding cookie " + cookie.toString());
		try {
			PersistedCookie persistedCookie = new PersistedCookie(cookie);
			String key = getKey(cookie.getDomain(), cookie.getPath(), cookie.getName());
			Log.i(TAG, "Cookies key " + key);
			prefs.edit().putString(key, persistedCookie.toJSON()).commit();
		} catch (JSONException e) {
			Log.e(TAG, "Error persisting cookie.");
			e.printStackTrace();
		}
		
	}

	@Override
	public void clear() {
		prefs.edit().clear().commit();
	}

	@Override
	public boolean clearExpired(Date date) {
		Log.i(TAG, "Clear expired cookies.");
		return false;
	}

	@Override
	public List<Cookie> getCookies() {
		Log.i(TAG, "Cookies requested");
		Map<String, ?> entries = prefs.getAll();
		List<Cookie> cookies = new ArrayList<Cookie>();
		for (Object value : entries.values()) {
			try {
				cookies.add(PersistedCookie.parseFromJSON((String)value));
			} catch (JSONException e) {
				Log.e(TAG, "Error parsing cookie.");
				e.printStackTrace();
			}
		}
		Log.i(TAG, "Returning " + cookies.size() + " cookies.");
		return cookies;
	}

	public String getCookie(String domain, String path, String name) {
		String key = getKey(domain, path, name);
		return prefs.getString(key, null);
	}
	
	public String getCookie(String domain, String name) {
		Log.i(TAG, "Looking for cookie " + name + " for domain " + domain);
		String key = getKey(domain, null, name);
		return prefs.getString(key, null);
	}

	private String getKey(String domain, String path, String name) {
		String key = domain;
		if (path != null) {
			key = key + " " + path;
		}
		key = key + " " + name;
		return key;
	}
}