package hu.edudroid.ict_plugin_battery;

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
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryPlugin extends PluginCommunicationInterface {

	private static BatteryPlugin instance;

	public static BatteryPlugin getInstance() {
		if (instance == null) {
			instance = new BatteryPlugin();
		}
		return instance;
	}
	
	private static final String PLUGIN_NAME = "Battery plugin";
	private static final String PLUGIN_DESCRIPTION = "This plugin let's researchers access the battery status of the device.";

	private static final String GET_BATTERY_STATUS = "Get battery status";

	protected static final String BATTERY_LEVEL_CHANGED = "Battery level changed";
	protected static final String CHARGING_STATE_CHANGED = "Charging state changed";

	protected static final String BATTERY_LEVEL = "Battery level";
	protected static final String IS_CHARGING = "Charging state";
	protected static final String CHARGER_TYPE = "Charger type";
	protected static final String CHARGER_TYPE_USB = "USB";
	protected static final String CHARGER_TYPE_AC = "AC";

	private static final String VERSION_CODE = "v1.0";

	private static final List<String> methods;
	private static final List<String> events;
	private static final String PLUGIN_AUTHOR = "Lajtha Balázs";
	@SuppressWarnings("unused")
	private static final String TAG = BatteryPlugin.class.getName();

	static {
		List<String> tmpMethods = new ArrayList<String>();
		List<String> tmpEvents = new ArrayList<String>();

		tmpMethods.add(GET_BATTERY_STATUS);
		tmpEvents.add(BATTERY_LEVEL_CHANGED);
		tmpEvents.add(CHARGING_STATE_CHANGED);

		methods = Collections.unmodifiableList(tmpMethods);
		events = Collections.unmodifiableList(tmpEvents);
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
			BatteryPlugin batteryPlugin = BatteryPlugin.getInstance();
			Map<String, Object> values = BatteryPlugin.processIntent(intent);
			batteryPlugin.event(BatteryPlugin.BATTERY_LEVEL_CHANGED, values, context);
		} else {
			super.onReceive(context, intent);
		}
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
		return BatteryPlugin.class.getPackage().getName();
	}

	
	@Override
	public String getReceiverClassName() {
		return BatteryPlugin.class.getName();
	}

	@Override
	public List<String> getMethodNames() {
		return methods;
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
		return events;
	}


	@Override
	public PluginResult callMethodSync(long callId, String method,
			Map<String, Object> parameters, Map<Long, Double> quotaLimits, Object context)
			throws AsyncMethodException {
		if (method.equals(GET_BATTERY_STATUS)) {
			// Requesting battery status
			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = ((Context) context).registerReceiver(null, ifilter);
			return new PluginResult(processIntent(batteryStatus), null); // TODO return quota consumption
		} else {
			return null;
		}
	}

	protected void event(String eventName, Map<String, Object> result, Context context) {
		super.fireEvent(eventName, result, context);
	}

	@Override
	public List<Quota> getQuotas() {
		List<Quota> quotas = new ArrayList<Quota>();
		// TODO add quotas
		return quotas;
	}

	public static Map<String, Object> processIntent(Intent intent) {
		Map<String, Object> values = new HashMap<String, Object>();
		int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		values.put(BatteryPlugin.BATTERY_LEVEL, level);
		int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
		boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
				|| status == BatteryManager.BATTERY_STATUS_FULL;
		values.put(BatteryPlugin.IS_CHARGING, isCharging);
		int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
		if(chargePlug == BatteryManager.BATTERY_PLUGGED_USB) {
			values.put(BatteryPlugin.CHARGER_TYPE, BatteryPlugin.CHARGER_TYPE_USB);
		}
		if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) {
			values.put(BatteryPlugin.CHARGER_TYPE, BatteryPlugin.CHARGER_TYPE_AC);
		}
		return values;
	}

	@Override
	public Map<Long, Double> getCostOfMethod(String method,
			Map<String, Object> parameters) {
		return null;
	}
}