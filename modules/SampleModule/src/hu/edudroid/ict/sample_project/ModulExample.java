package hu.edudroid.ict.sample_project;

import hu.edudroid.interfaces.ModuleBase;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResultListener;

import java.util.Arrays;
import java.util.List;

public class ModulExample extends ModuleBase implements PluginEventListener, PluginResultListener {
	
	private static final String 	TAG			 	= "ModuleExample";

	public ModulExample() {
		super();
	}
	
	public void run(){
		mLogger.d(TAG, "Modul created");
		
		Plugin plugin2  = mPluginCollection.getPluginByName("WiFi Plugin");
		if (plugin2 != null) {
			long id2 = plugin2.callMethodAsync("showIPAddress", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			long id3 = plugin2.callMethodAsync("showMACAddress", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			long id4 = plugin2.callMethodAsync("showNetMaskAddress", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			long id5 = plugin2.callMethodAsync("showNetworkSpeed", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			
			plugin2.registerEventListener("empty event", this);
		} else {
			mLogger.e(TAG, "Couldn't find WiFi Plugin");
		}
		mLogger.d(TAG,"Modul run ended");
	}
	
	@Override
	public String getModuleName(){
		return "TestModule";
	}

	@Override
	public void onResult(int id, String plugin, String pluginVersion,
			String methodName, String result, String meta) {
		mLogger.e("Event report ", plugin + " " + methodName + " " + result);
	}


	@Override
	public void onError(String plugin, String pluginVersion, String methodName,
			String errorMessage, String meta) {
		mLogger.e("Event error ", plugin + " " + methodName + " " + errorMessage);
	}

	@Override
	public void onEvent(List<String> params) {
				
	}
} 