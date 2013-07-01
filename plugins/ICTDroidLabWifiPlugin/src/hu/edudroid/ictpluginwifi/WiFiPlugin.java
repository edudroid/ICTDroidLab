package hu.edudroid.ictpluginwifi;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResultListener;

public class WiFiPlugin extends PluginCommunicationInterface {

	private static final List<String> methods=new ArrayList<String>();
	private static final List<String> events=new ArrayList<String>();
	
	@Override
	protected Plugin getPlugin() {
		return new Plugin() {
			
			@Override
			public void registerEventListener(String eventName,
					PluginEventListener listener) {
				// TODO Auto-generated method stub
				
			}
			
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
				methods.add("showIPAddress");
				methods.add("showMACAddress");
				methods.add("showNetMaskAddress");
				methods.add("showNetworkSpeed");
				return methods;
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
				events.add("empty event");
				return events;
			}
			
			@Override
			public List<String> callMethodSync(String method, List<Object> parameters) {
				List<String> answer=new ArrayList<String>();
				if(method.equals("showIPAddress")){
					answer.add("192.168.1.1");
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
		};
	}

}
