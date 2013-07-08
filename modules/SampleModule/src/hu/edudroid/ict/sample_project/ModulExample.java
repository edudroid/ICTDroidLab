package hu.edudroid.ict.sample_project;

import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.Module;
import hu.edudroid.interfaces.ModuleTimerListener;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResultListener;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;

import java.util.Arrays;
import java.util.List;

public class ModulExample extends Module implements PluginEventListener, PluginResultListener, ModuleTimerListener {
	
	public ModulExample(Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeservice) {
		super(prefs, logger, pluginCollection, timeservice);
	}

	private static final String 	TAG			 	= "ModuleExample";
	
	private Plugin plugin2;
	
	@Override
	public void init(){
		answersForScanning.clear();
		answersForPing.clear();
		answersForTraceroute.clear();
		
		mTimeService.runPeriodic(1000, 10000, 2, this);
		
		plugin2  = mPluginCollection.getPluginByName("WiFi Plugin");
		//plugin2.registerEventListener("empty event", this);		
	}
	
	public void run(){
		
		if (plugin2 != null) {
			plugin2.callMethodAsync("getBSSID", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getSSID", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("isHiddenSSID", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getIpAddress", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getMacAddress", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getLinkSpeed", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getNetworkId", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getRssi", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getDescribeContents", Arrays.asList(new Object[]{"empty"}),this);
			
			
			long callScanId=plugin2.callMethodAsync("scanning", Arrays.asList(new Object[]{"0","10000","1"}),this);
			mLogger.e("scanning callID:", String.valueOf(callScanId));
			long callPingId=plugin2.callMethodAsync("ping", Arrays.asList(new Object[]{"173.194.39.64","5"}),this);
			mLogger.e("ping callID:", String.valueOf(callPingId));
			/*
			long callTracerouteId=plugin2.callMethodAsync("traceroute", Arrays.asList(new Object[]{"127.0.0.1"}),this);
			answersForTraceroute.add(callTracerouteId);
			*/
			
			
		} else {
			mLogger.e(TAG, "Couldn't find WiFi Plugin");
		}
		mLogger.d(TAG,"Modul run ended");
	}
	
	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, List<String> result) {
		mLogger.e("REPORT in module: "+getModuleName(), id+" " + plugin + " " + methodName + " " + result);
	}


	@Override
	public void onError(long id, String plugin, String pluginVersion, String methodName,
			String errorMessage) {
		mLogger.e("ERROR in module: "+getModuleName(), plugin + " " + methodName + " " + errorMessage);
	}

	@Override
	public void onEvent(String plugin, String version, String eventName, List<String> extras) {
		mLogger.e("EVENT in module: "+getModuleName(), eventName +" size: " +extras.size());
		
		for(int i=0;i<extras.size();i++){
			mLogger.e("Result:", extras.get(i));
		}
		
	}

	@Override
	public void onTimerEvent() {
		mLogger.e("TIMER in module: "+getModuleName(), "timer event");
		this.run();		
	}
} 