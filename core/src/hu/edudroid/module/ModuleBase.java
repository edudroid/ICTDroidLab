package hu.edudroid.module;

import hu.edudroid.ict.RegisterActivity;
import hu.edudroid.ict.plugins.Plugin;
import hu.edudroid.ict.plugins.PluginCall;
import hu.edudroid.ict.plugins.PluginPollingBroadcast;
import hu.edudroid.ict.plugins.PluginResultListener;
import hu.edudroid.module.ModuleFileUploader.UploaderResultHandler;
import hu.edudroid.module.ModuleFileWriter.FileWriterResultHandler;
import java.io.File;
import android.os.Environment;


public abstract class ModuleBase implements PluginResultListener {

	// Prefs keys
	private final static String			PREFS_KEY_MAXIMUM_CACHE_SIZE	= "maxChacheSize";
	private final static String			PREFS_KEY_CACHE_UPLOAD_TYPE		= "cacheUploadType";
	private final static String			PREFS_KEY_MINIMUM_BATTERY		= "minimumBattery";
	public final static String			PREFS_NAME						= "modulePrefs";

	protected Preferences				mPrefs;
	private PluginPollingBroadcast      mPluginBroadcast;
	private FileWriterResultHandler     mFileWriterResult;
	private UploaderResultHandler       mUploadResultHandler;
	protected Logger 					mLogger;
	


	public ModuleBase(Preferences preferences, Logger logger) {
		mPrefs = preferences;
		mLogger = logger;
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
			mLogger.e("MODULE::ModulBase:addPluginEventListener","HIBA");
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

	public static long getMaximumCacheSize(Preferences preferences){
		return preferences.getLong(PREFS_KEY_MAXIMUM_CACHE_SIZE, 307200);
	}

	public static boolean isOnlyWifiUpload(Preferences preferences){
		return preferences.getBoolean(PREFS_KEY_CACHE_UPLOAD_TYPE, true);
	}

	public static int getMinimumBatteryForUpload(Preferences preferences){
		return preferences.getInt(PREFS_KEY_MINIMUM_BATTERY, 20);
	}

	public static void setMaximumCacheSize(	Preferences preferences,
											long maximumCache){
		preferences.putLong(PREFS_KEY_MAXIMUM_CACHE_SIZE, maximumCache);
	}

	public static void setUploadWithoutWifi(Preferences preferences,
											boolean withoutWifi){
		preferences.putBoolean(PREFS_KEY_CACHE_UPLOAD_TYPE, withoutWifi);
	}

	public static void setMinBatteryForUpload(	Preferences preferences,
												int minBattery){
		preferences.putInt(PREFS_KEY_MINIMUM_BATTERY, minBattery);
	}

	protected abstract String getModulName();

}
