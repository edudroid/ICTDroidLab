package hu.edudroid.ictdroidlocationplugin;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.PluginResult;
import hu.edudroid.interfaces.Quota;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

public class LocationPlugin extends PluginCommunicationInterface {
	
	private static LocationPlugin instance;
	
	public static LocationPlugin getInstance() {
		if (instance == null) {
			instance = new LocationPlugin();
		}
		return instance;
	}
	
	private static final String PLUGIN_NAME = "Sample plugin";
	private static final String PLUGIN_DESCRIPTION = "This is a sample plugin to demonstrate how to develop a plugin.";

	private static final String FIRST_SAMPLE_METHOD_NAME = "First sample method";
	private static final String SECOND_SAMPLE_METHOD_NAME = "Second sample method";
	private static final String THIRD_SAMPLE_METHOD_NAME = "Third sample method";
	
	protected static final String FIRST_SAMPLE_EVENT_NAME = "First sample event";
	protected static final String SECOND_SAMPLE_EVENT_NAME = "Second sample event";
	
	private static final String VERSION_CODE = "v1.0";

	private static final List<String> mMethods;
	private static final List<String> mEvents;
	private static final String PLUGIN_AUTHOR = "Lajtha Bal�zs";
	private static final String TAG = LocationPlugin.class.getName();

	static{
		List<String> methods = new ArrayList<String>();
		List<String> events = new ArrayList<String>();
		
		methods.add(FIRST_SAMPLE_METHOD_NAME);
		methods.add(SECOND_SAMPLE_METHOD_NAME);
		methods.add(THIRD_SAMPLE_METHOD_NAME);
		events.add(FIRST_SAMPLE_EVENT_NAME);
		events.add(SECOND_SAMPLE_EVENT_NAME);
		
		mMethods = Collections.unmodifiableList(methods);
		mEvents = Collections.unmodifiableList(events);
	}
	
	@Override
	public String getVersionCode() {
		return VERSION_CODE;
	}
	
	@Override
	public String getName() {
		return PLUGIN_NAME;
	}
	
	@Override
	public String getPackageName() {
		return LocationPlugin.class.getPackage().getName();
	}

	
	@Override
	public String getReceiverClassName() {
		return LocationPlugin.class.getName();
	}
	
	@Override
	public List<String> getMethodNames() {
		return mMethods;
	}
	
	@Override
	public String getDescription() {
		return PLUGIN_DESCRIPTION;
	}
	
	@Override
	public String getAuthor() {
		return PLUGIN_AUTHOR;
	}
	
	@Override
	public List<String> getAllEvents() {
		return mEvents;
	}
	
	@Override
	public PluginResult callMethodSync(long callId, String method, Map<String, Object> parameters, Map<Long, Double> quotaQuantity, Object context) throws AsyncMethodException {
		if (method.equals(FIRST_SAMPLE_METHOD_NAME)) {			
			Log.d(TAG, "First sample method called");
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("Value","First sample method result");
			return new PluginResult(ret, null);
		} else if (method.equals(SECOND_SAMPLE_METHOD_NAME)) {
			Log.d(TAG, "Second sample method called");
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("Value","Second sample method result");
			return new PluginResult(ret, null);
		} else if (method.equals(THIRD_SAMPLE_METHOD_NAME)) {
			Log.d(TAG, "Third sample method called");
			Map<String, Object> ret = new HashMap<String, Object>();
			ret.put("Value","Third sample method result");
			return new PluginResult(ret, null);
		} else {
			return null;
		}
	}
	
	protected void event(String eventName, Map<String, Object> result, Context context) {
		super.fireEvent(eventName, result, context);
	}
		
	@Override
	public List<Quota> getQuotas(){
		List<Quota> quotas = new ArrayList<Quota>();
		quotas.add(new Quota(0, "First sample quota", 3600, new int[]{100, 200, 500, 1000, 10000} ));
		quotas.add(new Quota(1, "Second sample quota", 86400, new int[]{10, 20, 50, 100} ));
		return quotas;
	}

	@Override
	public Map<Long, Double> getCostOfMethod(String method,
			Map<String, Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}
}