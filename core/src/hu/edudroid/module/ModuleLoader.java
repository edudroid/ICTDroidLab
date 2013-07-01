package hu.edudroid.module;

import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.PluginCollection;

import java.io.File;

import android.content.Context;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class ModuleLoader {

	private static final String MODULE_SHARED_PREFS = "ModulePrefs";

	private static Module loadModule(File file, String jarName, Context context){
		File dexedJavaFile = AssetReader.copyAssetToInternalStorage(jarName, context);
		DexClassLoader dexLoader = new DexClassLoader(dexedJavaFile.getAbsolutePath(), 
														file.getAbsolutePath(), 
														null, 
														context.getClassLoader());
		try {
			Class<?> dexLoadedClass = dexLoader.loadClass("hu.edudroid.ict.sample_project.ModulExample");
			Module dexContent = (Module)dexLoadedClass.newInstance();
			PluginCollection pluginCollection = AndroidPluginCollection.getInstance();
			dexContent.init(
					new SharedPrefs(context.getSharedPreferences(MODULE_SHARED_PREFS, Context.MODE_PRIVATE)),
					new AndroidLogger(),
					pluginCollection);
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
	
	public static void runModule(String urlString, String jarName, Context context){
		try {
			File outFile = new File(context.getFilesDir().getAbsolutePath());
			Module module = loadModule(outFile, jarName, context);
			module.run();			
		} catch (NullPointerException e) {
			
		}
	}
}