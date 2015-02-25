package hu.edudroid.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Interface that provides description of the plugin and makes it's methods available to both the communication interface both the plugin collection.
 * @author lajthabalazs
 *
 */
public interface Plugin {

	String getName();
	String getPackageName();
	/**
	 * Enables targeted messages to the receiver component. This means that only a single broadcast receiver class can handle the internal intents.
	 * @return The fully qualified class name of the broadcast receiver.
	 */
	String getReceiverClassName();
	String getAuthor();
	String getDescription();
	String getVersionCode();
	List<String> getAllEvents();
	List<String> getMethodNames();
	List<Quota> getQuotas();
	Map<Long, Double> getCostOfMethod(String method, Map<String, Object> parameters);
	long callMethodAsync(String method, Map<String, Object> parameters,PluginResultListener listener);
	long callMethodAsync(String method, Map<String, Object> parameters,PluginResultListener listener, Map<Long, Double> quotaLimits);

	/**
	 * 
	 * @param callId
	 * @param method
	 * @param parameters
	 * @param quotaQuantity
	 * @return
	 * @throws AsyncMethodException If method is an async method, throw exception, and send intent when you're done.
	 * @throws MethodNotSupportedException 
	 */
	PluginResult callMethodSync(long callId, String method, Map<String, Object> parameters, Map<Long, Double> quotaLimits, Object context) throws AsyncMethodException, MethodNotSupportedException;
	void registerEventListener(String eventName, PluginEventListener listener);
	void unregisterEventListener(String eventName, PluginEventListener listener);
	void unregisterEventListener(PluginEventListener listener);
	void cancelCallsForListener(PluginResultListener listener);
}