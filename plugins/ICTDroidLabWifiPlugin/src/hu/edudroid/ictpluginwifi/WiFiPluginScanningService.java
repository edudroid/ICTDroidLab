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

public class WiFiPluginScanningService extends Service {
	
	private WifiManager mWifiManager;
	
	private Timer t;
	private TimerTask ttask;
	
	private Context mContext;
	
	private void reportResult(long callId, String methodName, String versionCode, String method, List<String> result) {
		Intent intent = new Intent(Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER);
		intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, callId);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, methodName);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, versionCode);
		intent.putExtra(Constants.INTENT_EXTRA_METHOD_NAME, method);
		intent.putStringArrayListExtra(Constants.INTENT_EXTRA_VALUE_RESULT, new ArrayList<String>(result));
		mContext.sendBroadcast(intent);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
		
		mContext=this.getApplicationContext();
		
		mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		
    	final int delay=Integer.parseInt(intent.getExtras().getString("delay"));
    	final int periodicity=Integer.parseInt(intent.getExtras().getString("periodicity"));
    	final int count=Integer.parseInt(intent.getExtras().getString("count"));
    	final long callId=Long.parseLong(intent.getExtras().getString(Constants.INTENT_EXTRA_CALL_ID));
    	
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
			            reportResult(callId, "WiFi Plugin", "v1.0", "scanning", res);
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
