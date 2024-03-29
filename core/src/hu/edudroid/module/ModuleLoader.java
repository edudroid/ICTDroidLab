package hu.edudroid.module;

import hu.edudroid.ict.CoreService;
import hu.edudroid.ict.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

/**
 * Responsible for storing modules coming from assets or the Internet in internal storage. 
 * @author lajthabalazs
 *
 */
public class ModuleLoader {
	private static final String TAG = "ModuleLoader";
	private static final String DESCRIPTOR_ASSET_FOLDER = "descriptors";
	private static final String JAR_ASSET_FOLDER = "jars";
	private static final int TIMEOUT_CONNECTION = 5000;//5sec
	private static final int TIMEOUT_SOCKET = 30000;//30sec
	private static final String FILE_PREFIX = "attachment; filename=\"";

	/**
	 * Parse a module descriptor.
	 * @param descriptorPath Path to the JSON descriptor file.
	 * @return The descriptor of the parsed module, or null if parsing was unsuccessful.
	 */
	public static ModuleDescriptor parseModuleDescriptor(File descriptorPath, Context context) {
		try {
			String fileContent = FileUtils.readFile(descriptorPath);
			return new ModuleDescriptor(fileContent, context);
		} catch (Exception e) {
			Log.e(TAG, "Couldn't load parameters from descriptor " + descriptorPath + " : " + e, e);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Copies jars and descriptors from assets to internal storage. Parses descriptors from assets and returns them.
	 * @param context Application context to be used for asset management and file storage.
	 * @return The list of descriptors available in assets.
	 * @throws IOException
	 */
	public static void copyAssetsToInternalStorage(Context context) throws IOException {
		AssetManager assetManager = context.getAssets();
		String[] descriptors = assetManager.list(DESCRIPTOR_ASSET_FOLDER);
		String[] jars = assetManager.list(JAR_ASSET_FOLDER);
		for (String jar : jars) {
			String assetPath = new File(JAR_ASSET_FOLDER, jar).getAbsolutePath();
			AssetReader.copyAsset(assetPath, CoreService.getJarFolder(context), context);
		}
		for (String descriptor : descriptors) {
			String descriptorAssetPath = new File(DESCRIPTOR_ASSET_FOLDER, descriptor).getAbsolutePath();
			AssetReader.copyAsset(descriptorAssetPath, CoreService.getDescriptorFolder(context), context);
		}
	}
	
	/**
	 * Download a module's JAR file from the given URL and store it in internal storage.
	 * @param context The application context to be used
	 * @param fileUrl URL of the file on the web
	 */
	public static void downloadModule(Context context, String fileUrl){
		
		try{
			
			String[] splitUrl=fileUrl.split(" ");
			String jarUrl,descUrl;
			if(splitUrl.length==2){
				jarUrl=splitUrl[0];
				descUrl=splitUrl[1];
				
				downloadModulePart(context, jarUrl, "jar");
				downloadModulePart(context, descUrl, "desc");
				
				Log.i(TAG,"Module has been downloaded succesfully from: " + fileUrl);
			}
			
		}catch(Exception e){
			Log.i(TAG,"Could not download module: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public static void downloadModulePart(Context context, String moduleUrl, String part) throws Exception{
		URL url = new URL(moduleUrl);
		
		//Open a connection to that URL.
		URLConnection ucon = url.openConnection();
		Map<String, List<String>> headers = ucon.getHeaderFields();
		Log.e(TAG, "Headers");
		for (String key : headers.keySet()) {
			Log.e(TAG, key + " : " + headers.get(key));
		}
		List<String> contentDispositionValues = headers.get("Content-Disposition");
		String fileName = null;
		for (String value : contentDispositionValues) {
			Log.e(TAG, "Value: " + value);
			if (value.startsWith(FILE_PREFIX)) {
				fileName = value.substring(FILE_PREFIX.length(), value.length() -1);
			}
		}
		 

		//this timeout affects how long it takes for the app to realize there's a connection problem
		ucon.setReadTimeout(TIMEOUT_CONNECTION);
		ucon.setConnectTimeout(TIMEOUT_SOCKET);


		//Define InputStreams to read from the URLConnection.
		// uses 3KB download buffer
		InputStream is = ucon.getInputStream();
		BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);

		File file=null;
		if(part.equals("jar")){
			if (fileName == null) {
				fileName = "module.jar";
			}
			file=new File(CoreService.getJarFolder(context), fileName);
		}
		else if(part.equals("desc")){
			if (fileName == null) {
				fileName = "module.desc";
			}
			file=new File(CoreService.getDescriptorFolder(context), fileName);
		}
		
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
	}
	
	/**
	 * Delete all local module files.
	 * @param context
	 */
	public static void deleteAllModules(Context context) {
		File descriptorFolder = CoreService.getDescriptorFolder(context);
		File jarFolder = CoreService.getJarFolder(context);
		File[] files = descriptorFolder.listFiles();
		for (File file : files) {
			file.delete();
		}
		files = jarFolder.listFiles();
		for (File file : files) {
			file.delete();
		}
	}
	
	public static List<ModuleDescriptor> getAllModules(Context context) {
		List<ModuleDescriptor> ret = new ArrayList<ModuleDescriptor>();
		File descriptorFolder = CoreService.getDescriptorFolder(context);
		String[] descriptorFiles = descriptorFolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith("desc");
			}
		});
		if (descriptorFiles == null) {
			Log.i(TAG, "No descriptors available.");
		} else {
			Log.i(TAG, "Loading " + descriptorFiles.length + " module(s).");
			for (String fileName : descriptorFiles) {
				Log.e(TAG, "Loading module " + fileName);
				ModuleDescriptor descriptor = ModuleLoader.parseModuleDescriptor(new File(descriptorFolder,fileName), context);
				if (descriptor != null) {
					ret.add(descriptor);
				} else {
					Log.e(TAG, "Module " + fileName + " not valid.");
				}
			}
		}
		return ret;
	}
}