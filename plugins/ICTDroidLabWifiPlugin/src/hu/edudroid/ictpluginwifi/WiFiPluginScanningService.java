package hu.edudroid.ictpluginwifi;
import hu.edudroid.ictplugin.PluginCommunicationInterface;
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

public class WiFiPluginScanningService extends Service {
	
	private WifiManager mWifiManager;
	public static final String PLUGIN_NAME="WiFi Plugin";
	public static final String SCAN_METHOD_NAME="scanned networks";
	public static final String VERSION_CODE="v1.0";
	
	private Timer t;
	private TimerTask ttask;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
		
		mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		
    	final int delay = Integer.parseInt(intent.getExtras().getString("delay"));
    	final int periodicity = Integer.parseInt(intent.getExtras().getString("periodicity"));
    	final int count = Integer.parseInt(intent.getExtras().getString("count"));
    	final long callId = Long.parseLong(intent.getExtras().getString(Constants.INTENT_EXTRA_CALL_ID));
    	
		try{
			BroadcastReceiver wifi_scan = new BroadcastReceiver()
	        {
				String wifiScanningResult;
			
				boolean scanned=false;
				
	            @Override
				public void onReceive(Context arg0, Intent arg1) {
	            	wifiScanningResult="";
	            	
	            	if(!scanned){
		            	List<WifiConfiguration> results = mWifiManager.getConfiguredNetworks();
			            List<ScanResult> scanResults = mWifiManager.getScanResults();
			            
			            for(int i=0;i<results.size();i++){
			    			
			            	wifiScanningResult+="CONFIGURED NETWORK:\n" +
			    					"BSSID: " + results.get(i).BSSID + "\n" +
			    					"Network ID: " + results.get(i).networkId + "\n" +
			    					"PreSharedKey: " + results.get(i).preSharedKey + "\n" +
			    					"Priority: " + results.get(i).priority + "\n" +
			    					"SSID: " + results.get(i).SSID + "\n" +
			    					"Status: " + wifiStatus(results.get(i).status) + "\n" +
			    					"WEP key: " + results.get(i).wepTxKeyIndex + "\n---\n";
			    		}
			            
			            for(int i=0;i<scanResults.size();i++){
			    			
			            	wifiScanningResult+="SCANNED NETWORKS:\n" + 
			    					"BSSID: " + scanResults.get(i).BSSID + "\n" +
			    					"Capabilities: " + scanResults.get(i).capabilities + "\n" +
			    					"Frekvency: " + scanResults.get(i).frequency + " MHz\n" +
			    					"Level: " + scanResults.get(i).level + " dBm\n" +
			    					"SSID: " + scanResults.get(i).SSID + "\n---\n";
			    		}
			    		
			            List<String> res=new ArrayList<String>();
			            res.add(wifiScanningResult);
		                PluginCommunicationInterface.reportResult(callId, Constants.INTENT_EXTRA_VALUE_RESULT, PLUGIN_NAME, VERSION_CODE, SCAN_METHOD_NAME, res, getApplicationContext());
	            	}
	            	scanned=true;
				}
	        };
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
	        registerReceiver(wifi_scan,filter);
	        
	        t=new Timer();
	    	ttask=new TimerTask() {
				int tickCount=count;
				@Override
				public void run() {
					if(tickCount==0){
						t.purge();
					}
					else{
						mWifiManager.startScan();
						tickCount--;
					}
				}
			};
	      	t.scheduleAtFixedRate(ttask, delay, periodicity);
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
}
