package hu.edudroid.ict.plugins;

import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginListener;
import hu.edudroid.interfaces.PluginResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class AndroidPluginCollection implements PluginCollection, PluginListener {

	private static final String TAG = "AndroidPluginCollection";
	private static HashMap<String,Plugin> mPlugins = new HashMap<String,Plugin>();
	private Context context;
	private PluginIntentReceiver pluginIntentReceiver;

	public AndroidPluginCollection(Context context, PluginIntentReceiver pluginIntentReceiver) {
		this.context = context;
		this.pluginIntentReceiver = pluginIntentReceiver;
	}

	
	@Override
	public Plugin getPluginByName(String name) { 
		Log.i(TAG, "Requested " + name + ", searching among " + mPlugins.size() + " plugins.");
		return mPlugins.get(name);
	}
	
	@Override
	public List<Plugin> getAllPlugins() {
		Log.d(TAG, "Returning " + mPlugins.values().size() + " plugins.");
		return new ArrayList<Plugin>(mPlugins.values());
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		if (mPlugins.containsKey(plugin.getName())){
			Log.i(TAG, "We already have " + plugin.getName());
			return false;
		} else{
			Log.i(TAG, "Plugin discovered " + plugin.getName());
			synchronized (mPlugins) {
				Plugin pluginAdapter  = new PluginAdapter(plugin, pluginIntentReceiver, context);
				mPlugins.put(plugin.getName(), pluginAdapter);
			}
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
