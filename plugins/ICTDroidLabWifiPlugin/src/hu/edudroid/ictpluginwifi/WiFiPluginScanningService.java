package hu.edudroid.ictpluginwifi;
import hu.edudroid.interfaces.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
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
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

public class WiFiPluginScanningService extends Service {
	private static long mEventID=0;
	
	private WifiManager mWifiManager;
	
	TracerouteTask mTracerouteTask;
	
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
			result.addAll(params);
		}
		if(eventName.equals("ping")){
			result.addAll(params);
		}
		if(eventName.equals("traceroute")){
			result.addAll(params);
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
		
		mWifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		
    	final int delay=intent.getExtras().getInt("delay");
    	final int periodicity=intent.getExtras().getInt("periodicity");
    	final int count=intent.getExtras().getInt("count");
    	final long callId=intent.getExtras().getLong("callId");
    	
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
			            res.add(String.valueOf(callId));
			            res.add(wifiScanningResult);
			            onEvent("scanned networks", res);
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
	
	class TracerouteTask extends AsyncTask<String, Void, Void> {
        PipedOutputStream mPOut;
        PipedInputStream mPIn;
        LineNumberReader mReader;
        Process mProcess;
        @Override
        protected void onPreExecute() {
            mPOut = new PipedOutputStream();
            try {
                mPIn = new PipedInputStream(mPOut);
                mReader = new LineNumberReader(new InputStreamReader(mPIn));
            } catch (IOException e) {
                cancel(true);
            }

        }

        public void stop() {
            Process p = mProcess;
            if (p != null) {
                p.destroy();
            }
            cancel(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
            	ArrayList<String> commandLine = new ArrayList<String>();
                commandLine.add("su");
                commandLine.add("-c");
                commandLine.add("traceroute");
                commandLine.add(params[0]);

                mProcess = Runtime.getRuntime().exec(commandLine.toArray(new String[0]));

                try {
                    InputStream in = mProcess.getInputStream();
                    OutputStream out = mProcess.getOutputStream();
                    byte[] buffer = new byte[1024];
                    int count;

                    // in -> buffer -> mPOut -> mReader -> 1 line of ping information to parse
                    while ((count = in.read(buffer)) != -1) {
                        mPOut.write(buffer, 0, count);
                        publishProgress();
                    }
                    out.close();
                    in.close();
                    mPOut.close();
                    mPIn.close();
                } finally {
                    mProcess.destroy();
                    mProcess = null;
                }
            } catch (IOException e) {
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            try {
                // Is a line ready to read from the "traceroute" command?
                while (mReader.ready()) {
                    List<String> res=new ArrayList<String>();
                    String text=mReader.readLine();
                    res.add(text);
                	onEvent("traceroute", res);
                }
            } catch (IOException t) {
            	t.printStackTrace();
            }
        }
    }
		
		
	
}
