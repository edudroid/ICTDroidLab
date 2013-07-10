package hu.edudroid.ictpluginwifi;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WiFiPluginPingService extends Service {

	public static final String PLUGIN_NAME="WiFi Plugin";
	public static final String PING_METHOD_NAME="ping";
	public static final String VERSION_CODE="v1.0";
	
	public class LogStreamReader implements Runnable {

        private BufferedReader reader;
        private int lineCount;
        private long callId;

        public LogStreamReader(long callId, InputStream is, int lineCount) {
            this.reader = new BufferedReader(new InputStreamReader(is));
            this.lineCount = lineCount;
            this.callId = callId;
        }

        public void run() {
            try {
                String line = reader.readLine();
                List<String> res = new ArrayList<String>();
                res.add(String.valueOf(callId));
                while (lineCount>-1 && line != null) {
                    line = reader.readLine();
                    if(line!=null){
                    	res.add(line+"\n");
                    }
                    lineCount--;
                }
                PluginCommunicationInterface.reportResult(callId, Constants.INTENT_EXTRA_VALUE_RESULT, PLUGIN_NAME, VERSION_CODE, PING_METHOD_NAME, res, getApplicationContext());
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	@Override
    public void onStart(Intent intent, int startId) {
		
		final String ip=intent.getExtras().getString("ip");
		final int pcount=Integer.parseInt(intent.getExtras().getString("count"));
		final long callId=Long.parseLong(intent.getExtras().getString(Constants.INTENT_EXTRA_CALL_ID));
		
		List<String> commandLine=new ArrayList<String>();
		commandLine.add("ping");
        commandLine.add(ip);
        Process mProcess=null;
        try {
        	mProcess = Runtime.getRuntime().exec(commandLine.toArray(new String[0]));
            	
            LogStreamReader lsr = new LogStreamReader(callId, mProcess.getInputStream(),pcount);
            Thread thread = new Thread(lsr, "LogStreamReader");
            thread.start();
        } catch(Exception e){
        	e.printStackTrace();
        }

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
