package hu.edudroid.ict.plugins;

import hu.edudroid.interfaces.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PluginManager {
	
	/**
	 * Returns the list of available plugins. List is compiled from list of available plugins downloaded from the server, and locally found plugins.
	 * @param downloadedPlugins The list of plugins already present on the system.
	 * @return A list of plugin descriptors representing available and downloaded plugins.
	 */
	public static List<PluginDescriptor> getAvailablePlugins(List<Plugin> downloadedPlugins) {
		HashMap<String,PluginDescriptor> plugins = new HashMap<String, PluginDescriptor>();
		PluginDescriptor wifi = new PluginDescriptor("WiFi plugin", "hu.edudroid.ictpluginwifi", "A plugin for WiFi.");
		PluginDescriptor social = new PluginDescriptor("Social plugin", "hu.edudroid.ictpluginsocial", "A plugin for social stuff.");
		plugins.put(wifi.getPackageName(), wifi);
		plugins.put(social.getPackageName(), social);
		for (Plugin downloadedPlugin : downloadedPlugins) {
			if (plugins.containsKey(downloadedPlugin.getClass().getPackage().getName())) {
				plugins.get(downloadedPlugin.getClass().getPackage().getName()).setPlugin(downloadedPlugin);
			} else {
				plugins.put(downloadedPlugin.getClass().getPackage().getName(), new PluginDescriptor(downloadedPlugin));
			}
		}
		List<PluginDescriptor> ret = new ArrayList<PluginDescriptor>(plugins.values());
		return ret;
	}
}
