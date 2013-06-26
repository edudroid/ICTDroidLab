package hu.edudroid.interfaces;

public interface Module {
	public void run();
	public String getModuleName();
	public void init(Preferences prefs, Logger logger, PluginCollection pluginCollection);
}
