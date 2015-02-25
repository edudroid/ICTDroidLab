package hu.edudroid.interfaces;

public class CellConstants {
	public static final String PLUGIN_NAME = "Cell info plugin";

	public static final String METHOD_GET_NEIGHBORING_CELL_INFO = "Get neighboring cell info";

	public static final String EVENT_SIGNAL_STRENGTH_CHANGED = "Signal strength changed";
	public static final String KEY_CELLS = "Cells";
	public static final String KEY_SIGNAL_STRENGTH = "Signal strength";
	public static final String KEY_NETWORK_TECHNOLOGY = "Network technology";
	public static final String VALUE_GSM = "GSM";
	public static final String VALUE_CDMA = "CDMA";
	public static final String VALUE_OTHER = "Other";
	public static final String KEY_CELL_ID = "Cell id";

	public static final String EVENT_CELL_LOCATION_CHANGED = "Cell location changed";
	public static final String KEY_GSM_CELL_ID = "Cell id";
	public static final String KEY_GSM_AREA_CODE = "Area code";
	public static final String KEY_CDSM_BASE_STATION_ID = "Base station id";
	public static final String KEY_CDSM_NETWORK_ID = "Network id";

	public static final String EVENT_DATA_STATE_CHANGED = "Data state changed";
	public static final String KEY_DATA_STATE = "Data state";
	public static final String VALUE_DATA_STATE_DISCONNETED = "Disconnected";
	public static final String VALUE_DATA_STATE_CONNECTING = "Connecting";
	public static final String VALUE_DATA_STATE_CONNETED = "Connected";
	public static final String VALUE_DATA_STATE_SUSPENDED = "Suspended";

	public static final String EVENT_SERVICE_STATE_CHANGED = "Service state changed";
	public static final String KEY_SERVICE_STATE = "Service state";
	public static final String VALUE_SERVICE_STATE_EMERGENCY_ONLY = "Emergency only";
	public static final String VALUE_SERVICE_STATE_IN_SERVICE = "In service";
	public static final String VALUE_SERVICE_STATE_OUT_OF_SERVICE = "Out of service";
	public static final String VALUE_SERVICE_STATE_POWER_OFF = "Power off";

	public static final String KEY_OPERATOR = "Operator";
	public static final String KEY_ROAMING = "Roaming";
}
