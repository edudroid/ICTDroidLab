package hu.edudroid.ict_plugin_location_services;

import hu.edudroid.ictplugin.PluginCommunicationInterface;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.LocationServicesConstants;
import hu.edudroid.interfaces.PluginResult;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;

public class ICTLocationServcie extends Service implements ConnectionCallbacks, OnConnectionFailedListener {

	private static final String TAG = ICTLocationServcie.class.getName();
	private boolean connected = false;
	private LocationClient mLocationClient;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(TAG, "Location service started");
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();
	}
	
	@Override
	public void onDestroy() {
		mLocationClient.disconnect();
		super.onDestroy();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		Log.e(TAG, "Event received at service: " + intent.getAction());
		PluginCommunicationInterface communicationInterface = new PluginCommunicationInterface(new LocationServicesPlugin());
		// Subscribe to screen event when any broadcast is received
		if (intent.hasExtra(LocationServicesPlugin.METHOD_NAME) && intent.getStringExtra(LocationServicesPlugin.METHOD_NAME).equals(LocationServicesConstants.METHOD_GET_LOCATION)) {
			Log.e(TAG, "Requested location");
			long callId = intent.getLongExtra(LocationServicesPlugin.CALL_ID, -1);
			Log.e(TAG, "Connected : " + connected);
			if (connected) {
				Location mCurrentLocation = mLocationClient.getLastLocation();
				Log.e(TAG, "Current location : " + mCurrentLocation);
				if (mCurrentLocation != null) {
					Map<String, Object> results = new HashMap<String, Object>();
					results.put(LocationServicesConstants.KEY_LATITUDE, mCurrentLocation.getLatitude());
					results.put(LocationServicesConstants.KEY_LONGITUDE, mCurrentLocation.getLongitude());
					results.put(LocationServicesConstants.KEY_ACCURACY, mCurrentLocation.getAccuracy());
					results.put(LocationServicesConstants.KEY_PROVIDER, mCurrentLocation.getProvider());
					results.put(LocationServicesConstants.KEY_SPEED, mCurrentLocation.getSpeed());
					// Build result
					communicationInterface.reportResult(callId, Constants.INTENT_EXTRA_VALUE_RESULT, LocationServicesConstants.METHOD_GET_LOCATION, new PluginResult(results, null), this);
				} else {
					communicationInterface.reportResult(callId, Constants.INTENT_EXTRA_VALUE_ERROR, LocationServicesConstants.METHOD_GET_LOCATION, new PluginResult(null, null), this);					
				}
			} else {
				communicationInterface.reportResult(callId, Constants.INTENT_EXTRA_VALUE_ERROR, LocationServicesConstants.METHOD_GET_LOCATION, new PluginResult(null, null), this);
			}
		}
		return START_STICKY;
	}
	
	@Override
	public void onConnected(Bundle arg0) {
		connected = true;
	}

	@Override
	public void onDisconnected() {
		connected = false;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Log.e(TAG, "Connection failed " + arg0);
	}
}