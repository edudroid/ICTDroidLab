package hu.edudroid.interfaces;

import java.util.List;

public interface PluginCollection {

	Plugin getPluginByName(String string);
	List<Plugin> getAllPlugins();

}
