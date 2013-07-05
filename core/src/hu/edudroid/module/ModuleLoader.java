package hu.edudroid.module;

import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.TimeServiceInterface;

import java.io.File;

import android.content.Context;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class ModuleLoader {

	private static final String TAG = "ModuleLoader";
	private static final String MODULE_SHARED_PREFS = "ModulePrefs";
	
	public static Module loadModule(String jarName, String className, Context context) {
		Log.i(TAG, "Loading module " + className + " from file " + jarName);
		String dexOptimizedFolder = context.getFilesDir().getAbsolutePath();
		File dexedJavaFile = AssetReader.copyAssetToInternalStorage(jarName, context);
		DexClassLoader dexLoader = new DexClassLoader(dexedJavaFile.getAbsolutePath(), 
														dexOptimizedFolder, 
														null, 
														context.getClassLoader());
		try {
			Class<?> dexLoadedClass = dexLoader.loadClass(className);
			Module dexContent = (Module)dexLoadedClass.newInstance();
			
			PluginCollection pluginCollection = AndroidPluginCollection.getInstance();
			TimeServiceInterface timeservice = ModuleTimeService.getInstance();
			dexContent.init(
					new SharedPrefs(context.getSharedPreferences(MODULE_SHARED_PREFS, Context.MODE_PRIVATE)),
					new AndroidLogger(dexContent, jarName, className),
					pluginCollection,
					timeservice);
			Log.e("Module init","ready");
			return dexContent;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}