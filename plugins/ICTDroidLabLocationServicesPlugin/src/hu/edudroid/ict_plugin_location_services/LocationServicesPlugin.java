package hu.edudroid.ict_plugin_location_services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;

import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.BasePlugin;
import hu.edudroid.interfaces.LocationServicesConstants;
import hu.edudroid.interfaces.MethodNotSupportedException;
import hu.edudroid.interfaces.PluginResult;
import hu.edudroid.interfaces.Quota;

public class LocationServicesPlugin extends BasePlugin {
	
	private static final String PLUGIN_DESCRIPTION = "Makes user location available to modules..";
	private static final String VERSION_CODE = "v1.0";
	private static final String PLUGIN_AUTHOR = "Lajtha Balázs";
	
	private static final List<String> methods;
	private static final List<String> events;
	private static List<Quota> quotas;

	@SuppressWarnings("unused")
	private static final String TAG = LocationServicesPlugin.class.getName();
	protected static final String METHOD_NAME = "METHOD_NAME";
	protected static final String CALL_ID = "CALL_ID";
	
	static {
		List<String> tmpMethods = new ArrayList<String>();
		List<String> tmpEvents = new ArrayList<String>();
		List<Quota> tmpQuotas = new ArrayList<Quota>();

		tmpMethods.add(LocationServicesConstants.METHOD_GET_LOCATION);
		tmpEvents.add(LocationServicesConstants.EVENT_LOCATION_CHANGED);

		quotas = Collections.unmodifiableList(tmpQuotas);
		methods = Collections.unmodifiableList(tmpMethods);
		events = Collections.unmodifiableList(tmpEvents);
	}
	
	public LocationServicesPlugin() {
		super(LocationServicesConstants.PLUGIN_NAME, LocationServicesPlugin.class.getPackage().getName(), LocationServicesListener.class.getName(), PLUGIN_AUTHOR,
				PLUGIN_DESCRIPTION, VERSION_CODE, events, methods, quotas);
	}

	@Override
	public PluginResult callMethodSync(long callId, String method,
			Map<String, Object> parameters, Map<Long, Double> quotaLimits, Object context)
			throws AsyncMethodException, MethodNotSupportedException {
		if (method.equals(LocationServicesConstants.METHOD_GET_LOCATION)) {
			// Start service for scan
			Intent intent = new Intent(((Context)context).getApplicationContext(), ICTLocationServcie.class);
			intent.putExtra(METHOD_NAME, LocationServicesConstants.METHOD_GET_LOCATION);
			intent.putExtra(CALL_ID, callId);
			((Context)context).startService(intent);
			throw new AsyncMethodException();
		} else {
			return null; // TODO revisit exception
		}
	}
	
	@Override
	public Map<Long, Double> getCostOfMethod(String method,
			Map<String, Object> parameters) {
		return null;
	}
}