package hu.edudroid.ict.plugins;

import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

public class AndroidPluginCollection implements PluginCollection, PluginListener {

	private static final String TAG = null;
	private static AndroidPluginCollection	mInstance	= null;
	private HashMap<String, Plugin>			mPlugins	= null;

	private AndroidPluginCollection() {
		mPlugins = new HashMap<String, Plugin>();		
	}

	public static AndroidPluginCollection getInstance(){
		if (mInstance == null){
			synchronized (AndroidPluginCollection.class){
				if (mInstance == null)
					mInstance = new AndroidPluginCollection();
			}
		}
		return mInstance;
	}
	
	@Override
	public Plugin getPluginByName(String name) {
		return mPlugins.get(name);
	}
	
	@Override
	public ArrayList<Plugin> getAllPlugins() {
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
}
