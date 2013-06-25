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
		
		Plugin plugin2  = PluginCollection.getInstance().getPluginByName("WiFi Plugin");
		addPluginEventListener(plugin2, "showIPAddress", new Object[]{"WiFiparam1","WiFiparam2","WiFiparam3"});
	}

	@Override
	protected String getModulName(){
		return "TestModule";
	}

	@Override
	protected void eventHandleReport(String plugin, String method, String msg){
		Log.e("Event report ", plugin + " " + method + " " + msg);
		
	}

	@Override
	protected void eventHandleError(String plugin, String method, String msg){
		Log.e("Event error ", plugin + " " + method + " " + msg);
		
	}
}
