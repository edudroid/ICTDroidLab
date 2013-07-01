package hu.edudroid.ictpluginwifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.widget.Toast;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResultListener;

public class WiFiPlugin extends PluginCommunicationInterface {

	private static final List<String> mMethods=new ArrayList<String>();
	private static final List<String> mEvents=new ArrayList<String>();
	private static Plugin mPlugin;
	private static final Map<String,PluginEventListener> mEventListeners=new HashMap<String,PluginEventListener>();
	
	private static long mEventID=0;
	
	public WiFiPlugin(){
		
		mMethods.add("showIPAddress");
		mMethods.add("showMACAddress");
		mMethods.add("showNetMaskAddress");
		mMethods.add("showNetworkSpeed");
		
		mEvents.add("empty event");
		
		mPlugin=new Plugin() {
			
			@Override
			public String getVersionCode() {
				return "v1.0";
			}
			
			@Override
			public String getName() {
				return "WiFi Plugin";
			}
			
			@Override
			public List<String> getMethodNames() {
				return mMethods;
			}
			
			@Override
			public String getDescription() {
				return "This is a plugin for testing network parameters";
			}
			
			@Override
			public String getAuthor() {
				return "Patrik Weisz";
			}
			
			@Override
			public List<String> getAllEvents() {
				return mEvents;
			}
			
			@Override
			public List<String> callMethodSync(String method, List<Object> parameters) {
				List<String> answer=new ArrayList<String>();
				if(method.equals("showIPAddress")){
					answer.add("192.168.1.1");
					onEvent("empty event", null);
				}
				if(method.equals("showMACAddress")){
					answer.add("AC:00:FF:12:A4:34");
				}
				if(method.equals("showNetMaskAddress")){
					answer.add("255.255.255.0");
				}
				if(method.equals("showNetworkSpeed")){
					answer.add("54 Mbps");
				}
				return answer;				
			}
			
			@Override
			public long callMethodAsync(String method, List<Object> parameters,
					PluginResultListener listener) {
				// TODO Auto-generated method stub
				return -1;
			}

			@Override
			public void registerEventListener(String eventName,
					PluginEventListener listener) {
				mEventListeners.put(eventName, listener);				
			}
		};
	}
	
	@Override
	protected Plugin getPlugin() {
		return mPlugin;
	}

	@Override
	protected void onEvent(String eventName, List<String> params) {
		List<String> result=new ArrayList<String>();
		
		if(eventName.equals("empty event")){
			result.add("This ");
			result.add("an ");
			result.add("empty ");
			result.add("event.");
		}
		
		mEventListeners.get(eventName).onEvent(mEventID++, mPlugin.getName(),mPlugin.getVersionCode(), eventName, result);
	}
	
}
