package hu.edudroid.ictpluginwifi;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.Constants;
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
	
	public static final String VALUE_KEY = "value";

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
	public PluginResult callMethodSync(long callId, String method, Map<String, Object> parameters, Map<Long, Double> quotaQuantity, Object context) throws AsyncMethodException {
		mWifiManager=(WifiManager)((Context)context).getSystemService(Context.WIFI_SERVICE);
		mWifiInfo=mWifiManager.getConnectionInfo();
		Map<String, Object> answer = new HashMap<String, Object>();
		if(method.equals("getIpAddress")){
			answer.put(VALUE_KEY, intToIP(mWifiInfo.getIpAddress()));
		}
		if(method.equals("getMacAddress")){
			answer.put(VALUE_KEY, mWifiInfo.getMacAddress());
		}
		if(method.equals("getLinkSpeed")){
			answer.put(VALUE_KEY, String.valueOf(mWifiInfo.getLinkSpeed())+" Mbps");
		}
		if(method.equals("getBSSID")){
			answer.put(VALUE_KEY, mWifiInfo.getBSSID());
		}
		if(method.equals("getSSID")){
			answer.put(VALUE_KEY, mWifiInfo.getSSID());
		}
		if(method.equals("getNetworkId")){
			answer.put(VALUE_KEY, String.valueOf(mWifiInfo.getNetworkId()));
		}
		if(method.equals("getRssi")){
			answer.put(VALUE_KEY, String.valueOf(mWifiInfo.getRssi()));
		}
		if(method.equals("isHiddenSSID")){
			answer.put(VALUE_KEY, String.valueOf(mWifiInfo.getHiddenSSID()));
		}
		if(method.equals("getDescribeContents")){
			answer.put(VALUE_KEY, String.valueOf(mWifiInfo.describeContents()));
		}
		if(method.equals("scanning")){
			if(parameters.get(0)!=null && parameters.get(1)!=null && parameters.get(2)!=null){				
				Map<String,String> extras = new HashMap<String, String>();
				
				extras.put("delay", (String)parameters.get(0));
				extras.put("periodicity", (String)parameters.get(1));
				extras.put("count", (String)parameters.get(2));
				
				callingServiceMethod(callId, WiFiPluginScanningService.class, extras, (Context)context);
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
				
				callingServiceMethod(callId, WiFiPluginPingService.class, extras, (Context)context);
			}
			else{
				Log.e("WiFiPlugin","Missing IP and count parameters for ping!");
			}
		}
		if(method.equals("traceroute")){
			if(parameters.get(0)!=null){
				Map<String,String> extras = new HashMap<String, String>();
				
				extras.put("ip", (String)parameters.get(0));
				
				callingServiceMethod(callId, WiFiPluginTracerouteService.class, extras, (Context)context);
			}
			else{
				Log.e("WiFiPlugin","Missing IP parameter for traceroute!");
			}
			
		}
		Log.e("WifiPlugin",answer.get(VALUE_KEY).toString());
		return new PluginResult(answer, null); // TODO add consumed quota				
	}
	
	public void callingServiceMethod(long callId, Class<?> c, Map<String, String> extras, Context context) throws AsyncMethodException{
		
		Intent serviceIntent=new Intent(context, c);
		serviceIntent.putExtra(Constants.INTENT_EXTRA_CALL_ID, String.valueOf(callId));
		for (Map.Entry<String, String> entry : extras.entrySet()) {
			serviceIntent.putExtra(entry.getKey(), entry.getValue());		    
		}
		context.startService(serviceIntent);
		throw new AsyncMethodException();
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
