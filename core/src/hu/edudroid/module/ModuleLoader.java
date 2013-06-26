package hu.edudroid.module;

import java.io.File;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import dalvik.system.DexClassLoader;

public class ModuleLoader {

	private static ModuleLoader	mInstance;
	private final Context		mContext;

	private ModuleLoader(Context context) {
		mContext = context;
	}

	public static ModuleLoader getInstance(Context context){
		synchronized (ModuleLoader.class){
			if (mInstance == null)
				mInstance = new ModuleLoader(context);
			return mInstance;
		}
	}

	@SuppressWarnings("rawtypes")
	private ModuleRunnable loadModule(File file, String jarName){
		
		File dexedJavaFile = AssetReader.copyAssetToInternalStorage(jarName, this.mContext);
		
		DexClassLoader dexLoader = new DexClassLoader(dexedJavaFile.getAbsolutePath(), 
														file.getAbsolutePath(), 
														null, 
														getClass().getClassLoader());		
		try {
			Class<?> dexLoadedClass = dexLoader.loadClass("hu.edudroid.ict.sample_project.ModulExample");
			ModuleRunnable urlContent = (ModuleRunnable)dexLoadedClass.newInstance();
			return urlContent;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void runModule(String urlString, String jarName){
		try {
			/*
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			connection.connect();
			// download the file
			InputStream input = new BufferedInputStream(url.openStream());
			OutputStream output = new FileOutputStream(fileUrl);
	
			byte data[] = new byte[1024];
			int count;
			while ((count = input.read(data)) != -1){
				output.write(data, 0, count);
			}
	
			output.flush();
			output.close();
			input.close();
			*/
			File outFile = new File(this.mContext.getFilesDir().getAbsolutePath());
			ModuleRunnable module = loadModule(outFile,jarName);
			Log.e("ModuleLoader","Modul has been loaded succesfully. Start running!");
			module.run();
			Log.e("ModuleLoader","Modul created and running: " + module.getModuleName());
			
		} catch (NullPointerException e) {
			
		}
	}
}