package hu.edudroid.ict.sample_project;

import hu.edudroid.ict.plugins.Plugin;
import hu.edudroid.ict.plugins.PluginCollection;
import hu.edudroid.module.ModuleBase;

public class ModulExample extends ModuleBase {
	
	private static final String TAG = "ModuleExample";


	public ModulExample() {
		super();
	}
	
	public void run(){
		mLogger.d(TAG, "Modul created");
		Plugin plugin1  = PluginCollection.getInstance().getPluginByName("Test Plugin 1");
		addPluginEventListener(plugin1, "showToast", new Object[]{"param1", "param2", "param3"});
		
		Plugin plugin2  = PluginCollection.getInstance().getPluginByName("WiFi Plugin");
		addPluginEventListener(plugin2, "showIPAddress", new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"});
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