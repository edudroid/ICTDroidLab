package hu.edudroid.ict.sample_project;

import hu.edudroid.interfaces.ModuleBase;
import hu.edudroid.interfaces.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModulExample extends ModuleBase {
	
	private static final String TAG = "ModuleExample";

	public ModulExample() {
		super();
	}
	
	public void run(){
		mLogger.d(TAG, "Modul created");
		Plugin plugin1  = mPluginCollection.getPluginByName("Test Plugin 1");
		if (plugin1 != null) {
			plugin1.callMethod("showToast", Arrays.asList(new Object[]{"param1", "param2", "param3"}));
		} else {
			mLogger.e(TAG, "Couldn't find Test Plugin 1");
		}
		
		Plugin plugin2  = mPluginCollection.getPluginByName("WiFi Plugin");
		if (plugin2 != null) {
			plugin2.callMethod("showIPAddress", Arrays.asList(new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"}));
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
	public void onResult(String plugin, String pluginVersion,
			String methodName, String result, String meta) {
		mLogger.e("Event report ", plugin + " " + methodName + " " + result);
	}


	@Override
	public void onError(String plugin, String pluginVersion, String methodName,
			String errorMessage, String meta) {
		mLogger.e("Event error ", plugin + " " + methodName + " " + errorMessage);
	}
} 