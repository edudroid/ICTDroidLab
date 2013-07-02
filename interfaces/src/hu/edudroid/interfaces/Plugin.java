package hu.edudroid.interfaces;

import java.util.List;

public interface Plugin {

	String getAuthor();
	String getName();
	String getDescription();
	String getVersionCode();
	List<String> getAllEvents();
	List<String> getMethodNames();
	long callMethodAsync(String method, List<Object> parameters,PluginResultListener listener);
	List<String> callMethodSync(String method, List<Object> parameters);
	void registerEventListener(String eventName,PluginEventListener listener);

}
