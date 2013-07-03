package hu.edudroid.interfaces;


public abstract class ModuleBase implements PluginResultListener, Module {

	// Prefs keys
	private final static String			PREFS_KEY_MAXIMUM_CACHE_SIZE	= "maxChacheSize";
	private final static String			PREFS_KEY_CACHE_UPLOAD_TYPE		= "cacheUploadType";
	private final static String			PREFS_KEY_MINIMUM_BATTERY		= "minimumBattery";
	public final static String			PREFS_NAME						= "modulePrefs";

	protected Preferences				mPrefs;
	protected Logger 					mLogger;
	protected PluginCollection 			mPluginCollection;
	protected TimeServiceInterface		mTimeService;
	
	
	public ModuleBase() {
		super();
	}
	
	public final void init(Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeservice) {
		mPrefs = prefs;
		mLogger = logger;
		mPluginCollection = pluginCollection;
		mTimeService = timeservice;
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
}