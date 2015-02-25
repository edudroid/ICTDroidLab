package hu.edudroid.interfaces;

import java.util.Map;

public interface PluginResultListener {

	public void onResult(final long id,
						final String plugin,
						final String pluginVersion,
						final String methodName,
						final Map<String, Object> result);

	public void onError(final long id,
						final String plugin,
						final String pluginVersion,
						final String methodName,
						final String errorMessage);
}