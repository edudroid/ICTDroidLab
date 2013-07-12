package hu.edudroid.interfaces;

import java.util.List;

public interface PluginResultListener {

	void onEvent(String plugin, String version, String eventName, List<String> extras);

	public void onResult(final long id,
						final String plugin,
						final String pluginVersion,
						final String methodName,
						final List<String> result);

	public void onError(final long id,
						final String plugin,
						final String pluginVersion,
						final String methodName,
						final String errorMessage);
}
