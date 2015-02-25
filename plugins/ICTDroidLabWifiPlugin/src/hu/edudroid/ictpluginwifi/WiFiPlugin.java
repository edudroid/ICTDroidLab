package hu.edudroid.ictpluginwifi;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.MethodNotSupportedException;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResult;
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
	
	public WiFiPlugin() {
		super(null);
	}

	public static final String VALUE_KEY = "value";
	
	public static final String METHOD_NAME_GET_STATE = "getState";
	public static final String METHOD_NAME_SCAN = "scan";
	public static final String METHOD_NAME_PING = "ping";
	public static final String METHOD_NAME_TRACEROUTE = "traceroute";

	public static final String IP_ADDRESS = "ip address";
	public static final String MAC_ADDRESS = "mac address";
	public static final String LINK_SPEED = "link speed";
	public static final String NETWORK_ID = "network id";
	public static final String BSSID = "BSSID";
	public static final String SSID = "SSID";
	public static final String HIDDEN_SSID = "hidden SSID";
	public static final String RSSI = "RSSI";
	public static final String CONTENT_DESCRIPTION = "content description";

	private static final List<String> mMethods = new ArrayList<String>();
	static {
		mMethods.add(METHOD_NAME_GET_STATE);
		mMethods.add(METHOD_NAME_SCAN);
		mMethods.add(METHOD_NAME_PING);
		mMethods.add(METHOD_NAME_TRACEROUTE);
	}
	private static final List<String> mEvents = new ArrayList<String>();
	
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
	public String getPackageName() {
		return WiFiPlugin.class.getPackage().getName();
	}

	@Override
	public String getReceiverClassName() {
		return WiFiPlugin.class.getName();
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
	public PluginResult callMethodSync(long callId, String method, Map<String, Object> parameters, Map<Long, Double> quotaQuantity, Object context) throws AsyncMethodException, MethodNotSupportedException {
		mWifiManager=(WifiManager)((Context)context).getSystemService(Context.WIFI_SERVICE);
		mWifiInfo=mWifiManager.getConnectionInfo();
		if (method.equals(METHOD_NAME_GET_STATE)) {
			Map<String, Object> answer = new HashMap<String, Object>();
			answer.put(IP_ADDRESS, intToIP(mWifiInfo.getIpAddress()));
			answer.put(MAC_ADDRESS, mWifiInfo.getMacAddress());
			answer.put(LINK_SPEED, String.valueOf(mWifiInfo.getLinkSpeed())+" Mbps");
			answer.put(BSSID, mWifiInfo.getBSSID());
			answer.put(SSID, mWifiInfo.getSSID());
			answer.put(NETWORK_ID, String.valueOf(mWifiInfo.getNetworkId()));
			answer.put(RSSI, String.valueOf(mWifiInfo.getRssi()));
			answer.put(HIDDEN_SSID, String.valueOf(mWifiInfo.getHiddenSSID()));
			answer.put(CONTENT_DESCRIPTION, String.valueOf(mWifiInfo.describeContents()));
			return new PluginResult(answer, null);				
		}
		else if(method.equals("scanning")){
			if(parameters.get(0)!=null && parameters.get(1)!=null && parameters.get(2)!=null){				
				Map<String,String> extras = new HashMap<String, String>();
				
				extras.put("delay", (String)parameters.get(0));
				extras.put("periodicity", (String)parameters.get(1));
				extras.put("count", (String)parameters.get(2));
				
				callingServiceMethod(callId, WiFiPluginScanningService.class, extras, (Context)context);
				throw new AsyncMethodException();
			}
			else{
				Log.e("WiFiPlugin","Missing parameters for scanning!");
				throw new IllegalArgumentException("Missing parameters for scanning!");
			}
		}
		else if(method.equals("ping")){
			if(parameters.get(0)!=null && parameters.get(1)!=null){
				Map<String,String> extras = new HashMap<String, String>();
				
				extras.put("ip", (String)parameters.get(0));
				extras.put("count", (String)parameters.get(1));
				
				callingServiceMethod(callId, WiFiPluginPingService.class, extras, (Context)context);
				throw new AsyncMethodException();
			}
			else{
				Log.e("WiFiPlugin","Missing IP and count parameters for ping!");
				throw new IllegalArgumentException("Missing parameters for ping!");
			}
		}
		else if(method.equals("traceroute")){
			if(parameters.get(0)!=null){
				Map<String,String> extras = new HashMap<String, String>();
				
				extras.put("ip", (String)parameters.get(0));
				
				callingServiceMethod(callId, WiFiPluginTracerouteService.class, extras, (Context)context);
				throw new AsyncMethodException();
			}
			else{
				Log.e("WiFiPlugin","Missing IP parameter for traceroute!");
				throw new IllegalArgumentException("Missing parameters for traceroute!");
			}
		} else {
			throw new MethodNotSupportedException("Missing parameters for traceroute!");
		}
	}
	
	public void callingServiceMethod(long callId, Class<?> c, Map<String, String> extras, Context context){
		
		Intent serviceIntent=new Intent(context, c);
		serviceIntent.putExtra(Constants.INTENT_EXTRA_CALL_ID, String.valueOf(callId));
		for (Map.Entry<String, String> entry : extras.entrySet()) {
			serviceIntent.putExtra(entry.getKey(), entry.getValue());		    
		}
		context.startService(serviceIntent);
	}
	
	@Override
	public long callMethodAsync(String method, Map<String, Object> parameters, PluginResultListener listener, Map<Long, Double> quotaLimits) {
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
		return null;
	}

	@Override
	public Map<Long, Double> getCostOfMethod(String method,
			Map<String, Object> parameters) {
		// TODO Determine quota consumption
		return null;
	}
}
