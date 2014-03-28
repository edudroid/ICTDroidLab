package hu.edudroid.ict_plugin_battery;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class BatteryService extends Service {

	private static final String TAG = BatteryService.class.getName();
	private ScreenListener screenListener;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "Battery service started");
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        screenListener = new ScreenListener();
        registerReceiver(screenListener, filter);
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(screenListener);
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}
}