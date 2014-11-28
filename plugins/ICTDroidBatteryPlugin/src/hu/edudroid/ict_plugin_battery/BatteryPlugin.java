package hu.edudroid.ict_plugin_battery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.BasePlugin;
import hu.edudroid.interfaces.BatteryConstants;
import hu.edudroid.interfaces.PluginResult;
import hu.edudroid.interfaces.Quota;

public class BatteryPlugin extends BasePlugin {
	
	private static final String PLUGIN_DESCRIPTION = "This plugin let's researchers access the battery status of the device.";
	private static final String VERSION_CODE = "v1.0";
	private static final String PLUGIN_AUTHOR = "Lajtha Balázs";
	
	private static final List<String> methods;
	private static final List<String> events;
	private static List<Quota> quotas;

	@SuppressWarnings("unused")
	private static final String TAG = BatteryPlugin.class.getName();
	
	static {
		List<String> tmpMethods = new ArrayList<String>();
		List<String> tmpEvents = new ArrayList<String>();
		List<Quota> tmpQuotas = new ArrayList<Quota>();

		tmpMethods.add(BatteryConstants.GET_BATTERY_STATUS);
		tmpEvents.add(BatteryConstants.BATTERY_LEVEL_CHANGED);
		tmpEvents.add(BatteryConstants.CHARGING_STATE_CHANGED);
		tmpEvents.add(BatteryConstants.SCREEN_STATE_CHANGED);

		quotas = Collections.unmodifiableList(tmpQuotas);
		methods = Collections.unmodifiableList(tmpMethods);
		events = Collections.unmodifiableList(tmpEvents);
	}
	
	public BatteryPlugin() {
		super(BatteryConstants.PLUGIN_NAME, BatteryPlugin.class.getPackage().getName(), BatteryListener.class.getName(), PLUGIN_AUTHOR,
				PLUGIN_DESCRIPTION, VERSION_CODE, events, methods, quotas);
	}

	@Override
	public PluginResult callMethodSync(long callId, String method,
			Map<String, Object> parameters, Map<Long, Double> quotaLimits, Object context)
			throws AsyncMethodException {
		if (method.equals(BatteryConstants.GET_BATTERY_STATUS)) {
			// Requesting battery status
			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = ((Context) context).registerReceiver(null, ifilter);
			return new PluginResult(processIntent(batteryStatus), null); // TODO return quota consumption
		} else {
			return null;
		}
	}
	
	public static Map<String, Object> processIntent(Intent intent) {
		Map<String, Object> values = new HashMap<String, Object>();
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		values.put(BatteryConstants.BATTERY_LEVEL, level);
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL;
		values.put(BatteryConstants.IS_CHARGING, isCharging);
		int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		if(chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
			values.put(BatteryConstants.CHARGER_TYPE, BatteryConstants.CHARGER_TYPE_USB);
		} else if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
			values.put(BatteryConstants.CHARGER_TYPE, BatteryConstants.CHARGER_TYPE_AC);
		} else {
			values.put(BatteryConstants.CHARGER_TYPE, "Unknown charger");
		}
		return values;
	}

	@Override
	public Map<Long, Double> getCostOfMethod(String method,
			Map<String, Object> parameters) {
		return null;
	}
}