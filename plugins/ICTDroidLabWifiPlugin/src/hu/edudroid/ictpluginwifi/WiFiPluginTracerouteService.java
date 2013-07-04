package hu.edudroid.ictpluginwifi;

import hu.edudroid.interfaces.Constants;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WiFiPluginTracerouteService extends Service {

	private static long mEventID=0;
	
	public void onEvent(String eventName, List<String> params) {
		List<String> result = new ArrayList<String>();
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
    public void onStart(Intent intent, int startId) {
		
		final String ip=intent.getExtras().getString("ip");
		
		List<String> commandLine=new ArrayList<String>();
		commandLine.add("su");
		commandLine.add("-c");
		commandLine.add("traceroute");
        commandLine.add(ip);
        
        PipedInputStream mPIn;
        LineNumberReader mReader;
        Process mProcess=null;
        try {
        	mProcess = Runtime.getRuntime().exec(commandLine.toArray(new String[0]));
        	InputStream in = mProcess.getInputStream();
            OutputStream out = mProcess.getOutputStream();
            byte[] buffer = new byte[1024];
            int count;
            
            PipedOutputStream mPOut = new PipedOutputStream();
            mPIn = new PipedInputStream(mPOut);
            mReader = new LineNumberReader(new InputStreamReader(mPIn));
            // in -> buffer -> mPOut -> mReader -> 1 line of ping information to parse
            List<String> res=new ArrayList<String>();
            while (((count = in.read(buffer)) != -1)) {
                mPOut.write(buffer, 0, count);
                while (mReader.ready()) {
                    String text=mReader.readLine();
                    res.add(text);
                    Log.e("Traceroute",text);
                }
            }
            onEvent("traceroute", res);
            out.close();
            in.close();
            mPOut.close();
            mPIn.close();
            
        } 
        catch(Exception e){
        	e.printStackTrace();
        }
        finally {
            if(mProcess!=null){
            	mProcess.destroy();
            	mProcess = null;
            }
        }

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}