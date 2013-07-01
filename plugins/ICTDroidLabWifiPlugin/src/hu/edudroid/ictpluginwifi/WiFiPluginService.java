package hu.edudroid.ictpluginwifi;
import java.util.ArrayList;
import java.util.List;

import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.PluginEventListener;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class WiFiPluginService extends Service implements PluginEventListener{
	private static long mEventID=0;
	private static WiFiPlugin mPlugin;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
		mPlugin=new WiFiPlugin();
		for(int i=0;i<mPlugin.getPlugin().getAllEvents().size();i++){
			mPlugin.getPlugin().registerEventListener(mPlugin.getPlugin().getAllEvents().get(i), this);
		}
	}

	@Override
	public void onEvent(long id, String plugin, String version, String eventName, List<String> result) {
		Intent intent = new Intent(Constants.INTENT_ACTION_PLUGIN_EVENT);
		intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, id);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, mPlugin.getPlugin().getName());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, mPlugin.getPlugin().getVersionCode());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_EVENT_NAME, eventName);
		intent.putStringArrayListExtra(Constants.INTENT_EXTRA_VALUE_RESULT, new ArrayList<String>(result));
		this.sendBroadcast(intent);
		
	}

}
