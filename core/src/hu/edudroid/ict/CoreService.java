package hu.edudroid.ict;

import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.ict.plugins.PluginDescriptor;
import hu.edudroid.ict.plugins.PluginIntentReceiver;
import hu.edudroid.ict.utils.HttpUtils;
import hu.edudroid.ict.utils.ServerUtilities;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.ModuleDescriptor;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.PluginListener;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;
import hu.edudroid.module.AndroidLogger;
import hu.edudroid.module.ModuleLoader;
import hu.edudroid.module.ModuleTimeService;
import hu.edudroid.module.SharedPrefs;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

import dalvik.system.DexClassLoader;

public class CoreService extends Service implements PluginListener {
	
	public static final String TEMP_DIR = "temp"; 
	public static final String DESCRIPTOR_FOLDER = "descriptors"; 
	public static final String JAR_FOLDER = "jars";
	
	// Google project id
    public static final String SENDER_ID = "1017069233076";
    public static String registration_ID = "";

	private static final String TAG = "CoreService";

	private PluginIntentReceiver mBroadcast;
	private HashMap<String, Module> modules = new HashMap<String, Module>(); // Modules by class name
	private HashMap<String, ModuleDescriptor> descriptors = new HashMap<String, ModuleDescriptor>(); // Descriptors by class name
	
	private CoreBinder binder = new CoreBinder();
	
	private AndroidPluginCollection pluginCollection;

	private HashSet<PluginListener> pluginListeners = new HashSet<PluginListener>();
	private HashSet<ModuleSetListener> moduleListeners = new HashSet<ModuleSetListener>();
	private boolean started = false;
	private HashMap<String, TimeServiceInterface> timers = new HashMap<String, TimeServiceInterface>();
	private List<PluginDescriptor> availablePlugins;
	
	public static File getDescriptorFolder(Context context) {
		return new File(context.getFilesDir(), CoreService.DESCRIPTOR_FOLDER);
	}

	
	public static File getJarFolder(Context context) {
		return new File(context.getFilesDir(), CoreService.JAR_FOLDER);
	}
	
	public class CoreBinder extends Binder {
		public CoreService getService() {
			return CoreService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (!started) {
			Log.i(TAG, "Starting CoreService!");
			started = true;
			// Download available plugin list
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO get URL for this get
					String availablePluginsString = HttpUtils.get(ServerUtilities.SERVER_URL + "/jsp/ListRegisteredPlugins.jsp");
					Log.e(TAG, "Plugin string " + availablePluginsString);
					// TODO parse available plugin list
					availablePlugins = new ArrayList<PluginDescriptor>();
					PluginDescriptor wifi = new PluginDescriptor("WiFi plugin", "hu.edudroid.ictpluginwifi", "A plugin for WiFi.");
					PluginDescriptor social = new PluginDescriptor("Social plugin", "hu.edudroid.ictpluginsocial", "A plugin for social stuff.");
					availablePlugins.add(wifi);
					availablePlugins.add(social);		
				}
			}).start();			
			mBroadcast = new PluginIntentReceiver();
			pluginCollection = new AndroidPluginCollection();
			
			Log.i(TAG, "Registering receivers...");
			registerReceiver(mBroadcast, new IntentFilter(
					Constants.INTENT_ACTION_DESCRIBE));
			registerReceiver(mBroadcast, new IntentFilter(
					Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER));
			registerReceiver(mBroadcast, new IntentFilter(
					Constants.INTENT_ACTION_PLUGIN_EVENT));
			Log.i(TAG, "Receivers are registered!");
	
			mBroadcast.registerPluginDetailsListener(this);
			
			registeringGCM();
			
			Intent mIntent = new Intent(Constants.INTENT_ACTION_PLUGIN_POLL);
			sendBroadcast(mIntent);
			
			Intent uploadLogs = new Intent(this,UploadService.class);
			startService(uploadLogs);
			
			// Process descriptor files
			// Copy modules from assets at startup.
			try {
				ModuleLoader.copyAssetsToInternalStorage(this);
			} catch (IOException e) {
				Log.e(TAG, "Couldn't copy assets to internal storage.", e);
				e.printStackTrace();
			}

			File descriptorFolder = getDescriptorFolder(this);
			String[] descriptors = descriptorFolder.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.endsWith("desc");
				}
			});
			Log.i(TAG, "Loading " + descriptors.length + " module(s).");
			if(descriptors!=null){
				for (String descriptor : descriptors) {
					ModuleDescriptor moduleDescriptor = ModuleLoader.parseModuleDescriptor(new File(descriptorFolder,descriptor));
					if (moduleDescriptor != null) {
						addModule(moduleDescriptor);
					}
				}
			}
			
		}		
	}
	
	public void registerPluginDetailsListener(PluginListener listener) {
		pluginListeners.add(listener);
	}

	public void unregisterPluginDetailsListener(PluginListener listener) {
		pluginListeners.remove(listener);
	}

	public void registerModuleSetListener(ModuleSetListener listener) {
		moduleListeners.add(listener);
	}

	public void unregisterModuleSetListenerListener(ModuleSetListener listener) {
		moduleListeners.remove(listener);
	}

	/**
	 * Returns modules currently loaded to the system
	 * @return
	 */
	public List<ModuleDescriptor> getLoadedModules() {
		List<ModuleDescriptor> ret = new ArrayList<ModuleDescriptor>();
		for (String moduleClass : modules.keySet()) {
			ModuleDescriptor descriptor = descriptors.get(moduleClass);
			ret.add(descriptor);
		}
		return ret;
	}
	
	/**
	 * Adds a module to the core. Module will be part of the running system.
	 * @param moduleDescriptor The descriptor of the module
	 * @return True if module was started successfully, false otherwise.
	 */
	public boolean addModule(ModuleDescriptor moduleDescriptor) {
		if (modules.containsKey(moduleDescriptor.getClassName())) {
			Log.w(TAG, "Module " + moduleDescriptor.getClassName() + " already loaded.");
			return false;
		}
		try {
			File jarFolder = getJarFolder(this);
			String dexedJavaFile = new File(jarFolder, moduleDescriptor.getJarFile()).getAbsolutePath();
			String className = moduleDescriptor.getClassName();
			Log.i(TAG, "Loading module " + className + " from file " + dexedJavaFile);
			Module module = null; 
			File dexOptimizedFolder = new File(getFilesDir(), TEMP_DIR);
			dexOptimizedFolder.mkdirs();
			DexClassLoader dexLoader = new DexClassLoader(dexedJavaFile, 
															dexOptimizedFolder.getAbsolutePath(), 
															null, 
															getClassLoader());
			try {
				Class<?> dexLoadedClass = dexLoader.loadClass(className);
				@SuppressWarnings("unchecked")
				Constructor<Module> constructor = (Constructor<Module>) dexLoadedClass.getConstructor(Preferences.class, Logger.class, PluginCollection.class, TimeServiceInterface.class);
				if (constructor == null) {
					throw new NoSuchMethodException("Couldn't find proper consturctor.");
				}
				TimeServiceInterface timeService = new ModuleTimeService();
				timers.put(className, timeService);
				Log.i(TAG,"Calling module constructor");
				module = constructor.newInstance(new SharedPrefs(this, className),
						new AndroidLogger(className),
						pluginCollection,
						timeService);
			} catch (ClassNotFoundException e) {
				Log.e(TAG, "Error loading module.", e);
				e.printStackTrace();
			} catch (InstantiationException e) {
				Log.e(TAG, "Error loading module.", e);
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				Log.e(TAG, "Error loading module.", e);
				e.printStackTrace();
			}
			if (module == null) {
				Log.e(TAG, "Module couldn't be loaded.");
				return false;
			}
			modules.put(moduleDescriptor.getClassName(), module);
			this.descriptors.put(moduleDescriptor.getClassName(), moduleDescriptor);
			try {
				module.init();
			} catch (Exception e){
				Log.e(TAG, "Error initializing module " + moduleDescriptor.getModuleName() + " : " + e.getMessage());
				e.printStackTrace();
			}
			for (ModuleSetListener listener : moduleListeners) {
				listener.moduleAdded(moduleDescriptor);
			}
			return true;
		} catch (SecurityException e) {
			Log.e(TAG, "Couldn't load module " + e);
			e.printStackTrace();
			return false;
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Couldn't load module " + e);
			e.printStackTrace();
			return false;
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "Couldn't load module " + e);
			e.printStackTrace();
			return false;
		} catch (InvocationTargetException e) {
			Log.e(TAG, "Couldn't load module " + e);
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			Log.e(TAG, "Couldn't load module " + e);
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Remove a module from the core, module will stop running.
	 * @param moduleName The name of the module
	 * @return True if module was successfully removed, false otherwise.
	 */
	public boolean removeModule(String moduleName) {
		Log.w(TAG, "Removing module " + moduleName);
		Module module = modules.remove(moduleName);
		ModuleDescriptor descriptor = descriptors.remove(moduleName);
		if (module != null) {
			TimeServiceInterface timer = timers.remove(moduleName);
			timer.cancelAll();
			pluginCollection.removeEventListener(module);
			pluginCollection.removeResultListener(module);
			for (ModuleSetListener listener : moduleListeners) {
				listener.moduleRemoved(descriptor);
			}
			Log.w(TAG, "Module removed " + moduleName);
			return true;
		}
		Log.e(TAG, "Couldn't remove module " + moduleName);		
		return false;
	}
	
	@Override
	public void onDestroy() {
		
		Log.e(TAG, "Service destroyed");
		super.onDestroy();
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		Log.i(TAG,"newPlugin: "+plugin.getName());
		pluginCollection.newPlugin(plugin);
		for (PluginListener listener : pluginListeners) {
			listener.newPlugin(plugin);
		}
		return true;
	}


	public List<Plugin> getPlugins() {
		return pluginCollection.getAllPlugins();
	}
	
	public List<PluginDescriptor> getAvailablePlugins() {
		return availablePlugins;
	}
	
	public void registeringGCM(){
        GCMRegistrar.checkDevice(this);
        GCMRegistrar.checkManifest(this);

        registration_ID = GCMRegistrar.getRegistrationId(this);
 
        if (registration_ID.equals("")) {
            Log.i("GCM registration","Registration is not present, register now with GCM!");          
            GCMRegistrar.register(this, SENDER_ID);
        } else {
        	Log.i("GCM registration","Device is already registered on GCM: " +registration_ID);
            if (!GCMRegistrar.isRegisteredOnServer(this)) {
            	registeringGCMonServer();
            }
        }
	}
	
	public void registeringGCMonServer(){
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
        String imei=mngr.getDeviceId(); 
        String sdk_version=String.valueOf(android.os.Build.VERSION.SDK_INT);
		PackageManager pm = this.getPackageManager();
		
		boolean cellular = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
		boolean wifi = pm.hasSystemFeature(PackageManager.FEATURE_WIFI);
		boolean bluetooth = pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
		boolean gps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    	
        RegisterToServer regTask = new RegisterToServer(this,imei,registration_ID,sdk_version,cellular,wifi,bluetooth,gps);
        Thread thread = new Thread(regTask, "RegisterToServer");
        thread.start();
	}
	
	public class RegisterToServer implements Runnable {

		Context mContext;
		String mIMEI;
		String mGcmId;
		String mSdk_version;
		boolean mCellular;
		boolean mWifi;
		boolean mBluetooth;
		boolean mGps;
		
        public RegisterToServer(Context context,String imei, String gcmId, String sdk_version, boolean cellular, boolean wifi, boolean bluetooth, boolean gps) {
            mContext=context;
            mIMEI=imei;
            mGcmId=gcmId;
            mSdk_version=sdk_version;
            mCellular=cellular;
            mWifi=wifi;
            mBluetooth=bluetooth;
            mGps=gps;
        }

        public void run() {
        	ServerUtilities.register(mContext, mIMEI, mGcmId,mSdk_version,mCellular,mWifi,mBluetooth,mGps);
        }
    }	
	
	public class downloadFile implements Runnable {

		Context mContext;
		String mUrl;
		String mFilename;
		
        public downloadFile(Context context, String url) {
            mContext=context;
            mUrl=url;
        }

        public void run() {
        	ModuleLoader.downloadModule(mContext, mUrl);
        }
    }
}