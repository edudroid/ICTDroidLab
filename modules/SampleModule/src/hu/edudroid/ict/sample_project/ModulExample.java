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
			plugin2.callMethodAsync("getBSSID", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			plugin2.callMethodAsync("getSSID", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			plugin2.callMethodAsync("isHiddenSSID", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			plugin2.callMethodAsync("getIpAddress", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			plugin2.callMethodAsync("getMacAddress", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			plugin2.callMethodAsync("getLinkSpeed", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			plugin2.callMethodAsync("getNetworkId", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			plugin2.callMethodAsync("getRssi", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			plugin2.callMethodAsync("getDescribeContents", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}),this);
			
			plugin2.registerEventListener("empty event", this);
			plugin2.registerEventListener("scanned networks", this);
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
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, List<String> result) {
		mLogger.e("REPORT in module", plugin + " " + methodName + " " + result);
	}


	@Override
	public void onError(long id, String plugin, String pluginVersion, String methodName,
			String errorMessage) {
		mLogger.e("ERROR in module ", plugin + " " + methodName + " " + errorMessage);
	}

	@Override
	public void onEvent(String plugin, String version, String eventName, List<String> extras) {
		mLogger.e("EVENT in module ", eventName);
		for(int i=0;i<extras.size();i++){
			mLogger.e("Result:", extras.get(i));
		}
	}
} 