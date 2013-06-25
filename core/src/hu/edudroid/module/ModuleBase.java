package hu.edudroid.module;

import hu.edudroid.ict.RegisterActivity;
import hu.edudroid.ict.plugins.Plugin;
import hu.edudroid.ict.plugins.PluginCall;
import hu.edudroid.ict.plugins.PluginPollingBroadcast;
import hu.edudroid.ict.plugins.PluginResultListener;
import hu.edudroid.module.ModuleFileUploader.UploaderResultHandler;
import hu.edudroid.module.ModuleFileWriter.FileWriterResultHandler;
import java.io.File;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;


public abstract class ModuleBase implements PluginResultListener {

	// Prefs keys
	private final static String			PREFS_KEY_MAXIMUM_CACHE_SIZE	= "maxChacheSize";
	private final static String			PREFS_KEY_CACHE_UPLOAD_TYPE		= "cacheUploadType";
	private final static String			PREFS_KEY_MINIMUM_BATTERY		= "minimumBattery";
	public final static String			PREFS_NAME						= "modulePrefs";

	private SharedPreferences			mPrefs;
	private PluginPollingBroadcast      mPluginBroadcast;
	private FileWriterResultHandler     mFileWriterResult;
	private UploaderResultHandler       mUploadResultHandler;
	


	public ModuleBase(Context context) {
		mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		mPluginBroadcast = PluginPollingBroadcast.getInstance();
		setupFileWriterListener();
		setupUploadListener();
		mPluginBroadcast.registerResultListener(this);
	}
	
	private void setupUploadListener() {
		mUploadResultHandler = new UploaderResultHandler() {
			
			@Override
			public void uploadError(String fileName){
				
			}
			
			@Override
			public void successUpload(String fileName){
				
			}
		};
	}
	
	private void setupFileWriterListener() {
		mFileWriterResult = new FileWriterResultHandler() {
			
			@Override
			public void successFileWrite(){
				
			}
			
			@Override
			public void fileWriteError(String newData){
				
			}
		};
	}

	protected synchronized final void log(String log){
		schedule();
		new ModuleFileWriter(getModulName(), mFileWriterResult).execute(log);
	}

	protected final void addPluginEventListener(Plugin plugin,
												String functionName,
												Object[] params){
		PluginCall call = new PluginCall(functionName);
		for (int i = 0; i < params.length; i++){
			call.addParameter(params[i]);
		}
		try{
			plugin.callMethod(call);
		} catch(NullPointerException e){
			Log.e("MODULE::ModulBase:addPluginEventListener","HIBA");
			e.printStackTrace();
		}
	}

	private final void schedule(){
		File root = Environment.getExternalStorageDirectory();
		File logFile = new File(root, getModulName());
		if (logFile.exists()
			&& logFile.length() > mPrefs.getLong(	PREFS_KEY_MAXIMUM_CACHE_SIZE,
													20000)){
			new ModuleFileUploader(mUploadResultHandler).execute(	logFile.getName(),
															mPrefs.getString(	RegisterActivity.GCM_KEY,
																				null));
		}
	}

	// Settings

	public static long getMaximumCacheSize(SharedPreferences prefs){
		return prefs.getLong(PREFS_KEY_MAXIMUM_CACHE_SIZE, 307200);
	}

	public static boolean isOnlyWifiUpload(SharedPreferences prefs){
		return prefs.getBoolean(PREFS_KEY_CACHE_UPLOAD_TYPE, true);
	}

	public static int getMinimumBatteryForUpload(SharedPreferences prefs){
		return prefs.getInt(PREFS_KEY_MINIMUM_BATTERY, 20);
	}

	public static void setMaximumCacheSize(	SharedPreferences prefs,
											long maximumCache){
		prefs.edit()
				.putLong(PREFS_KEY_MAXIMUM_CACHE_SIZE, maximumCache)
				.commit();
	}

	public static void setUploadWithoutWifi(SharedPreferences prefs,
											boolean withoutWifi){
		prefs.edit()
				.putBoolean(PREFS_KEY_CACHE_UPLOAD_TYPE, withoutWifi)
				.commit();
	}

	public static void setMinBatteryForUpload(	SharedPreferences prefs,
												int minBattery){
		prefs.edit().putInt(PREFS_KEY_MINIMUM_BATTERY, minBattery).commit();
	}

	protected abstract String getModulName();

}
