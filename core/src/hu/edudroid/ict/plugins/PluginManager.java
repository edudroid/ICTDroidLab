package hu.edudroid.ict.plugins;

import hu.edudroid.interfaces.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PluginManager {
	
	/**
	 * Returns the list of available plugins. List is compiled from list of available plugins downloaded from the server, and locally found plugins.
	 * @param availablePlugins The list of available plugins - obtained from the server
	 * @param downloadedPlugins The list of plugins already present on the system.
	 * @return A list of plugin descriptors representing available and downloaded plugins.
	 */
	public static List<PluginDescriptor> getAvailablePlugins(List<PluginDescriptor> availablePlugins, List<Plugin> downloadedPlugins) {
		HashMap<String,PluginDescriptor> plugins = new HashMap<String, PluginDescriptor>();
		HashMap<String,PluginDescriptor> availablePluginsHash = new HashMap<String, PluginDescriptor>();
		if (availablePlugins != null) {
			for (PluginDescriptor descr : availablePlugins) {
				availablePluginsHash.put(descr.getPackageName(), descr);
			}
		}
		if (downloadedPlugins != null) {
			for (Plugin downloadedPlugin : downloadedPlugins) {
				if (plugins.containsKey(downloadedPlugin.getClass().getPackage().getName())) {
					plugins.get(downloadedPlugin.getClass().getPackage().getName()).setPlugin(downloadedPlugin);
				} else {
					plugins.put(downloadedPlugin.getClass().getPackage().getName(), new PluginDescriptor(downloadedPlugin));
				}
			}
		}
		List<PluginDescriptor> ret = new ArrayList<PluginDescriptor>(plugins.values());
		return ret;
	}
}
