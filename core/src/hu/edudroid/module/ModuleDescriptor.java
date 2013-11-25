package hu.edudroid.module;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModuleDescriptor implements Comparable<ModuleDescriptor>{
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
	private static final String INSTANCE_ID_KEY = "instance_id";

	public final String moduleName;
	public final String author;
	public final String description;
	public final String website;
	public final List<String> usedPlugins;
	public final List<String> permissions;
	public final List<QuotaLimit> quotas;
	public final long measurementLength;
	public final String className;
	public final String jarFile;
	public final String instanceId;
	
	public ModuleDescriptor(String jsonString) throws JSONException {
		JSONObject json = new JSONObject(jsonString);
		
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
		String instanceId = json.getString(INSTANCE_ID_KEY);
		
		this.moduleName = moduleName;
		this.author = author;
		this.description = description;
		this.website = website;
		this.usedPlugins = Collections.unmodifiableList(usedPlugins);
		this.permissions = permissions;
		this.quotas = quotas;
		this.measurementLength = measurementLength;
		this.className = className;
		this.jarFile = jarFile;
		this.instanceId = instanceId;
	}
	
	public int compareTo(ModuleDescriptor another) {
		if (moduleName == null) {
			return -1;
		}
		return moduleName.compareTo(another.moduleName);
	}
}