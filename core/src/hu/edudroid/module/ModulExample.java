package hu.edudroid.module;

import hu.edudroid.ict.plugins.Plugin;
import hu.edudroid.ict.plugins.PluginCollection;
import android.content.Context;
import android.util.Log;


public class ModulExample extends ModuleBase implements ModuleRunnable{

	
	public ModulExample(Context context) {
		super(context);
	}
	
	public void run(){
		log("Modul created");
		Plugin plugin1  = PluginCollection.getInstance().getPluginByName("Test Plugin 1");
		addPluginEventListener(plugin1, "showToast", new Object[]{"param1", "param2", "param3"});
		
		Plugin plugin2  = PluginCollection.getInstance().getPluginByName("Test Plugin 2");
		addPluginEventListener(plugin2, "showHelloWorld", new Object[]{"noName"});
		addPluginEventListener(plugin2, "showHelloWorldWithName", new Object[]{"ThisIsMyName!"});
	}

	@Override
	protected String getModulName(){
		return "TestModule";
	}

	@Override
	public void onResult(String plugin, String pluginVersion,
			String methodName, String result, String meta) {
		Log.e("Event report ", plugin + " " + methodName + " " + result);
	}

	@Override
	public void onError(String plugin, String pluginVersion, String methodName,
			String errorMessage, String meta) {
		Log.e("Event error ", plugin + " " + methodName + " " + errorMessage);
	}
}
