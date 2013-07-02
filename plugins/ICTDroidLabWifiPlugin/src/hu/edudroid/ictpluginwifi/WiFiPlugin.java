package hu.edudroid.ictpluginwifi;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WiFiPlugin extends PluginCommunicationInterface implements Plugin {

	private static final List<String> mMethods=new ArrayList<String>();
	private static final List<String> mEvents=new ArrayList<String>();
	private static final Map<String,PluginEventListener> mEventListeners=new HashMap<String,PluginEventListener>();
	
	private static long mEventID=0;	
	
	private Context mContext;
	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	
	public WiFiPlugin(){
		
		mMethods.add("getIpAddress");
		mMethods.add("getMacAddress");
		mMethods.add("getLinkSpeed");
		mMethods.add("getNetworkId");
		mMethods.add("getBSSID");
		mMethods.add("getSSID");
		mMethods.add("isHiddenSSID");
		mMethods.add("getRssi");
		mMethods.add("getDescribeContents");
		
		
		mEvents.add("empty event");
		mEvents.add("scanned networks");
		
		
	
	}
	
	@Override
	protected Plugin getPlugin(Context context) {
		this.mContext=context;
		mWifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);	
			
		return this;
	}

	@Override
	protected void onEvent(String eventName, List<String> params) throws UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}

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
		mWifiInfo=mWifiManager.getConnectionInfo();
		if(method.equals("getIpAddress")){
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
	
	public String intToIP(int addr) {
	    return  ((addr & 0xFF) + "." + 
	            ((addr >>>= 8) & 0xFF) + "." + 
	            ((addr >>>= 8) & 0xFF) + "." + 
	            ((addr >>>= 8) & 0xFF));
	}
	
}
