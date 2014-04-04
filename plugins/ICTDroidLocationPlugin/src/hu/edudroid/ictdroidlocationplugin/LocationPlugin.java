package hu.edudroid.ictdroidlocationplugin;

import hu.edudroid.interfaces.AsyncMethodException;
import hu.edudroid.interfaces.BasePlugin;
import hu.edudroid.interfaces.MethodNotSupportedException;
import hu.edudroid.interfaces.PluginListener;
import hu.edudroid.interfaces.PluginResult;
import hu.edudroid.interfaces.PluginResultListener;
import hu.edudroid.interfaces.Quota;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class LocationPlugin extends BasePlugin implements LocationListener {

	private static LocationPlugin	instance;

	public static LocationPlugin getInstance(){
		if (instance == null){
			instance = new LocationPlugin();
		}
		return instance;
	}

	private static final String			PLUGIN_NAME						= "Location plugin";
	private static final String			PLUGIN_DESCRIPTION				= "Location plugin returns the actual location of the the device asynchronously";

	private static final String			METHOD_NAME_REQUEST_LOCATION	= "Request locattion";

	protected static final String		FIRST_SAMPLE_EVENT_NAME			= "First sample event";
	protected static final String		SECOND_SAMPLE_EVENT_NAME		= "Second sample event";

	private static final String			VERSION_CODE					= "v1.0";

	private static final List<String>	mMethods;
	private static final List<String>	mEvents;
	private static List<Quota>			mQuotas;

	private static final String			PLUGIN_AUTHOR					= "Nagy László";
	private static final String			TAG								= LocationPlugin.class.getName();

	private PluginResultListener		mListener;

	static{
		List<String> methods = new ArrayList<String>();
		List<String> events = new ArrayList<String>();
		List<Quota> tmpQuotas = new ArrayList<Quota>();

		methods.add(METHOD_NAME_REQUEST_LOCATION);
		events.add(FIRST_SAMPLE_EVENT_NAME);
		events.add(SECOND_SAMPLE_EVENT_NAME);

		mQuotas = Collections.unmodifiableList(tmpQuotas);
		mMethods = Collections.unmodifiableList(methods);
		mEvents = Collections.unmodifiableList(events);
	}

	public LocationPlugin() {
		super(	PLUGIN_NAME,
				LocationPlugin.class.getPackage().getName(),
				BootingBroadcastReceiver.class.getName(),
				PLUGIN_AUTHOR,
				PLUGIN_DESCRIPTION,
				VERSION_CODE,
				mEvents,
				mMethods,
				mQuotas);

	}

	@Override
	public String getVersionCode(){
		return VERSION_CODE;
	}

	@Override
	public String getName(){
		return PLUGIN_NAME;
	}

	@Override
	public String getPackageName(){
		return LocationPlugin.class.getPackage().getName();
	}

	@Override
	public String getReceiverClassName(){
		return LocationPlugin.class.getName();
	}

	@Override
	public List<String> getMethodNames(){
		return mMethods;
	}

	@Override
	public String getDescription(){
		return PLUGIN_DESCRIPTION;
	}

	@Override
	public String getAuthor(){
		return PLUGIN_AUTHOR;
	}

	@Override
	public List<String> getAllEvents(){
		return mEvents;
	}

	@Override
	public long callMethodAsync(String method,
								Map parameters,
								PluginResultListener listener,
								Map quotaLimits){
		if (method.equals(METHOD_NAME_REQUEST_LOCATION)){
			if (parameters.get(0) != null
				&& parameters.get(1) != null
				&& parameters.get(2) != null){
				mListener = listener;
				// TODO Context from paramter
				final Context context = LocationPluginApplication.getInstance();
				LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

				final String provider = (String) parameters.get(0);
				final Long minTime = (Long) parameters.get(1);
				final Float minDistance = (Float) parameters.get(2);
				manager.requestLocationUpdates(	provider,
												minTime,
												minDistance,
												this);
				return 0L;
			}
		}
		return 0L;
	}

	@Override
	public PluginResult callMethodSync(	long callId,
										String method,
										Map parameters,
										Map quotaLimits,
										Object context)	throws AsyncMethodException,
														MethodNotSupportedException{
		return null;
	}

	protected void event(	String eventName,
							Map<String, Object> result,
							Context context){

	}

	@Override
	public List<Quota> getQuotas(){
		List<Quota> quotas = new ArrayList<Quota>();
		quotas.add(new Quota(	0,
								"First sample quota",
								3600,
								new int[]{ 100, 200, 500, 1000, 10000 }));
		quotas.add(new Quota(	1,
								"Second sample quota",
								86400,
								new int[]{ 10, 20, 50, 100 }));
		return quotas;
	}

	@Override
	public Map<Long, Double> getCostOfMethod(String method, Map parameters){
		return null;
	}

	@Override
	public void onLocationChanged(Location location){

	}

	@Override
	public void onProviderDisabled(String provider){
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider){
		// Nothing here

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras){
		// Nothin here

	}

}