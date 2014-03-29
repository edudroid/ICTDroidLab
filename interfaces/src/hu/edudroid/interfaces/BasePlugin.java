package hu.edudroid.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasePlugin implements Plugin {

	private final String name;
	private final String packageName;
	private final String receiverClassName;
	private final String author;
	private final String description;
	private final String versionCode;
	private final List<String> allEvents;
	private final List<String> methodNames;
	private final List<Quota> quotas;
	
	public BasePlugin(String name, String packageName, String receiverClass, String author, String description, String versionCode, List<String> allEvents, List<String>methodNames, List<Quota> quotas) {
		this.name = name;
		this.packageName = packageName;
		this.receiverClassName = receiverClass;
		this.author = author;
		this.description = description;
		this.versionCode = versionCode;
		if (allEvents == null) {
			allEvents = new ArrayList<String>();
		}
		this.allEvents = allEvents;
		if (methodNames == null) {
			methodNames = new ArrayList<String>();
		}
		this.methodNames = methodNames;
		if (quotas == null) {
			quotas = new ArrayList<Quota>();
		}
		this.quotas = quotas;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

	@Override
	public String getReceiverClassName() {
		return receiverClassName;
	}

	@Override
	public String getAuthor() {
		return author;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getVersionCode() {
		return versionCode;
	}

	@Override
	public List<String> getAllEvents() {
		return allEvents;
	}

	@Override
	public List<String> getMethodNames() {
		return methodNames;
	}

	@Override
	public List<Quota> getQuotas() {
		return quotas;
	}

	@Override
	public long callMethodAsync(String method, Map<String, Object> parameters,
			PluginResultListener listener) {
		throw new UnsupportedOperationException("No listeners available on real plugins.");
	}

	@Override
	public long callMethodAsync(String method, Map<String, Object> parameters,
			PluginResultListener listener, Map<Long, Double> quotaLimits) {
		throw new UnsupportedOperationException("No listeners available on real plugins.");
	}

	@Override
	public void registerEventListener(String eventName,
			PluginEventListener listener) {
		throw new UnsupportedOperationException("No listeners available on real plugins.");
	}

	@Override
	public void unregisterEventListener(String eventName,
			PluginEventListener listener) {
		throw new UnsupportedOperationException("No listeners available on real plugins.");
	}

	@Override
	public void unregisterEventListener(PluginEventListener listener) {
		throw new UnsupportedOperationException("No listeners available on real plugins.");
	}

	@Override
	public void cancelCallsForListener(PluginResultListener listener) {
		throw new UnsupportedOperationException("No listeners available on real plugins.");
	}

	@Override
	public Map<Long, Double> getCostOfMethod(String method,
			Map<String, Object> parameters) {
		throw new UnsupportedOperationException("Don't use it on a data plugin.");
	}
	
	@Override
	public PluginResult callMethodSync(long callId, String method,
			Map<String, Object> parameters, Map<Long, Double> quotaLimits,
			Object context) throws AsyncMethodException,
			MethodNotSupportedException {
		throw new AsyncMethodException();
	}
}