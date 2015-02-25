package hu.edudroid.ict.plugins;

import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResult;
import hu.edudroid.interfaces.PluginResultListener;
import hu.edudroid.interfaces.Quota;
import hu.edudroid.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PluginAdapter implements Plugin, PluginResultListener, PluginEventListener {

	private static final String TAG = PluginAdapter.class.getName();
	private final String name;
	private final String packageName;
	private final String receiverClassName;
	private final String author;
	private final String description;
	private final String versionCode;
	private final PluginIntentReceiver pluginIntentReceiver;

	private final Context context;
	private final List<String> pluginMethods;
	private final List<String> events;


	private final Map<Long, PluginResultListener> mCallBackIdentification = new HashMap<Long, PluginResultListener>();
	private final Map<String,HashSet<PluginEventListener>> mEventListeners = new HashMap<String,HashSet<PluginEventListener>>();

	private static long mCallMethodID = 0;

	public PluginAdapter(final Plugin plugin, final PluginIntentReceiver pluginIntentReceiver, final Context context) {
		this.name = plugin.getName();
		this.packageName = plugin.getPackageName();
		this.receiverClassName = plugin.getReceiverClassName();
		this.author = plugin.getAuthor();
		this.description = plugin.getDescription();
		this.versionCode = plugin.getVersionCode();
		this.pluginMethods = plugin.getMethodNames();
		this.events = plugin.getAllEvents();

		this.pluginIntentReceiver = pluginIntentReceiver;
		this.context = context;

		pluginIntentReceiver.registerEventListener(this);
	    pluginIntentReceiver.registerResultListener(this);
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
	};

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
	public List<String> getMethodNames(){
		return pluginMethods;
	}

	@Override
	public List<Quota> getQuotas(){
		final ArrayList<Quota> quotas = new ArrayList<Quota>();
		// TODO retrieve quotas
		return quotas;
	}

	@Override
	public List<String> getAllEvents() {
		return events;
	}

	@Override
	public long callMethodAsync(String method, Map<String, Object> params, PluginResultListener listener){
		return callMethodAsync(method, params, listener, null);
	}

	@Override
	public long callMethodAsync(String method, Map<String, Object> params, PluginResultListener listener, Map<Long, Double> quotaLimits){

		pluginIntentReceiver.registerResultListener(this);

		mCallBackIdentification.put(mCallMethodID, listener);

		// TODO check quota limits
		// TODO if no limits are set, retrieve max limit from system

		Intent intent = new Intent(Constants.INTENT_ACTION_CALL_METHOD);
		intent.setComponent(new ComponentName(packageName, receiverClassName));
		intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, mCallMethodID);
		intent.putExtra(Constants.INTENT_EXTRA_METHOD_NAME, method);
		byte[] paramBytes = Utils.mapToByteArray(params);
		if (paramBytes != null) {
			intent.putExtra(Constants.INTENT_EXTRA_METHOD_PARAMETERS, paramBytes);
		}
		context.sendBroadcast(intent);

		return mCallMethodID++;
	}

	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, Map<String, Object> result) {
		if (plugin.equals(name)) {
			Log.d(TAG, "Received result from " + plugin + " for the callId " + id);
			try{
				mCallBackIdentification.remove(id).onResult(id, plugin, pluginVersion, methodName, result);
			} catch(NullPointerException e){
				Log.e(TAG, "Call id " + id + " doesn't exist.");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onError(long id, String plugin, String pluginVersion, String methodName,
			String errorMessage) {
	}

	@Override
	public PluginResult callMethodSync(long callId, String method, Map<String, Object> parameters, Map<Long, Double> quotaLimits, Object context) {
		throw new UnsupportedOperationException("Can't call sync methods on stub.");
	}

	@Override
	public void onEvent(String plugin, String version, String eventName, Map<String, Object> extras) {
		Log.d(TAG, "Plugin event received " + plugin + " " + eventName);
		if (plugin.equals(getName())) {
			Log.e(TAG, "This is the plugin");
			HashSet<PluginEventListener> listeners = mEventListeners.get(eventName);
			if (listeners != null) {
				for (PluginEventListener listener : listeners) {
					Log.e(TAG, "Broadcasting event");
					try {
						listener.onEvent(plugin, version, eventName, extras);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void registerEventListener(String eventName, PluginEventListener listener) {
		Log.e(TAG, "Registering listener " + listener + " to event " + eventName);
		HashSet<PluginEventListener> listeners = mEventListeners.get(eventName);
		if (listeners == null) {
			listeners = new HashSet<PluginEventListener>();
			mEventListeners.put(eventName, listeners);
			pluginIntentReceiver.registerEventListener(this);
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public void unregisterEventListener(String eventName,
			PluginEventListener listener) {
		HashSet<PluginEventListener> listeners = mEventListeners.get(eventName);
		if(listeners!=null){
			listeners.remove(listener);
		}
	}

	@Override
	public void unregisterEventListener(PluginEventListener listener) {
		for(HashSet<PluginEventListener> listeners : mEventListeners.values()){
			listeners.remove(listener);
		}
	}

	@Override
	public void cancelCallsForListener(PluginResultListener listener) {
		HashSet<Long> callsToRemove = new HashSet<Long>();
		for (Entry<Long, PluginResultListener> entry : mCallBackIdentification.entrySet()){
			if (entry.getValue().equals(listener)) {
				callsToRemove.add(entry.getKey());
			}
		}
		for (Long callId : callsToRemove){
			mCallBackIdentification.remove(callId);
		}
	}

	@Override
	public HashMap<Long, Double> getCostOfMethod(String method,
			Map<String, Object> parameters) {
		throw new UnsupportedOperationException("Can't call sync methods on stub.");
	}
}
