package hu.edudroid.ict_plugin_wifi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.net.wifi.WifiManager;

import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.BasePlugin;
import hu.edudroid.interfaces.MethodNotSupportedException;
import hu.edudroid.interfaces.PluginResult;
import hu.edudroid.interfaces.Quota;
import hu.edudroid.interfaces.SimpleWiFiConstants;

public class WiFiPlugin extends BasePlugin {
	
	private static final String PLUGIN_DESCRIPTION = "This plugin let's researchers access the battery status of the device.";
	private static final String VERSION_CODE = "v1.0";
	private static final String PLUGIN_AUTHOR = "Lajtha Balázs";
	
	private static final List<String> methods;
	private static final List<String> events;
	private static List<Quota> quotas;

	@SuppressWarnings("unused")
	private static final String TAG = WiFiPlugin.class.getName();
	
	static {
		List<String> tmpMethods = new ArrayList<String>();
		List<String> tmpEvents = new ArrayList<String>();
		List<Quota> tmpQuotas = new ArrayList<Quota>();

		tmpMethods.add(SimpleWiFiConstants.METHOD_SCAN_WIFI);
		tmpEvents.add(SimpleWiFiConstants.EVENT_WIFI_SIGNAL_STRENGTH_CHANGED);
		tmpEvents.add(SimpleWiFiConstants.EVENT_WIFI_STATE_CHANGED);
		tmpEvents.add(SimpleWiFiConstants.EVENT_WIFI_SCAN_RESULT_RECEIVED);

		quotas = Collections.unmodifiableList(tmpQuotas);
		methods = Collections.unmodifiableList(tmpMethods);
		events = Collections.unmodifiableList(tmpEvents);
	}
	
	public WiFiPlugin() {
		super(SimpleWiFiConstants.PLUGIN_NAME, WiFiPlugin.class.getPackage().getName(), WiFiListener.class.getName(), PLUGIN_AUTHOR,
				PLUGIN_DESCRIPTION, VERSION_CODE, events, methods, quotas);
	}

	@Override
	public PluginResult callMethodSync(long callId, String method,
			Map<String, Object> parameters, Map<Long, Double> quotaLimits, Object context)
			throws AsyncMethodException, MethodNotSupportedException {
		if (method.equals(SimpleWiFiConstants.METHOD_SCAN_WIFI)) {
			// Start service for scan
			WifiManager wifi = (WifiManager)((Context)context).getSystemService(Context.WIFI_SERVICE);
			wifi.startScan();
			throw new AsyncMethodException();
		} else {
			return null; // TODO revisit exception
			// throw new MethodNotSupportedException("Method " + method + " not supported for " + SimpleWiFiConstants.PLUGIN_NAME);
		}
	}
	
	@Override
	public Map<Long, Double> getCostOfMethod(String method,
			Map<String, Object> parameters) {
		return null;
	}
}