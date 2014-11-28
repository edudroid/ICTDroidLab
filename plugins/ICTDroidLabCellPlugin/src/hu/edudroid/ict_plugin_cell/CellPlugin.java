package hu.edudroid.ict_plugin_cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;

import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.BasePlugin;
import hu.edudroid.interfaces.CellConstants;
import hu.edudroid.interfaces.LocationServicesConstants;
import hu.edudroid.interfaces.MethodNotSupportedException;
import hu.edudroid.interfaces.PluginResult;
import hu.edudroid.interfaces.Quota;

public class CellPlugin extends BasePlugin {
	
	private static final String PLUGIN_DESCRIPTION = "Makes user location available to modules..";
	private static final String VERSION_CODE = "v1.0";
	private static final String PLUGIN_AUTHOR = "Lajtha Balázs";
	
	private static final List<String> methods;
	private static final List<String> events;
	private static List<Quota> quotas;

	@SuppressWarnings("unused")
	private static final String TAG = CellPlugin.class.getName();
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
	
	public CellPlugin() {
		super(LocationServicesConstants.PLUGIN_NAME, CellPlugin.class.getPackage().getName(), CellListener.class.getName(), PLUGIN_AUTHOR,
				PLUGIN_DESCRIPTION, VERSION_CODE, events, methods, quotas);
	}

	@Override
	public PluginResult callMethodSync(long callId, String method,
			Map<String, Object> parameters, Map<Long, Double> quotaLimits, Object context)
			throws AsyncMethodException, MethodNotSupportedException {
		if (method.equals(CellConstants.METHOD_GET_NEIGHBORING_CELL_INFO)) {
			TelephonyManager telephonyManager = (TelephonyManager)((Context)context).getSystemService(Context.TELEPHONY_SERVICE);
			List<NeighboringCellInfo> cells = telephonyManager.getNeighboringCellInfo();
			Map<String, Object> data = new HashMap<String, Object>();
			List<HashMap<String, String>> cellsData = new ArrayList<HashMap<String,String>>();
			for(NeighboringCellInfo info : cells) {
				HashMap<String, String> cellData = new HashMap<String, String>();
				switch (info.getNetworkType()) {
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_GPRS:
					cellData.put(CellConstants.KEY_CELL_ID, "" + info.getCid());
					cellsData.add(cellData);
					break;
				case TelephonyManager.NETWORK_TYPE_UMTS:
				case TelephonyManager.NETWORK_TYPE_HSPA:
				case TelephonyManager.NETWORK_TYPE_HSDPA:
				case TelephonyManager.NETWORK_TYPE_HSUPA:
					cellData.put(CellConstants.KEY_CELL_ID, "" + info.getPsc());
					cellsData.add(cellData);
					break;
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
					// skip
				}
			}
			data.put(CellConstants.KEY_CELLS, cellsData );
			PluginResult result = new PluginResult(data, new HashMap<Long, Double>());
			return result;
		} else {
			return null;
		}
	}
	
	@Override
	public Map<Long, Double> getCostOfMethod(String method,
			Map<String, Object> parameters) {
		return null;
	}
}