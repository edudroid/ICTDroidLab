package hu.edudroid.ict_plugin_vehicleict;

import java.util.Set;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Master service of the plugin, manages the lifetime
 * @author mtmkls
 *
 */
public class VehicleICTService extends Service {

	private static final String TAG = VehicleICTService.class.getName();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "VehicleICT service started");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		VehicleICTPlugin.getInstance().disconnect();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if (adapter == null) {
		    //TODO Device does not support Bluetooth
			return START_STICKY;
		}

		if (!adapter.isEnabled()) {
		    //TODO in this case we cannot do anything here
			return START_STICKY;

			//Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    //startActivityForResult(enableBtIntent, 1); //TODO I need to be an Activity for this
		    //sendBroadcast(enableBtIntent); //TODO is this a good substitute? no
		    //TODO this seems impossible from a Service, we would need an Activity for this
		}

		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
		    for (BluetoothDevice device : pairedDevices) {
		    	if (device.getName().equals("ODBII")) {
		    		//TODO there may be more dongles paired with this phone
		    		VehicleICTPlugin plugin = VehicleICTPlugin.getInstance();
		    		plugin.setDeviceID(device.getAddress());
		    		break;
		    	}
		    	Log.d(TAG, "Device " + device.getName() + " " + device.getAddress());
		    }
		}

		//TODO start device discovery here? no

		return START_STICKY;
	}
}
