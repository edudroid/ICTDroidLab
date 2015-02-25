package hu.edudroid.interfaces;

import java.util.Map;

public interface PluginEventListener {
	void onEvent(String plugin, String version, String eventName, Map<String, Object> extras);
}
