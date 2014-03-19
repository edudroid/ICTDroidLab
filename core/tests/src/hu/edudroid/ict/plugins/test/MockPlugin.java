package hu.edudroid.ict.plugins.test;

import java.util.List;
import java.util.Map;

import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.MethodNotSupportedException;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResult;
import hu.edudroid.interfaces.PluginResultListener;
import hu.edudroid.interfaces.Quota;

public class MockPlugin implements Plugin {

	private final String name;
	private final String packageName;

	public MockPlugin(String name, String packageName) {
		this.name = name;
		this.packageName = packageName;
	}

	@Override
	public String getName() { return name; }

	@Override
	public String getPackageName() { return packageName; }

	@Override
	public String getReceiverClassName() { return null; }

	@Override
	public String getAuthor() { return null; }

	@Override
	public String getDescription() { return null; }

	@Override
	public String getVersionCode() { return null; }

	@Override
	public List<String> getAllEvents() { return null; }

	@Override
	public List<String> getMethodNames() { return null; }

	@Override
	public List<Quota> getQuotas() { return null; }

	@Override
	public Map<Long, Double> getCostOfMethod(String method, Map<String, Object> parameters) { return null; }

	@Override
	public long callMethodAsync(String method, Map<String, Object> parameters, PluginResultListener listener) { return 0; }

	@Override
	public long callMethodAsync(String method, Map<String, Object> parameters, PluginResultListener listener, Map<Long, Double> quotaLimits) { return 0; }

	@Override
	public PluginResult callMethodSync(long callId, String method, Map<String, Object> parameters, Map<Long, Double> quotaLimits, Object context) throws AsyncMethodException, MethodNotSupportedException { return null; }

	@Override
	public void registerEventListener(String eventName, PluginEventListener listener) { }

	@Override
	public void unregisterEventListener(String eventName, PluginEventListener listener) { }

	@Override
	public void unregisterEventListener(PluginEventListener listener) {}

	@Override
	public void cancelCallsForListener(PluginResultListener listener) {}

}
