package hu.edudroid.module;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ModuleDescriptor implements Comparable<ModuleDescriptor>{
	// Shared preferences keys
	private static final String MODULE_DATA_STORE = "Module data store";
	private static final String MODULE_STATE_PREFIX = "MODULE_STATE_";
	private static final String MODULE_INSTALLED_PREFIX = "INSTALL_DATE_";

	// JSON keys
	private static final String MODULE_ID_KEY = "module_id";
	private static final String MODULE_NAME_KEY = "module_name";
	private static final String AUTHOR_KEY = "author";
	private static final String DESCRIPTION_KEY = "description";
	private static final String WEBSITE_KEY = "website";
	private static final String MEASUREMENT_LENGTH_KEY = "measurement_length";
	private static final String USED_PLUGINS_KEY = "used_plugins";
	private static final String PERMISSIONS_KEY = "permissions";
	private static final String QUOTAS_KEY = "quotas";
	private static final String JAR_FILE_KEY = "jar_file";
	private static final String CLASS_NAME_KEY = "class_name";

	public final String moduleId;
	public final String moduleName;
	public final String author;
	public final String description;
	public final String website;
	public final List<String> usedPlugins;
	public final List<String> permissions;
	public final List<QuotaLimit> quotas;
	/**
	 * Measurement length in seconds
	 */
	public final long measurementLength;
	public final String className;
	public final String jarFile;
	
	private ModuleState moduleState;
	private long installDate;
	
	public ModuleDescriptor(String jsonString, Context context) throws JSONException {
		JSONObject json = new JSONObject(jsonString);
		String moduleId = json.getString(MODULE_ID_KEY);
		String moduleName = json.getString(MODULE_NAME_KEY);
		String author = json.getString(AUTHOR_KEY);
		String description = null;
		try {
			description = json.getString(DESCRIPTION_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String website = null;
		try {
			website = json.getString(WEBSITE_KEY);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		List<String> usedPlugins = new ArrayList<String>();
		try {
			JSONArray pluginsJson = json.getJSONArray(USED_PLUGINS_KEY);
			for (int i = 0 ; i < pluginsJson.length(); i++) {
				usedPlugins.add(pluginsJson.getString(i));
			}
		} catch (JSONException e) {
			usedPlugins.clear();
		}
		List<String> permissions = new ArrayList<String>();
		try {
			JSONArray permissionsJson = json.getJSONArray(PERMISSIONS_KEY);
			for (int i = 0 ; i < permissionsJson.length(); i++) {
				permissions.add(permissionsJson.getString(i));
			}
		} catch (JSONException e) {
			permissions.clear();
		}
		List<QuotaLimit> quotas = new ArrayList<QuotaLimit>();
		try {
			JSONArray quotasJson = json.getJSONArray(QUOTAS_KEY);
			for (int i = 0 ; i < quotasJson.length(); i++) {
				quotas.add(new QuotaLimit(quotasJson.getJSONObject(i)));
			}
		} catch (JSONException e) {
			quotas.clear();
		}
		long measurementLength = json.getLong(MEASUREMENT_LENGTH_KEY);
		String className = json.getString(CLASS_NAME_KEY);
		String jarFile = json.getString(JAR_FILE_KEY);
		
		this.moduleId = moduleId;
		this.moduleName = moduleName;
		this.author = author;
		this.description = description;
		this.website = website;
		this.usedPlugins = Collections.unmodifiableList(usedPlugins);
		this.permissions = Collections.unmodifiableList(permissions);
		this.quotas = Collections.unmodifiableList(quotas);
		this.measurementLength = measurementLength;
		this.className = className;
		this.jarFile = jarFile;
		
		// From data store
		SharedPreferences prefs = context.getSharedPreferences(MODULE_DATA_STORE, Context.MODE_PRIVATE);
		if (prefs.contains(MODULE_STATE_PREFIX + moduleId)) {
			moduleState = ModuleState.getModuleState(prefs.getInt(MODULE_STATE_PREFIX + moduleId, ModuleState.INSTALLED.getValue()));
		} else {
			moduleState = ModuleState.AVAILABLE;
			prefs.edit().putInt(MODULE_STATE_PREFIX + moduleId, ModuleState.AVAILABLE.getValue()).commit();
		}
		installDate = prefs.getLong(MODULE_INSTALLED_PREFIX + moduleId, -1);
	}
	
	/**
	 * Sets the module state, if state is changed from AVAILABLE to INSTALLED, install date is saved
	 * @param newState
	 */
	public void setSate(ModuleState newState, Context context) {
		Editor editor = context.getSharedPreferences(MODULE_DATA_STORE, Context.MODE_PRIVATE).edit();
		if (moduleState == ModuleState.AVAILABLE && newState == ModuleState.INSTALLED) {
			installDate = System.currentTimeMillis();
			editor.putLong(MODULE_INSTALLED_PREFIX + moduleId, installDate);
		}
		moduleState = newState;
		editor.putInt(MODULE_STATE_PREFIX + moduleId, moduleState.getValue());
		editor.commit();
	}
	
	public int compareTo(ModuleDescriptor another) {
		if (moduleName == null) {
			return -1;
		}
		return moduleName.compareTo(another.moduleName);
	}

	public CharSequence getPluginsText(String separator) {
		String ret = "";
		for (String plugin : usedPlugins) {
			if (ret.length() > 0) {
				ret = ret + separator;
			}
			ret = ret + plugin;
		}
		return ret;
	}

	public CharSequence getPermissionsText(String separator) {
		String ret = "";
		for (String permission : permissions) {
			if (ret.length() > 0) {
				ret = ret + separator;
			}
			ret = ret + permission;
		}
		return ret;
	}

	public CharSequence getQuotasText(String separator) {
		String ret = "";
		for (QuotaLimit quota : quotas) {
			if (ret.length() > 0) {
				ret = ret + separator;
			}
			ret = ret + quota.toString();
		}
		return ret;
	}

	public ModuleState getState(Context context) {
		// Check if module is terminated
		if (moduleState == ModuleState.INSTALLED) {
			if (installDate + measurementLength * 1000 < System.currentTimeMillis()) {
				setSate(ModuleState.TERMINATED, context);
			}
		}
		return moduleState;
	}
}