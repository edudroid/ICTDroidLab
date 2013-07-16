package hu.edudroid.module;

import hu.edudroid.ict.CoreService;
import hu.edudroid.ict.FileUtils;
import hu.edudroid.interfaces.ModuleDescriptor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
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
	
	public static void downloadModule(Context context,String fileUrl,String filename){
		final int TIMEOUT_CONNECTION = 5000;//5sec
		final int TIMEOUT_SOCKET = 30000;//30sec
		try{
			URL url = new URL(fileUrl);
			long startTime = System.currentTimeMillis();
			Log.e(TAG, "download beginning: "+fileUrl);
	
			//Open a connection to that URL.
			URLConnection ucon = url.openConnection();
	
			//this timeout affects how long it takes for the app to realize there's a connection problem
			ucon.setReadTimeout(TIMEOUT_CONNECTION);
			ucon.setConnectTimeout(TIMEOUT_SOCKET);
	
	
			//Define InputStreams to read from the URLConnection.
			// uses 3KB download buffer
			InputStream is = ucon.getInputStream();
			BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
			File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),filename);
			Log.e("Saving file to:",file.getAbsolutePath());
			FileOutputStream outStream = new FileOutputStream(file);
			byte[] buff = new byte[5 * 1024];
	
			 //Read bytes (and store them) until there is nothing more to read(-1)
			int len;
			while ((len = inStream.read(buff)) != -1)
			{
			    outStream.write(buff,0,len);
			}
			//clean up
			outStream.flush();
			outStream.close();
			inStream.close();
			
			Log.i(TAG, "download completed in "
				    + ((System.currentTimeMillis() - startTime) / 1000)
				    + " sec");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
