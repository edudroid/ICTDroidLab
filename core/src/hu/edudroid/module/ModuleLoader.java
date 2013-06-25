package hu.edudroid.module;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import android.content.Context;
import android.util.Log;
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
	private ModuleRunnable loadModule(File file){
		final File optimizedDexOutputPath = mContext.getDir("outdex",
															Context.MODE_PRIVATE);
		DexClassLoader cl = new DexClassLoader(	file.getAbsolutePath(),
												optimizedDexOutputPath.getAbsolutePath(),
												null,
												mContext.getClassLoader());
		Class moduleRunnerClass = null;
		try{
			moduleRunnerClass = cl.loadClass("hu.tmit.ictdroid.module.ModuleRunnable");
			ModuleRunnable module = (ModuleRunnable) moduleRunnerClass.newInstance();
			return module;
		}
		catch (Exception exception){
			exception.printStackTrace();
		}
		return null;
	}
	
	public void runModule(String urlString, String fileUrl){
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
			File outFile = new File(this.mContext.getFilesDir(),fileUrl);
			ModuleRunnable module = loadModule(new File(outFile.getAbsolutePath()));
			module.run();
			
			Log.e("ModuleLoader","Modul has been loaded!");
			
		} catch (NullPointerException e) {
			
		}
	}
}
