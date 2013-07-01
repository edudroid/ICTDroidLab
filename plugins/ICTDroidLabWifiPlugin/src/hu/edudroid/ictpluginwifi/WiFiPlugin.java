package hu.edudroid.ictpluginwifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.widget.Toast;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResultListener;

public class WiFiPlugin extends PluginCommunicationInterface {

	private static final List<String> mMethods=new ArrayList<String>();
	private static final List<String> mEvents=new ArrayList<String>();
	private static Plugin mPlugin;
	private static long mEventID;
	
	@Override
	protected Plugin getPlugin() {
		mPlugin=new Plugin() {
			
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
				mMethods.add("showIPAddress");
				mMethods.add("showMACAddress");
				mMethods.add("showNetMaskAddress");
				mMethods.add("showNetworkSpeed");
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
				mEvents.add("empty event");
				return mEvents;
			}
			
			@Override
			public List<String> callMethodSync(String method, List<Object> parameters) {
				List<String> answer=new ArrayList<String>();
				if(method.equals("showIPAddress")){
					answer.add("192.168.1.1");
					//onEvent("empty event", null);
				}
				if(method.equals("showMACAddress")){
					answer.add("AC:00:FF:12:A4:34");
				}
				if(method.equals("showNetMaskAddress")){
					answer.add("255.255.255.0");
				}
				if(method.equals("showNetworkSpeed")){
					answer.add("54 Mbps");
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
				// TODO Auto-generated method stub
				
			}
		};
		return mPlugin;
	}

	@Override
	protected void onEvent(String eventName, List<String> eventParams) {
		Intent intent = new Intent(Constants.INTENT_ACTION_PLUGIN_CALLMETHOD_ANSWER);
		intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, mEventID++);
		intent.putExtra(Constants.INTENT_EXTRA_KEY_PLUGIN_ID, mPlugin.getName());
		intent.putExtra(Constants.INTENT_EXTRA_KEY_VERSION, mPlugin.getVersionCode());
		
		if(eventName.equals("empty event")){
			
			intent.putExtra(Constants.INTENT_EXTRA_KEY_EVENT_NAME, eventName);
			intent.putStringArrayListExtra(Constants.INTENT_EXTRA_VALUE_EVENT, new ArrayList<String>(eventParams));
			//context.sendBroadcast(intent);
		}
		
	}

}
