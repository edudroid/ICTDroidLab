package hu.edudroid.ict;

import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.ict.plugins.PluginDescriptor;
import hu.edudroid.ict.plugins.PluginIntentReceiver;
import hu.edudroid.ict.utils.CoreConstants;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;

public class CoreService extends Service implements PluginListener {
	
	public static final String TEMP_DIR = "temp"; 
	public static final String DESCRIPTOR_FOLDER = "descriptors"; 
	public static final String JAR_FOLDER = "jars";
	
	public static final String REGISTER_DEVICE_COMMAND = "register device";
	
	// Google project id
    public static final String SENDER_ID = "1017069233076";
	private static final String TAG = "CoreService";

	private PluginIntentReceiver mBroadcast;
	
	private CoreBinder binder = new CoreBinder();
	
	private AndroidPluginCollection pluginCollection;

	private HashSet<PluginListener> pluginListeners = new HashSet<PluginListener>();
	private boolean started = false;
	private List<PluginDescriptor> availablePlugins;
	private ModuleManager moduleManager;
	private WakeLock wl;
	
	
	public static File getDescriptorFolder(Context context) {
		return new File(context.getFilesDir(), CoreService.DESCRIPTOR_FOLDER);
	}

	
	public static File getJarFolder(Context context) {
		File ret = new File(context.getFilesDir(), CoreService.JAR_FOLDER);
		Log.e(TAG, "JAR path : " + ret.getAbsolutePath());
		return ret;
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

	public void init() {
		if (!started) {			
			Log.i(TAG, "Starting CoreService!");
			started = true;
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
			wl.acquire();
			// Download available plugin list
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					availablePlugins = ServerUtilities.getAvailablePlugins(this);
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
			
			// Register GCM 
	        GCMRegistrar.checkDevice(this);
	        GCMRegistrar.checkManifest(this);

	        String registration_ID = GCMRegistrar.getRegistrationId(this);
	 
	        if (registration_ID.equals("")) {
	            Log.i("GCM registration","Registration is not present, register now with GCM!");          
	            GCMRegistrar.register(this, SENDER_ID);
	        } else {
	        	Log.i("GCM registration","Device is already registered on GCM: " +registration_ID);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						registerWithBackend();
					}
				}).start();
	        }			
			pollPlugins();
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
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		init();
		if (intent.getAction() != null && intent.getAction().equals(REGISTER_DEVICE_COMMAND)) {
			// Register on new thread
			new Thread(new Runnable() {
				@Override
				public void run() {
					registerWithBackend();
				}
			}).start();
		}
		return Service.START_STICKY;
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
		wl.release();			
		Log.e(TAG, "Service destroyed");
		super.onDestroy();
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		Log.i(TAG,"newPlugin: "+plugin.getName());
		if (pluginCollection.newPlugin(plugin)) {
			for (PluginListener listener : pluginListeners) {
				listener.newPlugin(plugin);
			}
			return true;
		}
		return false;
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
	
	/**
	 * Register device to the server, this registration must be done every time the device comes online,
	 * or the user credentials become available, or the device capabilities or GCM ID changes.
	 */
	public void registerWithBackend() {
    	// Register device with server
		TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE); 
        String imei = mngr.getDeviceId(); 
        String sdkVersion=String.valueOf(android.os.Build.VERSION.SDK_INT);
		String deviceName = CoreConstants.getString(CoreConstants.DEVICE_NAME_KEY, CoreConstants.DEFAULT_DEVICE_NAME, this);
    	boolean registered = ServerUtilities.registerDevice(this, imei, deviceName, GCMRegistrar.getRegistrationId(this), sdkVersion, null);
		Log.e("Device registered", "Success: " + registered);
	}


	public void pollPlugins() {
		Log.e(TAG, "Polling plugins");
		Intent mIntent = new Intent(Constants.INTENT_ACTION_PLUGIN_POLL);
		sendBroadcast(mIntent);
	}	
}