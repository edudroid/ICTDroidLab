package hu.edudroid.interfaces;

public abstract class Module implements PluginResultListener, PluginEventListener {

	protected final Preferences				mPrefs;
	protected final Logger 					mLogger;
	protected final PluginCollection 			mPluginCollection;
	protected final TimeServiceInterface		mTimeService;
	
	
	public Module(Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeservice) {
		mPrefs = prefs;
		mLogger = logger;
		mPluginCollection = pluginCollection;
		mTimeService = timeservice;
	}
	
	public abstract void run();
	public abstract void init();
}