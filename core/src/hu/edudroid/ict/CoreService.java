package hu.edudroid.ict;

import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.ict.plugins.PluginPollingBroadcast;
import hu.edudroid.interfaces.Constants;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class CoreService extends Service {

	public static final String 		TAG 					= "CoreService";
	
	private PluginPollingBroadcast	mBroadcast				= null;
	private AndroidPluginCollection mPluginCollection		= null;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
    public void onStart(Intent intent, int startId) {        
        //Plugin -> PluginPollingBroadcast
        
		mBroadcast = PluginPollingBroadcast.getInstance();
		registerReceiver(mBroadcast, new IntentFilter(Constants.INTENT_ACTION_DESCRIBE));
		registerReceiver(mBroadcast, new IntentFilter(Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER));
		registerReceiver(mBroadcast, new IntentFilter(Constants.INTENT_ACTION_PLUGIN_EVENT));
        
		//PluginPollingBroadcast -> mPluginCollection
		mPluginCollection=AndroidPluginCollection.getInstance();
		mBroadcast.registerPluginDetailsListener(mPluginCollection);
		
        Intent mIntent = new Intent(Constants.INTENT_ACTION_PLUGIN_POLL);
		sendBroadcast(mIntent);        
    }

}
