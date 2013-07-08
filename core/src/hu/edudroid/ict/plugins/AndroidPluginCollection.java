package hu.edudroid.ict.plugins;

import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;

import java.util.ArrayList;

public class AndroidPluginCollection implements PluginCollection, PluginListener {

	private static AndroidPluginCollection	mInstance	= null;
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
		for (int i = 0; i < mPlugins.size(); i++){
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
			return true;			
		}		
		else{
			return false;
		}
	}

	@Override
	public boolean newPluginMethod(PluginMethod method) {
		return true;
	}
}
