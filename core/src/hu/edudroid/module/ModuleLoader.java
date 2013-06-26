package hu.edudroid.module;

import java.io.File;

import android.content.Context;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class ModuleLoader {

	private static final String MODULE_SHARED_PREFS = "ModulePrefs";

	private static ModuleBase loadModule(File file, String jarName, Context context){
		File dexedJavaFile = AssetReader.copyAssetToInternalStorage(jarName, context);
		DexClassLoader dexLoader = new DexClassLoader(dexedJavaFile.getAbsolutePath(), 
														file.getAbsolutePath(), 
														null, 
														context.getClassLoader());
		try {
			Class<?> dexLoadedClass = dexLoader.loadClass("hu.edudroid.ict.sample_project.ModulExample");
			ModuleBase dexContent = (ModuleBase)dexLoadedClass.newInstance();
			dexContent.init(new SharedPrefs(context.getSharedPreferences(MODULE_SHARED_PREFS, Context.MODE_PRIVATE)), new AndroidLogger());
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
			ModuleBase module = loadModule(outFile, jarName, context);
			Log.e("ModuleLoader","Modul has been loaded succesfully. Start running!");
			module.run();
			Log.e("ModuleLoader","Modul created and running: " + module.getModuleName());
			
		} catch (NullPointerException e) {
			
		}
	}
}