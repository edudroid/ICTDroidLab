package hu.edudroid.ict;

import hu.edudroid.ict.plugins.AndroidPluginCollection;
import hu.edudroid.ict.plugins.PluginPollingBroadcast;
import hu.edudroid.module.ModuleLoader;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class CoreService extends Service {

	public static final String 		TAG 					= "CoreService";
	private final String			FILTER_PLUGIN_POLL		= "hu.edudroid.ict.plugin_polling_question";
	private final String			FILTER_PLUGIN_ANSWER	= "hu.edudroid.ict.plugin_polling_answer";
	
	private PluginPollingBroadcast	mBroadcast				= null;
	private AndroidPluginCollection mPluginCollection		= null;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
        Log.e("CORE SERVICE","Service has been started!");
        
        //Plugin -> PluginPollingBroadcast
        mBroadcast = PluginPollingBroadcast.getInstance();
		registerReceiver(mBroadcast, new IntentFilter(FILTER_PLUGIN_ANSWER));
        
		//PluginPollingBroadcast -> mPluginCollection
		mPluginCollection=AndroidPluginCollection.getInstance();
		mBroadcast.registerPluginDetailsListener(mPluginCollection);
		
        Intent mIntent = new Intent(FILTER_PLUGIN_POLL);
		mIntent.putExtra("action", "reportSelf");
		sendBroadcast(mIntent);
		
		ModuleLoader.runModule("none", "SampleModule.jar", this);
		
        Log.e("CORE SERVICE","Ready...");
        
    }

}
