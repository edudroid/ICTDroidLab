package hu.edudroid.interfaces;

public abstract class Module implements PluginResultListener, PluginEventListener, ModuleTimerListener {

	protected final Preferences				mPrefs;
	protected final Logger 					mLogger;
	protected final PluginCollection 			mPluginCollection;
	protected final TimeServiceInterface		mTimeService;
	protected final ThreadSemaphore			mThreadSemaphore;
	
	
	public Module(Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeservice, ThreadSemaphore threadsemaphore) {
		mPrefs = prefs;
		mLogger = logger;
		mPluginCollection = pluginCollection;
		mTimeService = timeservice;
		mThreadSemaphore = threadsemaphore;
	}
	
	public abstract void init();
}