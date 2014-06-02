package hu.edudroid.ict_plugin_cell;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * CellService is running only to listen to phone state changes. This listener couldn't be registered elsewhere.
 * @author lajthabalazs
 *
 */
public class CellService extends Service {

	private static final String TAG = CellService.class.getName();
	private ICTPhoneStateListener stateListener;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "Cell service started");
		stateListener = new ICTPhoneStateListener(this);
	}
	
	@Override
	public void onDestroy() {
		stateListener.disconnect();
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}
}