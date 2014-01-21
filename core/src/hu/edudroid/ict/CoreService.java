package hu.edudroid.ict;

import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.ict.plugins.PluginDescriptor;
import hu.edudroid.ict.plugins.PluginIntentReceiver;
import hu.edudroid.ict.utils.HttpUtils;
import hu.edudroid.ict.utils.ServerUtilities;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginListener;
import hu.edudroid.module.ModuleDescriptor;
import hu.edudroid.module.ModuleLoader;
import hu.edudroid.module.ModuleManager;
import hu.edudroid.module.ModuleState;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

public class CoreService extends Service implements PluginListener {
	
	public static final String TEMP_DIR = "temp"; 
	public static final String DESCRIPTOR_FOLDER = "descriptors"; 
	public static final String JAR_FOLDER = "jars";
	
	// Google project id
    public static final String SENDER_ID = "1017069233076";
    public static String registration_ID = "";

	private static final String TAG = "CoreService";

	private PluginIntentReceiver mBroadcast;
	
	private CoreBinder binder = new CoreBinder();
	
	private AndroidPluginCollection pluginCollection;

	private HashSet<PluginListener> pluginListeners = new HashSet<PluginListener>();
	private boolean started = false;
	private List<PluginDescriptor> availablePlugins;
	private ModuleManager moduleManager;
	
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
					availablePlugins = Collections.unmodifiableList(availablePlugins);
				}
			}).start();			
			mBroadcast = new PluginIntentReceiver();
			pluginCollection = new AndroidPluginCollection();
			moduleManager = new ModuleManager(this);
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
			
			// Process descriptor files
			// Copy modules from assets at startup.
			try {
				ModuleLoader.copyAssetsToInternalStorage(this);
			} catch (IOException e) {
				Log.e(TAG, "Couldn't copy assets to internal storage.", e);
				e.printStackTrace();
			}

			List<ModuleDescriptor> moduleDescriptors = ModuleLoader.getAllModules(this);
			for (ModuleDescriptor moduleDescriptor : moduleDescriptors) {
				if (moduleDescriptor.getState(this) == ModuleState.INSTALLED) {
					moduleManager.startModule(moduleDescriptor, pluginCollection, getApplicationContext());
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
		moduleManager.registerModuleSetListener(listener);
	}

	public void unregisterModuleSetListener(ModuleSetListener listener) {
		moduleManager.unregisterModuleSetListener(listener);
	}

	public void registerModuleStatsListener(ModuleStatsListener listener) {
		moduleManager.registerModuleStatsListener(listener);
	}

	public void unregisterModuleStatsListener(ModuleStatsListener listener) {
		moduleManager.unregisterModuleStatsListener(listener);
	}

	/**
	 * Returns modules currently loaded to the system
	 * @return
	 */
	public List<ModuleDescriptor> getLoadedModules() {
		return moduleManager.getLoadedModules();
	}

	public List<ModuleDescriptor> getAllModules() {
		List<ModuleDescriptor> ret = moduleManager.getLoadedModules();
		HashSet<String> moduleIds = new HashSet<String>();
		for (ModuleDescriptor descriptor : ret) {
			moduleIds.add(descriptor.moduleId);
		}
		List<ModuleDescriptor> available = ModuleLoader.getAllModules(this);
		for (ModuleDescriptor descriptor : available) {
			if (!moduleIds.contains(descriptor.moduleId)) {
				ret.add(descriptor);
			}
		}
		return ret;
	}

	public Map<String, String> getModuleStats(String moduleId) {
		return moduleManager.getModuleStats(moduleId);
	}

	public ModuleDescriptor getModule(String moduleId) {
		return moduleManager.getModule(moduleId);
	}

	/**
	 * Adds a module to the core. Module will be part of the running system.
	 * @param moduleDescriptor The descriptor of the module
	 * @return True if module was started successfully, false otherwise.
	 */
	public boolean installModule(ModuleDescriptor moduleDescriptor) {
		return moduleManager.installModule(moduleDescriptor, pluginCollection, this);
	}
	
	/**
	 * Remove a module from the core, module will stop running.
	 * @param moduleId The id of the module
	 * @return True if module was successfully removed, false otherwise.
	 */
	public boolean removeModule(String moduleId) {
		return moduleManager.removeModule(moduleId, pluginCollection);
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
	
	/**
	 * Returns available plugins, including those already downloaded
	 * @return
	 */
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
        	ServerUtilities.register(mContext, mIMEI, mGcmId, mSdk_version, null);
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