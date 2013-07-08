package hu.edudroid.ict;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import dalvik.system.DexClassLoader;
import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.ict.plugins.PluginPollingBroadcast;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.ModuleDescriptor;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;
import hu.edudroid.module.AndroidLogger;
import hu.edudroid.module.ModuleTimeService;
import hu.edudroid.module.SharedPrefs;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class CoreService extends Service {
	
	public static final String TEMP_DIR = "temp";
	
	public static final String DESCRIPTOR_FOLDER = "descriptors";
	
	public static File getDescriptorFolder(Context context) {
		return new File(context.getFilesDir(), CoreService.DESCRIPTOR_FOLDER);
	}

	public static final String JAR_FOLDER = "jars";	
	
	public static File getJarFolder(Context context) {
		return new File(context.getFilesDir(), CoreService.JAR_FOLDER);
	}

	private static final String TAG = "CoreService";
	private static final String JAR_FILE_KEY = "jar_file";
	private static final String CLASS_NAME_KEY = "class_name";

	private static final String MODULE_NAME_KEY = "module_name";

	private PluginPollingBroadcast mBroadcast = null;
	private AndroidPluginCollection mPluginCollection = null;
	private HashMap<String, Module> modules = new HashMap<String, Module>(); // Modules by class name
	private HashMap<String, ModuleDescriptor> descriptors = new HashMap<String, ModuleDescriptor>(); // Descriptors by class name
	
	private CoreBinder binder = new CoreBinder();
	
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
		Log.e(TAG, "Service has been started!");

		// Plugin -> PluginPollingBroadcast
		mBroadcast = PluginPollingBroadcast.getInstance();
		registerReceiver(mBroadcast, new IntentFilter(
				Constants.INTENT_ACTION_DESCRIBE));
		registerReceiver(mBroadcast, new IntentFilter(
				Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER));
		registerReceiver(mBroadcast, new IntentFilter(
				Constants.INTENT_ACTION_PLUGIN_EVENT));

		// PluginPollingBroadcast -> mPluginCollection
		mPluginCollection = AndroidPluginCollection.getInstance();
		mBroadcast.registerPluginDetailsListener(mPluginCollection);

		Intent mIntent = new Intent(Constants.INTENT_ACTION_PLUGIN_POLL);
		sendBroadcast(mIntent);

		// Process descriptor files
		File descriptorFolder = getDescriptorFolder(this);
		String[] descriptors = descriptorFolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith("desc");
			}
		});
		for (String descriptor : descriptors) {
			ModuleDescriptor moduleDescriptor = parseModuleDescriptor(descriptor);
			addModule(moduleDescriptor);
		}
	}
	
	/**
	 * Parse a module descriptor.
	 * @param descriptorPath Path to the JSON descriptor file.
	 * @return The descriptor of the parsed module, or null if parsing was unsuccessful.
	 */
	public ModuleDescriptor parseModuleDescriptor(String descriptorPath) {
		Log.i(TAG, "Parsing module from descriptor " + descriptorPath);
		JSONObject json = null;
		String moduleName = null;
		String jarFile = null;
		String className = null;
		try {
			String fileContent = FileUtils.readFile(descriptorPath);
			json = new JSONObject(fileContent);
			jarFile = json.getString(JAR_FILE_KEY);
			className = json.getString(CLASS_NAME_KEY);
			moduleName = json.getString(MODULE_NAME_KEY);
			return new ModuleDescriptor(moduleName, className, jarFile);
		} catch (JSONException e) {
			Log.e(TAG, "Couldn't load parameters from descriptor " + descriptorPath);
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			Log.e(TAG, "Couldn't load " + className + " from " + jarFile + " : " + e.getMessage());
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Couldn't load " + className + " from " + jarFile + " : " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public List<ModuleDescriptor> getLoadedModules() {
		List<ModuleDescriptor> ret = new ArrayList<ModuleDescriptor>();
		for (Module module : modules.values()) {
			ModuleDescriptor descriptor = descriptors .get(module);
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
			PluginCollection pluginCollection = AndroidPluginCollection.getInstance();
			TimeServiceInterface timeservice = ModuleTimeService.getInstance();
			Log.e(TAG,"Calling constructor");
			module = constructor.newInstance(new SharedPrefs(this, className),
					new AndroidLogger(),
					pluginCollection,
					timeservice);
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
	}}
