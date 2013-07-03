package hu.edudroid.interfaces;

import java.util.List;

public interface PluginEventListener {
	void onEvent(String plugin, String version, String eventName, List<String> extras);
}
