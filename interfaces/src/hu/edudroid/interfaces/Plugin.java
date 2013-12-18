package hu.edudroid.interfaces;

import java.util.List;
import java.util.Map;

public interface Plugin {

	String getName();
	String getPackageName();
	String getReceiverClassName();
	String getAuthor();
	String getDescription();
	String getVersionCode();
	List<String> getAllEvents();
	List<String> getMethodNames();
	List<Quota> getQuotas();
	Quota getQuotaForMethod(String method);
	long callMethodAsync(String method, Map<String, Object> parameters,PluginResultListener listener);
	long callMethodAsync(String method, Map<String, Object> parameters,PluginResultListener listener, int quotaQuantity);

	/**
	 * 
	 * @param callId
	 * @param method
	 * @param parameters
	 * @return
	 * @throws AsyncMethodException If method is an async method, throw exception, and send intent when you're done.
	 */
	Map<String, Object> callMethodSync(long callId, String method, Map<String, Object> parameters, Object context) throws AsyncMethodException;
	/**
	 * 
	 * @param callId
	 * @param method
	 * @param parameters
	 * @param quotaQuantity
	 * @return
	 * @throws AsyncMethodException If method is an async method, throw exception, and send intent when you're done.
	 */
	Map<String, Object> callMethodSync(long callId, String method, Map<String, Object> parameters, int quotaQuantity, Object context) throws AsyncMethodException;
	void registerEventListener(String eventName, PluginEventListener listener);
	void unregisterEventListener(String eventName, PluginEventListener listener);
	void unregisterEventListener(PluginEventListener listener);
	void cancelCallsForListener(PluginResultListener listener);
	boolean validateQuota(Quota quota);
	void consumeQuota(int identifier, int quantity);
}
