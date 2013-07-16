package hu.edudroid.ict.plugins;

import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginResultListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;

public class PluginAdapter implements Plugin, PluginResultListener, PluginEventListener {

	private final String					mName;
	private final String					mAuthor;
	private final String					mDescription;
	private final String					mVersionCode;
	private final PLuginIntentReceiver 	mBroadcast;

	private Context							mContext;
	private List<String>					mPluginMethods;
	private List<String>					mEvents;
	
	
	private Map<Long, PluginResultListener> mCallBackIdentification;
	private Map<String,HashSet<PluginEventListener>> mEventListeners;
	
	private static long mCallMethodID = 0;

	public PluginAdapter(final String name,
					final String author,
					final String description,
					final String versionCode,
					final List<String> pluginMethods,
					final List<String> events,
					PLuginIntentReceiver broadcast,
					final Context context) {
		mName = name;
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
	public List<String> getMethodNames() {
		return mPluginMethods;
	}

	@Override
	public List<String> getAllEvents() {
		return mEvents;
	}
	
	@Override
	public long callMethodAsync(String method, List<Object> params, PluginResultListener listener){		
		
		mBroadcast.registerResultListener(this);
		
		mCallBackIdentification.put(mCallMethodID, listener);
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream stream = null;
		try{
			stream = new ObjectOutputStream(bytes);
			stream.writeObject(Integer.valueOf(params.size()));
			for (int i = 0; i < params.size(); i++)
				stream.writeObject(params.get(i));
			byte[] parameters = bytes.toByteArray();

			Intent intent = new Intent(Constants.INTENT_ACTION_CALL_METHOD);
			intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, mCallMethodID);
			intent.putExtra(Constants.INTENT_EXTRA_METHOD_NAME, method);
			intent.putExtra(Constants.INTENT_EXTRA_METHOD_PARAMETERS, parameters);
			mContext.sendBroadcast(intent);

			bytes.close();
			stream.close();
			return mCallMethodID++;
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, List<String> result) {
		try{
			mCallBackIdentification.remove(id).onResult(id, plugin, pluginVersion, methodName, result);
		} catch(NullPointerException e){
			e.printStackTrace();
		}
	}

	@Override
	public void onError(long id, String plugin, String pluginVersion, String methodName,
			String errorMessage) {
	}

	@Override
	public List<String> callMethodSync(long callId, String method, List<Object> parameters) {
		throw new UnsupportedOperationException("Can't call sync methods on stub.");
	}

	@Override
	public void onEvent(String plugin, String version, String eventName, List<String> extras) {
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
}
