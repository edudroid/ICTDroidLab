package hu.edudroid.module;

import hu.edudroid.ict.CoreService;
import hu.edudroid.ict.ModuleSetListener;
import hu.edudroid.ict.ModuleStatsListener;
import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.ModuleDescriptor;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.util.Log;
import dalvik.system.DexClassLoader;

public class ModuleManager implements ModuleStatsListener{
	private static final String TAG = null;
	private HashMap<String, ModuleWrapper> moduleWrappers = new HashMap<String, ModuleWrapper>(); // Modules by class name
	private HashMap<String, ModuleDescriptor> descriptors = new HashMap<String, ModuleDescriptor>(); // Descriptors by class name
	private HashMap<String, TimeServiceInterface> timers = new HashMap<String, TimeServiceInterface>();
	private HashSet<ModuleSetListener> moduleSetListeners = new HashSet<ModuleSetListener>();
	private HashSet<ModuleStatsListener> moduleStatsListeners = new HashSet<ModuleStatsListener>();
	
	private CoreService coreService;
	
	public ModuleManager(CoreService coreService) {
		this.coreService = coreService;
	}

	public List<ModuleDescriptor> getLoadedModules() {
		List<ModuleDescriptor> ret = new ArrayList<ModuleDescriptor>();
		for (String moduleClass : moduleWrappers.keySet()) {
			ModuleDescriptor descriptor = descriptors.get(moduleClass);
			ret.add(descriptor);
		}
		return ret;
	}
	
	public Map<String, String> getModuleStats(String className) {
		ModuleWrapper wrapper = moduleWrappers.get(className);
		if (wrapper != null) {
			 return wrapper.getStats();
		} else {
			return null;
		}
	}

	public boolean addModule(ModuleDescriptor moduleDescriptor, PluginCollection pluginCollection) {
		Log.e(TAG, "Adding module");
		if (moduleWrappers.containsKey(moduleDescriptor.getClassName())) {
			Log.w(TAG, "Module " + moduleDescriptor.getClassName() + " already loaded.");
			return false;
		}
		try {
			File jarFolder = CoreService.getJarFolder(coreService);
			String dexedJavaFile = new File(jarFolder, moduleDescriptor.getJarFile()).getAbsolutePath();
			String className = moduleDescriptor.getClassName();
			Log.i(TAG, "Loading module " + className + " from file " + dexedJavaFile);
			ModuleWrapper moduleWrapper = null; 
			File dexOptimizedFolder = new File(coreService.getFilesDir(), CoreService.TEMP_DIR);
			dexOptimizedFolder.mkdirs();
			DexClassLoader dexLoader = new DexClassLoader(dexedJavaFile, 
															dexOptimizedFolder.getAbsolutePath(), 
															null, 
															coreService.getClassLoader());
			try {
				Class<?> dexLoadedClass = dexLoader.loadClass(className);
				@SuppressWarnings("unchecked")
				Constructor<Module> constructor = (Constructor<Module>) dexLoadedClass.getConstructor(Preferences.class, Logger.class, PluginCollection.class, TimeServiceInterface.class);
				if (constructor == null) {
					throw new NoSuchMethodException("Couldn't find proper consturctor.");
				}
				TimeServiceInterface timeService = new ModuleTimeService();
				timers.put(className, timeService);
				moduleWrapper = new ModuleWrapper(className, constructor, new SharedPrefs(coreService, className),
						new AndroidLogger(className),
						pluginCollection,
						timeService, coreService);
				moduleWrapper.registerModuleStatsListener(this);
				Log.e(TAG, "Module added");
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
			if (moduleWrapper == null) {
				Log.e(TAG, "Module couldn't be loaded.");
				return false;
			}
			moduleWrappers.put(moduleDescriptor.getClassName(), moduleWrapper);
			this.descriptors.put(moduleDescriptor.getClassName(), moduleDescriptor);
			try {
				moduleWrapper.init();
			} catch (Exception e){
				Log.e(TAG, "Error initializing module " + moduleDescriptor.getModuleName() + " : " + e.getMessage());
				e.printStackTrace();
			}
			for (ModuleSetListener listener : moduleSetListeners) {
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

	public boolean removeModule(String moduleName, AndroidPluginCollection pluginCollection) {
		Log.w(TAG, "Removing module " + moduleName);
		Module module = moduleWrappers.remove(moduleName);
		ModuleDescriptor descriptor = descriptors.remove(moduleName);
		if (module != null) {
			TimeServiceInterface timer = timers.remove(moduleName);
			timer.cancelAll();
			pluginCollection.removeEventListener(module);
			pluginCollection.removeResultListener(module);
			for (ModuleSetListener listener : moduleSetListeners) {
				listener.moduleRemoved(descriptor);
			}
			Log.w(TAG, "Module removed " + moduleName);
			return true;
		}
		Log.e(TAG, "Couldn't remove module " + moduleName);		
		return false;
	}

	public void registerModuleSetListener(ModuleSetListener listener) {
		moduleSetListeners.add(listener);
	}

	public void unregisterModuleSetListener(ModuleSetListener listener) {
		moduleSetListeners.remove(listener);
	}

	public void registerModuleStatsListener(ModuleStatsListener listener) {
		moduleStatsListeners.add(listener);
	}

	public void unregisterModuleStatsListener(ModuleStatsListener listener) {
		moduleStatsListeners.remove(listener);
	}

	public ModuleDescriptor getModule(String moduleName) {
		return descriptors.get(moduleName);
	}

	@Override
	public void moduleSTatsChanged(String className,
			Map<String, String> stats) {
		Log.e(TAG, "Stats changed");
		for (ModuleStatsListener listener : moduleStatsListeners) {
			listener.moduleSTatsChanged(className, stats);
		}
	}

}
