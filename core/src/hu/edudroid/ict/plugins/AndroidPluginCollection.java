package hu.edudroid.ict.plugins;

import hu.edudroid.ict.R;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.module.ModuleLoader;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class AndroidPluginCollection implements PluginCollection, PluginListener{

	private static AndroidPluginCollection	mInstance	= null;
	private PluginPollingBroadcast			mBroadcast	= null;
	private ArrayList<Plugin>			mPlugins	= null;

	private AndroidPluginCollection() {
		mPlugins = new ArrayList<Plugin>();		
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
	
	public Plugin getPluginByHashcode(final int hash){
		Log.e("PLUGIN", "# of plugins = " + mPlugins.size());
		for (int i = 0; i < mPlugins.size(); i++){
			Log.e("PLUGIN", "(" + i + ") hash: " + mPlugins.get(i).hashCode());
			if (mPlugins.get(i).hashCode() == hash)
				return mPlugins.get(i);
		}
		return null;
	}
	
	@Override
	public Plugin getPluginByName(final String name) {
		for (int i=0; i< mPlugins.size(); i++) {
			if (mPlugins.get(i).getName().equals(name))
				return mPlugins.get(i);
		}
		return null;
	}
	
	@Override
	public ArrayList<Plugin> getAllPlugins() {
		return mPlugins;
	}

	@Override
	public boolean newPlugin(Plugin plugin) {
		boolean isAlreadyInList=false;
		for(int i=0;i<mPlugins.size();i++){
			if(mPlugins.get(i).getName().equals(plugin.getName())){
				isAlreadyInList=true;
			}
		}
		if(!isAlreadyInList){
			mPlugins.add(plugin);
			Log.e("New Plugin added to PluginCollection!",plugin.getName());
			return true;			
		}		
		else{
			Log.e("Plugin is already in PluginCollection!",plugin.getName());
			return false;
		}
	}

	@Override
	public boolean newPluginMethod(PluginMethod method) {
		return true;
	}
}
