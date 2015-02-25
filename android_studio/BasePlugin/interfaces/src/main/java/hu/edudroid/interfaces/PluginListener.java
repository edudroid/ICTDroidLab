package hu.edudroid.interfaces;


public interface PluginListener {
	/**
	 * Called when a new plugin was received
	 * @param plugin The new plugin that is available.
	 * @return True if plugin was new for this receiver, false othervise
	 */
	public boolean newPlugin(Plugin plugin);
}
