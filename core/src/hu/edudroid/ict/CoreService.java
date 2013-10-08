package hu.edudroid.ict;

import hu.edudroid.ict.GCMIntentService.RegisterToServer;
import hu.edudroid.ict.gcm.ServerUtilities;
import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.ict.plugins.PluginIntentReceiver;
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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import android.app.IntentService;
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
	
	public static File getDescriptorFolder(Context context) {
		// TODO Itt van egy hiba, mivel a .desc fileok nem a CoreService.DESCRIPTOR_FOLDER-ben vannak....
		return new File(context.getFilesDir(), CoreService.JAR_FOLDER);
	}

	
	public static File getJarFolder(Context context) {
		return new File(context.getFilesDir(), CoreService.JAR_FOLDER);
	}

	private static final String TAG = "CoreService";

	private PluginIntentReceiver mBroadcast;
	private HashMap<String, Module> modules = new HashMap<String, Module>(); // Modules by class name
	private HashMap<String, ModuleDescriptor> descriptors = new HashMap<String, ModuleDescriptor>(); // Descriptors by class name
	
	private CoreBinder binder = new CoreBinder();
	
	private AndroidPluginCollection pluginCollection;

	private HashSet<PluginListener> listeners = new HashSet<PluginListener>();
	private HashSet<ModuleSetListener> moduleListeners = new HashSet<ModuleSetListener>();
	private boolean started = false;
	private HashMap<String, TimeServiceInterface> timers = new HashMap<String, TimeServiceInterface>();
	
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
			started = true;
			Log.i(TAG, "Starting service!");
			mBroadcast = new PluginIntentReceiver();
			registerReceiver(mBroadcast, new IntentFilter(
					Constants.INTENT_ACTION_DESCRIBE));
			registerReceiver(mBroadcast, new IntentFilter(
					Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER));
			registerReceiver(mBroadcast, new IntentFilter(
					Constants.INTENT_ACTION_PLUGIN_EVENT));
	
			pluginCollection = new AndroidPluginCollection();
			mBroadcast.registerPluginDetailsListener(this);
	
			registeringGCM();
			
			Intent mIntent = new Intent(Constants.INTENT_ACTION_PLUGIN_POLL);
			sendBroadcast(mIntent);
			
			Intent uploadLogs = new Intent(this,UploadService.class);
			startService(uploadLogs);
	        
	
			// Process descriptor files
			File descriptorFolder = getDescriptorFolder(this);
			String[] descriptors = descriptorFolder.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String filename) {
					return filename.endsWith("desc");
				}
			});
			//Log.i(TAG, "Loading " + descriptors.length + " module(s).");
			if(descriptors!=null){
				for (String descriptor : descriptors) {
					ModuleDescriptor moduleDescriptor = ModuleLoader.parseModuleDescriptor(new File(descriptorFolder,descriptor));
					if (moduleDescriptor != null) {
						addModule(moduleDescriptor);
					}
				}
			}
			
		} else {
			Log.i(TAG, "Service already running.");
		}
		
		LogTask logTask = new LogTask(this);
        Thread thread = new Thread(logTask, "uploadLogsToServer");
        thread.start();
		
	}
	
	public void registerPluginDetailsListener(PluginListener listener) {
		listeners.add(listener);
	}

	public void unregisterPluginDetailsListener(PluginListener listener) {
		listeners.remove(listener);
	}

	public void registerModuleSetListener(ModuleSetListener listener) {
		moduleListeners.add(listener);
	}

	public void unregisterModuleSetListenerListener(ModuleSetListener listener) {
		moduleListeners.remove(listener);
	}

	public List<ModuleDescriptor> getLoadedModules() {
		List<ModuleDescriptor> ret = new ArrayList<ModuleDescriptor>();
		for (String moduleClass : modules.keySet()) {
			ModuleDescriptor descriptor = descriptors.get(moduleClass);
			ret.add(descriptor);
		}
		return ret;
	}
	
	public boolean addModule(ModuleDescriptor moduleDescriptor) {
		if (modules.containsKey(moduleDescriptor.getClassName())) {
			Log.w(TAG, "Module " + moduleDescriptor.getClassName() + " already loaded.");
			return false;
		}
		try {
			File jarFolder = getJarFolder(this);
			Module module = loadModule(new File(jarFolder, moduleDescriptor.getJarFile()).getAbsolutePath(), moduleDescriptor.getClassName());
			modules.put(moduleDescriptor.getClassName(), module);
			this.descriptors.put(moduleDescriptor.getClassName(), moduleDescriptor);
			module.init();
			for (ModuleSetListener listener : moduleListeners) {
				listener.moduleAdded(moduleDescriptor);
			}
			return true;
		} catch (NullPointerException e) {
			Log.e(TAG, "Couldn't load module " + e);
			e.printStackTrace();
			return false;
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
		}
	}
	
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
	
	@SuppressWarnings("unchecked")
	private Module loadModule(String dexedJavaFile, String className) throws SecurityException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		Log.i(TAG, "Loading module " + className + " from file " + dexedJavaFile);
		File dexOptimizedFolder = new File(getFilesDir(), TEMP_DIR);
		dexOptimizedFolder.mkdirs();
		DexClassLoader dexLoader = new DexClassLoader(dexedJavaFile, 
														dexOptimizedFolder.getAbsolutePath(), 
														null, 
														getClassLoader());
		try {
			Class<?> dexLoadedClass = dexLoader.loadClass(className);
			Module module = null; 
			Log.e(TAG,"Retrieving constructor");
			Constructor<Module> constructor = (Constructor<Module>) dexLoadedClass.getConstructor(Preferences.class, Logger.class, PluginCollection.class, TimeServiceInterface.class);
			if (constructor == null) {
				throw new NoSuchMethodException("Couldn't find proper consturctor.");
			}
			TimeServiceInterface timeService = new ModuleTimeService();
			timers.put(className, timeService);
			Log.e(TAG,"Calling constructor");
			module = constructor.newInstance(new SharedPrefs(this, className),
					new AndroidLogger(className),
					pluginCollection,
					timeService);
			Log.e(TAG,"Module init ready " + module);
			return module;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void onDestroy() {
		
		Log.e(TAG, "Service destroyed");
		super.onDestroy();
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		pluginCollection.newPlugin(plugin);
		for (PluginListener listener : listeners) {
			listener.newPlugin(plugin);
		}
		return true;
	}


	public List<Plugin> getPlugins() {
		return pluginCollection.getAllPlugins();
	}
	
	public void registeringGCM(){
		 // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(this);
 
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
        GCMRegistrar.checkManifest(this);
         
         
        // Get GCM registration id
        registration_ID = GCMRegistrar.getRegistrationId(this);
 
        // Check if regid already presents
        if (registration_ID.equals("")) {
            Log.e("GCM:","Registration is not present, register now with GCM ");          
            GCMRegistrar.register(this, SENDER_ID);
        } else {
        	Log.e("GCM:","Device is already registered on GCM: " +registration_ID);
            if (GCMRegistrar.isRegisteredOnServer(this)) {          
                
            } else {
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
        }
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
	
	public class LogTask implements Runnable {

		Context mContext;
		
        public LogTask(Context context) {
            mContext=context;
        }

        public void run() {
        	UploadService.upload(mContext);
        }
    }
}