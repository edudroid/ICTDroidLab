package hu.edudroid.module;

import hu.edudroid.ict.CoreService;
import hu.edudroid.ict.FileUtils;
import hu.edudroid.interfaces.ModuleDescriptor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class ModuleLoader {
	private static final String JAR_FILE_KEY = "jar_file";
	private static final String CLASS_NAME_KEY = "class_name";
	private static final String MODULE_NAME_KEY = "module_name";
	private static final String TAG = "ModuleLoader";
	private static final String DESCRIPTOR_ASSET_FOLDER = "descriptors";
	private static final String JAR_ASSET_FOLDER = "jars";

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
	
	public static List<ModuleDescriptor> readModulesFromAssets(Context context, AssetManager assetManager) throws IOException {
		ArrayList<ModuleDescriptor> modulesInAssets = new ArrayList<ModuleDescriptor>();
		String[] descriptors = assetManager.list(DESCRIPTOR_ASSET_FOLDER);
		String[] jars = assetManager.list(JAR_ASSET_FOLDER);
		for (String jar : jars) {
			String assetPath = new File(JAR_ASSET_FOLDER, jar).getAbsolutePath();
			AssetReader.copyAsset(assetPath, CoreService.getJarFolder(context), context);
		}
		for (String descriptor : descriptors) {
			File descriptorFile = AssetReader.copyAsset(new File(DESCRIPTOR_ASSET_FOLDER, descriptor).getAbsolutePath(), new File(context.getFilesDir(), CoreService.DESCRIPTOR_FOLDER), context);
			ModuleDescriptor moduleDescriptor = ModuleLoader.parseModuleDescriptor(descriptorFile);
			if (moduleDescriptor != null) {
				modulesInAssets.add(moduleDescriptor);
			}
		}
		return modulesInAssets;
	}
}
