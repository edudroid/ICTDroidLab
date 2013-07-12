package hu.edudroid.ict;

import hu.edudroid.interfaces.ModuleDescriptor;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ModuleLoader {
	private static final String JAR_FILE_KEY = "jar_file";
	private static final String CLASS_NAME_KEY = "class_name";
	private static final String MODULE_NAME_KEY = "module_name";
	private static final String TAG = "ModuleLoader";

	/**
	 * Parse a module descriptor.
	 * @param descriptorPath Path to the JSON descriptor file.
	 * @return The descriptor of the parsed module, or null if parsing was unsuccessful.
	 */
	public static ModuleDescriptor parseModuleDescriptor(File descriptorPath) {
		Log.i(TAG, "Parsing module from descriptor " + descriptorPath);
		JSONObject json = null;
		String moduleName = null;
		String jarFile = null;
		String className = null;
		try {
			String fileContent = FileUtils.readFile(descriptorPath);
			Log.i(TAG, "Parsing descriptor : " + fileContent);
			json = new JSONObject(fileContent);
			jarFile = json.getString(JAR_FILE_KEY);
			className = json.getString(CLASS_NAME_KEY);
			moduleName = json.getString(MODULE_NAME_KEY);
			return new ModuleDescriptor(moduleName, className, jarFile);
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't load parameters from descriptor " + descriptorPath);
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			Log.e(TAG, "Couldn't load " + className + " from " + jarFile + " : " + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Couldn't load " + className + " from " + jarFile + " : " + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			Log.e(TAG, "Couldn't load " + className + " from " + jarFile + " : " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
}
