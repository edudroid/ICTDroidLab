package hu.edudroid.ictpluginsample;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.BasePlugin;
import hu.edudroid.interfaces.PluginResult;
import hu.edudroid.interfaces.Quota;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class SamplePlugin extends BasePlugin {
	
	
	PluginCommunicationInterface communicationInterface = new PluginCommunicationInterface(new SamplePlugin());
	
	private static final String PLUGIN_NAME = "Sample plugin";
	private static final String PLUGIN_DESCRIPTION = "This is a sample plugin to demonstrate how to develop a plugin.";

	private static final String FIRST_SAMPLE_METHOD_NAME = "First sample method";
	private static final String SECOND_SAMPLE_METHOD_NAME = "Second sample method";
	private static final String THIRD_SAMPLE_METHOD_NAME = "Third sample method";
	
	private static final String FIRST_SAMPLE_EVENT_NAME = "First sample event";
	private static final String SECOND_SAMPLE_EVENT_NAME = "Second sample event";
	
	private static final String VERSION_CODE = "v1.0";

	private static final List<String> methods;
	private static final List<String> events;
	private static List<Quota> quotas;
	private static final String PLUGIN_AUTHOR = "Lajtha Bal�zs";
	private static final String TAG = SamplePlugin.class.getName();

	static{
		List<String> localMethods = new ArrayList<String>();
		List<String> localEvents = new ArrayList<String>();
		
		localMethods.add(FIRST_SAMPLE_METHOD_NAME);
		localMethods.add(SECOND_SAMPLE_METHOD_NAME);
		localMethods.add(THIRD_SAMPLE_METHOD_NAME);
		localEvents.add(FIRST_SAMPLE_EVENT_NAME);
		localEvents.add(SECOND_SAMPLE_EVENT_NAME);
		
		methods = Collections.unmodifiableList(localMethods);
		events = Collections.unmodifiableList(localEvents);
	}
	
	public SamplePlugin() {
		super(PLUGIN_NAME, SamplePlugin.class.getPackage().getName(), SampleEventDispatcher.class.getName(), PLUGIN_AUTHOR, PLUGIN_DESCRIPTION, VERSION_CODE,
				events, methods, quotas);
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
			Log.d(TAG, "Third sample method, this is a long one");
			throw new AsyncMethodException();
		} else {
			return null;
		}
	}
}