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
	List<String> callMethodSync(long callId, String method, List<Object> parameters) throws AsyncMethodException;
	void registerEventListener(String eventName, PluginEventListener listener);
	void unregisterEventListener(String eventName, PluginEventListener listener);
	void unregisterEventListener(PluginEventListener listener);
	void cancelCallsForListener(PluginResultListener listener);
}
