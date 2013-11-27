package hu.edudroid.ictpluginsample;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.Quota;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.util.Log;

public class SamplePlugin extends PluginCommunicationInterface {
	
	private static SamplePlugin instance;
	
	public static SamplePlugin getInstance() {
		if (instance == null) {
			instance = new SamplePlugin();
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
	private static final String TAG = SamplePlugin.class.getName();

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
	public List<String> callMethodSync(long callId, String method, List<Object> parameters, Object context) throws AsyncMethodException{
		return callMethodSync(callId, method, parameters, 0);
	}
	
	@Override
	public List<String> callMethodSync(long callId, String method, List<Object> parameters, int quotaQuantity, Object context) throws AsyncMethodException {
		if (method.equals(FIRST_SAMPLE_METHOD_NAME)) {			
			Log.d(TAG, "First sample method called");
			List<String> ret = new ArrayList<String>();
			ret.add("First sample method result");
			return ret;
		} else if (method.equals(SECOND_SAMPLE_METHOD_NAME)) {
			Log.d(TAG, "Second sample method called");
			List<String> ret = new ArrayList<String>();
			ret.add("Second sample method result");
			return ret;
		} else if (method.equals(THIRD_SAMPLE_METHOD_NAME)) {
			Log.d(TAG, "Third sample method called");
			List<String> ret = new ArrayList<String>();
			ret.add("Third sample method result");
			return ret;
		} else {
			return null;
		}
	}
	
	protected void event(String eventName, List<String> result, Context context) {
		super.fireEvent(eventName, result, context);
	}
		
	@Override
	public List<Quota> getQuotas(){
		List<Quota> quotas = new ArrayList<Quota>();
		quotas.add(new FirstSampleQuota());
		quotas.add(new SecondSampleQuota());
		return quotas;
	}

	@Override
	public Quota getQuotaForMethod(String method){
		return null;
	}

	@Override
	public boolean validateQuota(Quota quota){
		return true;
	}
	
	@Override
	public void consumeQuota(int identifier, int quantity){
	}
}