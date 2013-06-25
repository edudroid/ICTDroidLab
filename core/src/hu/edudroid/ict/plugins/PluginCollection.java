package hu.edudroid.ict.plugins;

import java.util.ArrayList;
import android.util.Log;

public class PluginCollection {

	private static PluginCollection	mInstance	= null;
	public ArrayList<Plugin>		mPlugins	= null;

	private PluginCollection() {
		mPlugins = new ArrayList<Plugin>();
	}

	public static PluginCollection getInstance(){
		if (mInstance == null){
			synchronized (PluginCollection.class){
				if (mInstance == null)
					mInstance = new PluginCollection();
			}
		}

		return mInstance;
	}
	
	public Plugin getPluginByHashcode(final int hash){
		Log.e("PLUGIN", "# of plugins = " + mPlugins.size());
		for (int i = 0; i < mPlugins.size(); i++){
			Log.e("PLUGIN", "(" + i + ") hash: " + mPlugins.get(i).hashCode());
			if (mPlugins.get(i).hashCode() == hash)
				return mPlugins.get(i);
		}
		return null;
	}
	
	public Plugin getPluginByName(final String name) {
		for (int i=0; i< mPlugins.size(); i++) {
			if (mPlugins.get(i).getName().equals(name))
				return mPlugins.get(i);
		}
		return null;
	}
}
