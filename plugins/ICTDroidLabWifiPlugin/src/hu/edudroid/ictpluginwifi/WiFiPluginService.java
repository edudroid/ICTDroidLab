package hu.edudroid.ictpluginwifi;
import hu.edudroid.interfaces.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.Toast;

public class WiFiPluginService extends Service {
	private static long mEventID=0;
	
	private WifiManager mWifiManager;
	private String resultMessage;
	
	private Timer t;
	private TimerTask ttask;
	
	public void onEvent(String eventName, List<String> params) {
		List<String> result = new ArrayList<String>();
		if(eventName.equals("empty event")){
			result.add("This ");
			result.add("is ");
			result.add("an ");
			result.add("empty ");
			result.add("event");
		}
		if(eventName.equals("scanned networks")){
			result.add(resultMessage);
		}
		
		Intent intent = new Intent(Constants.INTENT_ACTION_PLUGIN_EVENT);
		intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, mEventID++);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, "WiFi Plugin");
		intent.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, "v1.0");
		intent.putExtra(Constants.INTENT_EXTRA_KEY_EVENT_NAME, eventName);
		intent.putStringArrayListExtra(Constants.INTENT_EXTRA_VALUE_RESULT, new ArrayList<String>(result));
		this.sendBroadcast(intent);
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
		onEvent("empty event", null);
		
		mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		
		try{
			BroadcastReceiver wifi_scan = new BroadcastReceiver()
	        {
	            @Override
				public void onReceive(Context arg0, Intent arg1) {
					resultMessage="";
	            	List<WifiConfiguration> results = mWifiManager.getConfiguredNetworks();
		            List<ScanResult> scanResults = mWifiManager.getScanResults();
		            
		            
		            for(int i=0;i<results.size();i++){
		    			
		            	resultMessage+="CONFIGURED NETWORK:\n" +
		    					"BSSID: " + results.get(i).BSSID + "\n" +
		    					"Network ID: " + results.get(i).networkId + "\n" +
		    					"PreSharedKey: " + results.get(i).preSharedKey + "\n" +
		    					"Priority: " + results.get(i).priority + "\n" +
		    					"SSID: " + results.get(i).SSID + "\n" +
		    					"Status: " + wifiStatus(results.get(i).status) + "\n" +
		    					"WEP key: " + results.get(i).wepTxKeyIndex + "\n---\n";
		    		}
		            
		            for(int i=0;i<scanResults.size();i++){
		    			
		            	resultMessage+="SCANNED NETWORKS:\n" + 
		    					"BSSID: " + scanResults.get(i).BSSID + "\n" +
		    					"Capabilities: " + scanResults.get(i).capabilities + "\n" +
		    					"Frekvency: " + scanResults.get(i).frequency + " MHz\n" +
		    					"Level: " + scanResults.get(i).level + " dBm\n" +
		    					"SSID: " + scanResults.get(i).SSID + "\n---\n";
		    		}
		            onEvent("scanned networks", null);
		            
				}
	        };
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
	        registerReceiver(wifi_scan,filter);
	        
	        
	        t=new Timer();
	    	ttask=new myTimerTask();
	      	t.scheduleAtFixedRate(ttask, 0, 1000);
		}catch(Exception e){
			Toast.makeText(this, "Hiba a betöltés során!", Toast.LENGTH_LONG).show();
		}
	}
	
	String wifiStatus(int i){
		switch(i){
		case 0:
			return "CURRENT";
		case 1:
			return "DISABLED";
		case 2:
			return "ENABLED";
		}
		return "none";
	}
	
class myTimerTask extends TimerTask {
				
		@Override
		public void run() {
			mWifiManager.startScan();
		}
	};
		
		
	
}
