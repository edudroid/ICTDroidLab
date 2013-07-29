package hu.edudroid.ict.plugins;

import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginListener;
import hu.edudroid.interfaces.PluginResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.util.Log;

public class AndroidPluginCollection implements PluginCollection, PluginListener {

	private static final String TAG = "AndroidPluginCollection";
	private static HashMap<String, Plugin>			mPlugins	= null;

	public AndroidPluginCollection() {
		mPlugins = new HashMap<String, Plugin>();		
	}

	
	@Override
	public Plugin getPluginByName(String name) {
		Log.i(TAG, "Requested " + name + ", searching among " + mPlugins.size() + " plugins.");
		Plugin res=mPlugins.get(name);
		if (res!=null){
			Log.i(TAG,"Plugin returned");
			return res;
		}
		else{
			Log.i(TAG,"No such plugin");
			return null;
		}
	}
	
	@Override
	public List<Plugin> getAllPlugins() {
		return new ArrayList<Plugin>(mPlugins.values());
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		if (mPlugins.containsKey(plugin.getName())){
			Log.i(TAG, "We already have " + plugin.getName());
			return false;
		} else{
			
			Log.i(TAG, "Plugin discovered " + plugin.getName());
			mPlugins.put(plugin.getName(), plugin);
			return true;
		}
	}
	
	public void removeEventListener(PluginEventListener listener){
		for (Plugin plugin : mPlugins.values()){
			plugin.unregisterEventListener(listener);
		}
	}
	
	public void removeResultListener(PluginResultListener listener){
		for (Plugin plugin : mPlugins.values()){
			plugin.cancelCallsForListener(listener);
		}
	}
}
