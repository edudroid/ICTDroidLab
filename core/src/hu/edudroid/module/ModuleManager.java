package hu.edudroid.module;

import hu.edudroid.ict.CoreService;
import hu.edudroid.ict.ModuleSetListener;
import hu.edudroid.ict.ModuleStatsListener;
import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;
import hu.edudroid.interfaces.ThreadSemaphore;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import dalvik.system.DexClassLoader;

public class ModuleManager implements ModuleStatsListener{
	private static final String TAG = ModuleManager.class.getName();
	
	private HashMap<String, ModuleWrapper> moduleWrappers = new HashMap<String, ModuleWrapper>(); // Modules by moduleId
	private HashMap<String, TimeServiceInterface> timers = new HashMap<String, TimeServiceInterface>();
	private HashSet<ModuleSetListener> moduleSetListeners = new HashSet<ModuleSetListener>();
	private HashSet<ModuleStatsListener> moduleStatsListeners = new HashSet<ModuleStatsListener>();
	
	private HashMap<String, ThreadSemaphore> semaphores = new HashMap<String, ThreadSemaphore>();
	
	private CoreService coreService;
	
	public ModuleManager(CoreService coreService) {
		this.coreService = coreService;
	}

	public List<ModuleDescriptor> getLoadedModules() {
		List<ModuleDescriptor> ret = new ArrayList<ModuleDescriptor>();
		for (String moduleId : moduleWrappers.keySet()) {
			ModuleDescriptor descriptor = moduleWrappers.get(moduleId).getDescriptor();
			ret.add(descriptor);
		}
		return ret;
	}
	
	public List<ThreadSemaphore> getLoadedModuleSemaphores() {
		List<ThreadSemaphore> ret = new ArrayList<ThreadSemaphore>();
		for (String moduleClass : moduleWrappers.keySet()) {
			ThreadSemaphore semaphore = semaphores.get(moduleClass);
			ret.add(semaphore);
		}
		return ret;
	}

	public Map<String, String> getModuleStats(String moduleId) {
		ModuleWrapper wrapper = moduleWrappers.get(moduleId);

		if (wrapper != null) {
			 return wrapper.getStats();
		} else {
			return null;
		}
	}

	/**
	 * Activates a module. Only modules in state INSTALLED are added to the system.
	 * @param moduleDescriptor
	 * @param pluginCollection
	 * @return
	 */
	public boolean startModule(ModuleDescriptor moduleDescriptor, PluginCollection pluginCollection) {
		Log.e(TAG, "Adding module");
		if (moduleWrappers.containsKey(moduleDescriptor.moduleId)) {
			Log.w(TAG, "Module " + moduleDescriptor.moduleId + " already loaded.");
			return false;
		}
		if (moduleDescriptor.getState(coreService) != ModuleState.INSTALLED) {
			Log.w(TAG, "Module " + moduleDescriptor.moduleId + " should not be running.");
			return false;
		}
		try {
			File jarFolder = CoreService.getJarFolder(coreService);
			String dexedJavaFile = new File(jarFolder, moduleDescriptor.jarFile).getAbsolutePath();
			String className = moduleDescriptor.className;
			String moduleId = moduleDescriptor.moduleId;
			Log.i(TAG, "Loading module " + className + " from file " + dexedJavaFile);
			ModuleWrapper moduleWrapper = null; 
			File dexOptimizedFolder = new File(coreService.getFilesDir(), CoreService.TEMP_DIR);
			dexOptimizedFolder.mkdirs();
			DexClassLoader dexLoader = new DexClassLoader(dexedJavaFile, 
															dexOptimizedFolder.getAbsolutePath(), 
															null, 
															coreService.getClassLoader());

				Class<?> dexLoadedClass = dexLoader.loadClass(className);
				@SuppressWarnings("unchecked")
				Constructor<Module> constructor = (Constructor<Module>) dexLoadedClass.getConstructor(Preferences.class, Logger.class, PluginCollection.class, TimeServiceInterface.class, ThreadSemaphore.class);
				if (constructor == null) {
					throw new NoSuchMethodException("Couldn't find proper consturctor.");
				}
				TimeServiceInterface timeService = new ModuleTimeService();
				timers.put(moduleId, timeService);
				ThreadSemaphore threadSemaphore = new ModuleSemaphore();
				semaphores.put(moduleId, threadSemaphore);
				moduleWrapper = new ModuleWrapper(moduleDescriptor, constructor, new SharedPrefs(coreService, moduleId),
                        new AndroidLogger(moduleId),
                        pluginCollection,
						timeService, threadSemaphore, coreService);
				moduleWrapper.registerModuleStatsListener(this);
				moduleWrappers.put(moduleDescriptor.moduleId, moduleWrapper);
                Log.e(TAG, "Module added");
                // Initializing module
                try {
                        moduleWrapper.init();
                } catch (Exception e){
                        Log.e(TAG, "Error initializing module " + moduleDescriptor.moduleName + " : " + e.getMessage());
                        e.printStackTrace();
                }
                for (ModuleSetListener listener : moduleSetListeners) {
                        listener.moduleAdded(moduleDescriptor);
                }
                return true;
        } catch (ClassNotFoundException e) {
                Log.e(TAG, "Error loading module.", e);
                e.printStackTrace();
                return false;
        } catch (InstantiationException e) {
                Log.e(TAG, "Error loading module.", e);
                e.printStackTrace();
                return false;
        } catch (IllegalAccessException e) {
                Log.e(TAG, "Error loading module.", e);
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
        } catch (Exception e) {
                Log.e(TAG, "Couldn't load module " + e);
                e.printStackTrace();
                return false;
        }
}

	public boolean removeModule(String moduleId, AndroidPluginCollection pluginCollection) {
		Log.w(TAG, "Removing module " + moduleId);
		ModuleWrapper module = moduleWrappers.remove(moduleId);
		if (module != null) {
			TimeServiceInterface timer = timers.remove(moduleId);
			timer.cancelAll();
			pluginCollection.removeEventListener(module);
			pluginCollection.removeResultListener(module);
			// Saves delete event
			module.getDescriptor().setSate(ModuleState.BANNED, coreService);
			for (ModuleSetListener listener : moduleSetListeners) {
				listener.moduleRemoved(module.getDescriptor());
			}
			Log.w(TAG, "Module removed " + moduleId);
			return true;
		}
		Log.e(TAG, "Couldn't remove module " + moduleId);		
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

	public ModuleDescriptor getModule(String moduleId) {
		if (moduleWrappers.containsKey(moduleId)) {
			return moduleWrappers.get(moduleId).getDescriptor();
		} else {
			List<ModuleDescriptor> moduleDescriptors = ModuleLoader.getAllModules(coreService);
			for (ModuleDescriptor descriptor : moduleDescriptors) {
				if (descriptor.moduleId.equals(moduleId)) {
					return descriptor;
				}
			}
		}
		return null;
	}

	@Override
	public void moduleStatsChanged(String moduleId,
			Map<String, String> stats) {
		Log.e(TAG, "Stats changed");
		for (ModuleStatsListener listener : moduleStatsListeners) {
			listener.moduleStatsChanged(moduleId, stats);
		}
	}

	public boolean installModule(ModuleDescriptor moduleDescriptor,
			AndroidPluginCollection pluginCollection, Context context) {
		if (moduleDescriptor.getState(context) != ModuleState.AVAILABLE) {
			return false;
		}
		moduleDescriptor.setSate(ModuleState.INSTALLED, context);
		return startModule(moduleDescriptor, pluginCollection);
	}

}
