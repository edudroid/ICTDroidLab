package hu.edudroid.interfaces;

public interface PluginResultListener {

	public void onResult(final String plugin,
						final String pluginVersion,
						final String methodName,
						final String result,
						final String meta);

	public void onError(final String plugin,
						final String pluginVersion,
						final String methodName,
						final String errorMessage,
						final String meta);
}
