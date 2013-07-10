package hu.edudroid.ict.sample_project2;

import hu.edudroid.interfaces.Logger;
import hu.edudroid.interfaces.ModuleBase;
import hu.edudroid.interfaces.ModuleTimerListener;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginCollection;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResultListener;
import hu.edudroid.interfaces.Preferences;
import hu.edudroid.interfaces.TimeServiceInterface;

import java.util.Arrays;
import java.util.List;

public class ModulExample2 extends ModuleBase implements PluginEventListener, PluginResultListener, ModuleTimerListener {
	
	private static final String 	TAG			 	= "ModuleExample2";
	
	private Plugin plugin2;
	
	public ModulExample2() {
		super();
	}
	
	@Override
	public void init(Preferences prefs, Logger logger, PluginCollection pluginCollection, TimeServiceInterface timeservice){
		super.init(prefs, logger, pluginCollection, timeservice);
		
		//mTimeService.runAt(0,this);
		mTimeService.runPeriodic(5000, 10000, 2, this);
		
		plugin2  = mPluginCollection.getPluginByName("WiFi Plugin");
		
	}
	
	public void run(){
		
		if (plugin2 != null) {
			/*
			plugin2.callMethodAsync("getBSSID", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getSSID", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("isHiddenSSID", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getIpAddress", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getMacAddress", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getLinkSpeed", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getNetworkId", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getRssi", Arrays.asList(new Object[]{"empty"}),this);
			plugin2.callMethodAsync("getDescribeContents", Arrays.asList(new Object[]{"empty"}),this);
			*/
			/*
			long callScanId=plugin2.callMethodAsync("scanning", Arrays.asList(new Object[]{"0","10000","1"}),this);
			answersForScanning.add(callScanId);
			*/
			/*
			long callPingId=plugin2.callMethodAsync("ping", Arrays.asList(new Object[]{"173.194.39.64","5"}),this);
			answersForPing.add(callPingId);
			*/
			plugin2.callMethodAsync("traceroute", Arrays.asList(new Object[]{"173.194.39.64"}),this);			
			
		} else {
			mLogger.e(TAG, "Couldn't find WiFi Plugin");
		}
	}
	
	@Override
	public String getModuleName(){
		return "TestModule2";
	}

	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, List<String> result) {
		mLogger.e("REPORT in module: "+getModuleName(), id+" " +plugin + " " + methodName + " " + result);
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

	@Override
	public String getEventListenerName() {
		return this.getModuleName();
	}
} 