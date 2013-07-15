package hu.edudroid.ict.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.util.Log;

public class ModuleUtils {
	private static final String TAG = "ModuleUtils";

	
	public static List<ModuleDescriptor> processModules(List<hu.edudroid.interfaces.ModuleDescriptor> loadedModules, List<hu.edudroid.interfaces.ModuleDescriptor> modulesInAssets) {
		Log.i(TAG, "Loaded modules " + loadedModules.size());
		Log.i(TAG, "Stored modules " + modulesInAssets.size());
		TreeSet<hu.edudroid.interfaces.ModuleDescriptor> orderer = new TreeSet<hu.edudroid.interfaces.ModuleDescriptor>(loadedModules);
		orderer.addAll(modulesInAssets);
		final List<hu.edudroid.interfaces.ModuleDescriptor> orderedModules = new ArrayList<hu.edudroid.interfaces.ModuleDescriptor>(orderer);
		final List<ModuleDescriptor> ret = new ArrayList<ModuleDescriptor>();
		for (hu.edudroid.interfaces.ModuleDescriptor descriptor : orderedModules) {
			ret.add(new ModuleDescriptor(descriptor, loadedModules.contains(descriptor)));
		}
		Log.i(TAG, "Total modules " + ret.size());
		return ret;
	}
}
