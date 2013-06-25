package hu.edudroid.module;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import android.content.Context;
import android.os.AsyncTask;
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

	
	public void runModule(String url, String fileUrl){
		new JarDownloader().execute(url, fileUrl);
	}

	private class JarDownloader extends AsyncTask<String, Void, ModuleRunnable> {

		@Override
		protected ModuleRunnable doInBackground(String... params){
			try{
				URL url = new URL(params[0]);
				URLConnection connection = url.openConnection();
				connection.connect();
				int fileLength = connection.getContentLength();
				// download the file
				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream(params[1]);

				byte data[] = new byte[1024];
				long total = 0;
				int count;
				while ((count = input.read(data)) != -1){
					total += count;
					// TODO publishing the progress
					// publishProgress((int) (total * 100 / fileLength));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
				return loadModule(new File(params[1]));

			}
			catch (Exception e){

			}
			return null;
		}

		@Override
		protected void onPostExecute(ModuleRunnable result){
			if (result != null)
				result.run();
		}
	}

}
