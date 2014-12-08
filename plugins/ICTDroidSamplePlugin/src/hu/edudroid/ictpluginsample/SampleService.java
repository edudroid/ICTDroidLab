package hu.edudroid.ictpluginsample;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class SampleService extends Service {

	private static final String TAG = SampleService.class.getName();
	private SampleEventDispatcher runtimeListener;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "Battery service started");
		IntentFilter filter = new IntentFilter();
        runtimeListener = new SampleEventDispatcher();
        registerReceiver(runtimeListener, filter);
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(runtimeListener);
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}
}