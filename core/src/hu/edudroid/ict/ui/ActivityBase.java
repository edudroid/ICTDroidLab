package hu.edudroid.ict.ui;

import hu.edudroid.ict.CoreService;
import hu.edudroid.ict.CoreService.CoreBinder;
import hu.edudroid.ict.plugins.PluginListener;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public abstract class ActivityBase extends Activity implements ServiceConnection, PluginListener {
	private static final String TAG = "ActivityBase";
	protected CoreService service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startService(new Intent(this, CoreService.class));
	}

	@Override
	protected void onResume() {
		super.onResume();
		bindService(new Intent(this, CoreService.class), this, BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onPause() {
		if (this.service != null) {
			service.unregisterPluginDetailsListener(this);
		}
		service = null;
		unbindService(this);
		super.onPause();
	}
	
	@Override
	public void onServiceConnected(ComponentName arg0, IBinder arg1) {
		Log.e(TAG, "Service connected");
		this.service = ((CoreBinder)arg1).getService();
		this.service.registerPluginDetailsListener(this);
	}
	
	@Override
	public void onServiceDisconnected(ComponentName arg0) {
		this.service = null;
	}
	

}
