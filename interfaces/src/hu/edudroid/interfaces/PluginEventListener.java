package hu.edudroid.interfaces;

import java.util.List;

public interface PluginEventListener {
	void onEvent(long id, String plugin, String version, String eventName, List<String> result);
}
