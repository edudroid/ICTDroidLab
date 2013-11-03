package hu.edudroid.ictpluginwifi;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResultListener;
import hu.edudroid.interfaces.Quota;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WiFiPlugin extends PluginCommunicationInterface {

	private static final List<String> mMethods = new ArrayList<String>();
	static {
		mMethods.add("getIpAddress");
		mMethods.add("getMacAddress");
		mMethods.add("getLinkSpeed");
		mMethods.add("getNetworkId");
		mMethods.add("getBSSID");
		mMethods.add("getSSID");
		mMethods.add("isHiddenSSID");
		mMethods.add("getRssi");
		mMethods.add("getDescribeContents");
		mMethods.add("scanning");
		mMethods.add("ping");
		mMethods.add("traceroute");
	}
	private static final List<String> mEvents = new ArrayList<String>();
	static{
		mEvents.add("empty event");
		mEvents.add("scanned networks");
		mEvents.add("ping");
		mEvents.add("traceroute");
	}
	
	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	
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
	public List<String> callMethodSync(long callId, String method, List<Object> parameters) throws AsyncMethodException {
		mWifiManager=(WifiManager)this.getContext().getSystemService(Context.WIFI_SERVICE);
		mWifiInfo=mWifiManager.getConnectionInfo();
		List<String> answer=new ArrayList<String>();
		if(method.equals("getIpAddress")){
			Log.e("WifiPlugin","2");
			answer.add(intToIP(mWifiInfo.getIpAddress()));
		}
		if(method.equals("getMacAddress")){
			answer.add(mWifiInfo.getMacAddress());
		}
		if(method.equals("getLinkSpeed")){
			answer.add(String.valueOf(mWifiInfo.getLinkSpeed())+" Mbps");
		}
		if(method.equals("getBSSID")){
			answer.add(mWifiInfo.getBSSID());
		}
		if(method.equals("getSSID")){
			answer.add(mWifiInfo.getSSID());
		}
		if(method.equals("getNetworkId")){
			answer.add(String.valueOf(mWifiInfo.getNetworkId()));
		}
		if(method.equals("getRssi")){
			answer.add(String.valueOf(mWifiInfo.getRssi()));
		}
		if(method.equals("isHiddenSSID")){
			answer.add(String.valueOf(mWifiInfo.getHiddenSSID()));
		}
		if(method.equals("getDescribeContents")){
			answer.add(String.valueOf(mWifiInfo.describeContents()));
		}
		if(method.equals("scanning")){
			if(parameters.get(0)!=null && parameters.get(1)!=null && parameters.get(2)!=null){				
				Map<String,String> extras = new HashMap<String, String>();
				
				extras.put("delay", (String)parameters.get(0));
				extras.put("periodicity", (String)parameters.get(1));
				extras.put("count", (String)parameters.get(2));
				
				callingServiceMethod(callId, WiFiPluginScanningService.class, extras);
			}
			else{
				Log.e("WiFiPlugin","Missing parameters for scanning!");
			}
		}
		if(method.equals("ping")){
			if(parameters.get(0)!=null && parameters.get(1)!=null){
				Map<String,String> extras = new HashMap<String, String>();
				
				extras.put("ip", (String)parameters.get(0));
				extras.put("count", (String)parameters.get(1));
				
				callingServiceMethod(callId, WiFiPluginPingService.class, extras);
			}
			else{
				Log.e("WiFiPlugin","Missing IP and count parameters for ping!");
			}
		}
		if(method.equals("traceroute")){
			if(parameters.get(0)!=null){
				Map<String,String> extras = new HashMap<String, String>();
				
				extras.put("ip", (String)parameters.get(0));
				
				callingServiceMethod(callId, WiFiPluginTracerouteService.class, extras);
			}
			else{
				Log.e("WiFiPlugin","Missing IP parameter for traceroute!");
			}
			
		}
		Log.e("WifiPlugin",answer.get(0));
		return answer;				
	}
	
	public void callingServiceMethod(long callId, Class<?> c, Map<String, String> extras) throws AsyncMethodException{
		
		Intent serviceIntent=new Intent(getContext(), c);
		serviceIntent.putExtra(Constants.INTENT_EXTRA_CALL_ID, String.valueOf(callId));
		for (Map.Entry<String, String> entry : extras.entrySet()) {
			serviceIntent.putExtra(entry.getKey(), entry.getValue());		    
		}
		getContext().startService(serviceIntent);
		throw new AsyncMethodException();
	}
	
	@Override
	public long callMethodAsync(String method, List<Object> parameters, PluginResultListener listener) {
		throw new UnsupportedOperationException("Can't call async method on plugin.");
	}

	@Override
	public void registerEventListener(String eventName, PluginEventListener listener) {
		throw new UnsupportedOperationException("You have to register the listener on PluginAdapter...");
	}
	
	public String intToIP(int addr) {
	    return  ((addr & 0xFF) + "." + 
	            ((addr >>>= 8) & 0xFF) + "." + 
	            ((addr >>>= 8) & 0xFF) + "." + 
	            ((addr >>>= 8) & 0xFF));
	}

	@Override
	public void unregisterEventListener(String eventName,
			PluginEventListener listener) {
		throw new UnsupportedOperationException("You have to register the listener on PluginAdapter.");
		
	}

	@Override
	public void unregisterEventListener(PluginEventListener listener) {
		throw new UnsupportedOperationException("You have to unregister the listener on PluginAdapter.");
	}

	@Override
	public void cancelCallsForListener(PluginResultListener listener) {
		throw new UnsupportedOperationException("Can't cancel a call here.");
	}

	@Override
	public List<Quota> getQuotas(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Quota getQuotaForMethod(String method){
		// TODO Auto-generated method stub
		return null;
	}
}
