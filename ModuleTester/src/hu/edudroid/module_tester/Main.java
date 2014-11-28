package hu.edudroid.module_tester;

import java.util.List;

import hu.edudroid.ict.sample_module.ModuleExample;
import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.Preferences;

public class Main implements Preferences, Logger, PluginCollection {
	
	public Main() {
		ModuleExample moduleExample = new ModuleExample(this, this, this, new ModuleTimeService());
		moduleExample.init();
	}
	
	public static void main(String[] args) {
		new Main();
	}


	@Override
	public Plugin getPluginByName(String string) {
		return null;
	}

	@Override
	public List<Plugin> getAllPlugins() {
		return null;
	}

	@Override
	public void e(String tag, String message) {
		System.out.println("E " + tag + " : " + message);
	}

	@Override
	public void d(String tag, String message) {
		System.out.println("D " + tag + " : " + message);
	}

	@Override
	public void i(String tag, String message) {
		System.out.println("I " + tag + " : " + message);
	}

	@Override
	public void putString(String key, String value) {
	}

	@Override
	public String getString(String key, String defaultValue) {
		return null;
	}

	@Override
	public void putInt(String key, int value) {
	}

	@Override
	public int getInt(String key, int defaultValue) {
		return 0;
	}

	@Override
	public long getLong(String key, long defaultValue) {
		return 0;
	}

	@Override
	public void putLong(String key, long value) {
	}

	@Override
	public boolean getBoolean(String key, boolean defaultValue) {
		return false;
	}

	@Override
	public void putBoolean(String key, boolean value) {
	}
}