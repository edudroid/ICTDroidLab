package hu.edudroid.interfaces;

import java.util.List;

public interface Plugin {

	String getAuthor();
	String getName();
	String getDescription();
	String getVersionCode();
	List<String> getAllEvents();
	List<String> getMethodNames();
	List<Quota> getQuotas();
	Quota getQuotaForMethod(String method);
	long callMethodAsync(String method, List<Object> parameters,PluginResultListener listener);
	long callMethodAsync(String method, List<Object> parameters,PluginResultListener listener, int quotaQuantity);
	List<String> callMethodSync(long callId, String method, List<Object> parameters) throws AsyncMethodException;
	List<String> callMethodSync(long callId, String method, List<Object> parameters, int quotaQuantity) throws AsyncMethodException;
	void registerEventListener(String eventName, PluginEventListener listener);
	void unregisterEventListener(String eventName, PluginEventListener listener);
	void unregisterEventListener(PluginEventListener listener);
	void cancelCallsForListener(PluginResultListener listener);
	boolean validateQuota(Quota quota);
	void consumeQuota(int identifier, int quantity);
}
