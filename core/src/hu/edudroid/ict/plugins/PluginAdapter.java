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
	private final String					mName;
	private final String					mPackage;
	private final String					mReceiverClassName;
	private final String					mAuthor;
	private final String					mDescription;
	private final String					mVersionCode;
	private final PluginIntentReceiver 		mBroadcast;

	private Context							mContext;
	private List<String>				mPluginMethods;
	private List<String>					mEvents;
	
	
	private Map<Long, PluginResultListener> mCallBackIdentification;
	private Map<String,HashSet<PluginEventListener>> mEventListeners;
	
	private static long mCallMethodID = 0;

	public PluginAdapter(final String name,
					final String packageName,
					final String className,
					final String author,
					final String description,
					final String versionCode,
					final List<String> pluginMethods,
					final List<String> events,
					PluginIntentReceiver broadcast,
					final Context context) {
		mName = name;
		mPackage = packageName;
		mReceiverClassName = className;
		mAuthor = author;
		mDescription = description;
		mVersionCode = versionCode;
		mPluginMethods = pluginMethods;
		mEvents = events;
		mBroadcast = broadcast;
		
		mCallBackIdentification = new HashMap<Long, PluginResultListener>();
		mEventListeners = new HashMap<String,HashSet<PluginEventListener>>();
		mContext = context;
	}

	@Override
	public String getName() {
		return mName;
	}

	@Override
	public String getPackageName() {
		return mPackage;
	}

	@Override
	public String getReceiverClassName() {
		return mReceiverClassName;
	};

	@Override
	public String getAuthor() {
		return mAuthor;
	}

	@Override
	public String getDescription() {
		return mDescription;
	}

	@Override
	public String getVersionCode() {
		return mVersionCode;
	}
	
	@Override
	public List<String> getMethodNames(){
		return mPluginMethods;
	}
	
	@Override
	public List<Quota> getQuotas(){
		final ArrayList<Quota> quotas = new ArrayList<Quota>();
		// TODO retrieve quotas
		return quotas;
	}
	
	@Override
	public List<String> getAllEvents() {
		return mEvents;
	}
	
	public long callMethodAsync(String method, Map<String, Object> params, PluginResultListener listener){
		return callMethodAsync(method, params, listener, null);
	}
	
	@Override
	public long callMethodAsync(String method, Map<String, Object> params, PluginResultListener listener, Map<Long, Double> quotaLimits){		
		
		mBroadcast.registerResultListener(this);
		
		mCallBackIdentification.put(mCallMethodID, listener);

		// TODO check quota limits
		// TODO if no limits are set, retrieve max limit from system
		
		Intent intent = new Intent(Constants.INTENT_ACTION_CALL_METHOD);
		intent.setComponent(new ComponentName(mPackage, mReceiverClassName));
		intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, mCallMethodID);
		intent.putExtra(Constants.INTENT_EXTRA_METHOD_NAME, method);
		byte[] paramBytes = Utils.mapToByteArray(params);
		if (paramBytes != null) {
			intent.putExtra(Constants.INTENT_EXTRA_METHOD_PARAMETERS, paramBytes);
		}
		mContext.sendBroadcast(intent);

		return mCallMethodID++;
	}

	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, Map<String, Object> result) {
		if (plugin.equals(mName)) {
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
		if (plugin.equals(getName())) {
			HashSet<PluginEventListener> listeners = mEventListeners.get(eventName);
			if (listeners != null) {
				for (PluginEventListener listener : listeners) {
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
		HashSet<PluginEventListener> listeners = mEventListeners.get(eventName);
		if (listeners == null) {
			listeners = new HashSet<PluginEventListener>();
			mEventListeners.put(eventName, listeners);
			mBroadcast.registerEventListener(this);
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
