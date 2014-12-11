package hu.edudroid.ict_plugin_vehicleict;

import hu.bute.daai.amorg.vehicleict.platform.lib.Platform;
import hu.bute.daai.amorg.vehicleict.platform.lib.SampleListener;
import hu.bute.daai.amorg.vehicleict.platform.lib.model.Configuration;
import hu.bute.daai.amorg.vehicleict.platform.lib.model.Fields;
import hu.bute.daai.amorg.vehicleict.platform.lib.model.ReportInterval;
import hu.bute.daai.amorg.vehicleict.platform.lib.model.Sample;
import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.BasePlugin;
import hu.edudroid.interfaces.MethodNotSupportedException;
import hu.edudroid.interfaces.PluginResult;
import hu.edudroid.interfaces.Quota;
import hu.edudroid.interfaces.VehicleICTConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

public class VehicleICTPlugin extends BasePlugin {

	private static VehicleICTPlugin instance;

	public static VehicleICTPlugin getInstance() {
		if (instance == null) {
			instance = new VehicleICTPlugin();
		}
		return instance;
	}

	private Platform m_platform;
	private String m_device_id = "PlatformConstants.DEVICE_DEMO"; //TODO hardcode the MAC of the CANbus dongle here

	private static final String PLUGIN_DESCRIPTION = "Makes vehicle information from VehicleICT available to modules..";
	private static final String VERSION_CODE = "v1.0";
	private static final String PLUGIN_AUTHOR = "Máté Miklós";

	private static final List<String> methods;
	private static final List<String> events;
	private static List<Quota> quotas;

	@SuppressWarnings("unused")
	private static final String TAG = VehicleICTPlugin.class.getName();
	protected static final String METHOD_NAME = "METHOD_NAME";
	protected static final String CALL_ID = "CALL_ID";

	static {
		List<String> tmpMethods = new ArrayList<String>();
		List<String> tmpEvents = new ArrayList<String>();
		List<Quota> tmpQuotas = new ArrayList<Quota>();

		tmpEvents.add(VehicleICTConstants.EVENT_VEHICLE_DATA_RECEIVED);

		quotas = Collections.unmodifiableList(tmpQuotas);
		methods = Collections.unmodifiableList(tmpMethods);
		events = Collections.unmodifiableList(tmpEvents);
	}

	class Receiver implements SampleListener {

		private final Context m_context;

		public Receiver(Context context) {
			m_context = context; //TODO I hope context is not ephemeral
		}

		@Override
		public void receiverSample(Sample arg0) {
			Map<String, Object> values = new HashMap<String, Object>();
			values.put("RPM", arg0.getEngineRPM());
			values.put("Speed", arg0.getVehicleSpeed());

			PluginCommunicationInterface comm = new PluginCommunicationInterface(getInstance());
			comm.fireEvent(VehicleICTConstants.EVENT_VEHICLE_DATA_RECEIVED, values, m_context);
		}
	}

	private VehicleICTPlugin() {
		super(VehicleICTConstants.PLUGIN_NAME, VehicleICTPlugin.class.getPackage().getName(), VehicleICTListener.class.getName(), PLUGIN_AUTHOR,
				PLUGIN_DESCRIPTION, VERSION_CODE, events, methods, quotas);
	}

	public void setDeviceID(final String id) {
		m_device_id = id;
	}

	@Override
	public PluginResult callMethodSync(long callId, String method,
			Map<String, Object> parameters, Map<Long, Double> quotaLimits, Object context)
			throws AsyncMethodException, MethodNotSupportedException {
		if (method.equals(VehicleICTConstants.METHOD_SUBSCRIBE)) {
			/*int period_ms = 1000*10;
			for (Map.Entry<String, Object> entry : parameters.entrySet()) {
				if (entry.getKey().equals("period")) {
					period_ms = (int) entry.getValue(); //TODO how do I get a value out of that?
				}
			}
			//TODO how to convert period_ms to that stupid Enum?
			*/

			//TODO what if we have multiple subscribers?
			disconnect();

			//TODO app name? the same as in the contract (we need a contract!)
			//TODO user id? we need a login to the server (we need a login to the server!)
			// device id: MAC of the dongle, we need to discover it (hardcode for the demo)
			Configuration conf = new Configuration.Builder("DroidLabPlugin", 1234, m_device_id).
					setRequiredFields(Fields.getAllCommands()).
	                setReportToServer(false).
	                setReportInterval(ReportInterval.Sec1).
	                build();

			m_platform = new Platform((Context)context, conf, new Receiver((Context)context));
			m_platform.connect();

		} else if (method.equals(VehicleICTConstants.METHOD_UNSUBSCRIBE)) {
			//TODO what if we have multiple subscribers?
			disconnect();

		} else {
			throw new MethodNotSupportedException(method);
		}

		// note: we must return something or NullPointerException in PluginCommunicationInterface
		Map<String, Object> data = new HashMap<String, Object>();
		return new PluginResult(data, null);

	}

	@Override
	public Map<Long, Double> getCostOfMethod(String method, Map<String, Object> parameters) {
		return null;
	}

	public void disconnect() {
		if (m_platform != null) {
			m_platform.disconnect();
			m_platform = null;
		}
	}

}
