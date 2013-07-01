package hu.edudroid.ict.plugins;

import hu.edudroid.ict.PluginDetailsActivity;
import hu.edudroid.interfaces.Constants;
import hu.edudroid.interfaces.Plugin;
import hu.edudroid.interfaces.PluginEventListener;
import hu.edudroid.interfaces.PluginQuota;
import hu.edudroid.interfaces.PluginResultListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class PluginAdapter implements OnClickListener, Plugin, PluginResultListener {

	private final String					mName;
	private final String					mAuthor;
	private final String					mDescription;
	private final String					mVersionCode;
	private final ArrayList<PluginQuota>	mQuotas;
	private final PluginPollingBroadcast 	mBroadcast = PluginPollingBroadcast.getInstance();

	private Context							mContext;
	private List<String>					mPluginMethods;
	private List<String>					mEvents;
	
	
	private Map<Long, PluginResultListener> mCallBackIdentification;
	private static long mCallMethodID = 0;

	public PluginAdapter(final String name,
					final String author,
					final String description,
					final String versionCode,
					final List<String> pluginMethods,
					final List<String> events,
					final Context context) {
		mName = name;
		mAuthor = author;
		mDescription = description;
		mVersionCode = versionCode;
		mQuotas = new ArrayList<PluginQuota>();
		mPluginMethods = pluginMethods;
		mEvents = events;
		
		mCallBackIdentification = new HashMap<Long, PluginResultListener>();
		mContext = context;
	}

	public void addQuota(PluginQuota quota){
		mQuotas.add(quota);
	}

	@Override
	public void onClick(View view){
		mContext.startActivity(PluginDetailsActivity.generateIntent(hashCode(),
																	mContext));
	}
	
	@Override
	public String getName() {
		return mName;
	}

	@Override
	public String getAuthor() {
		return mAuthor;
	}

	@Override
	public String getDescription() {
		return mDescription;
	}

	@Override
	public String getVersionCode() {
		return mVersionCode;
	}

	@Override
	public List<String> getMethodNames() {
		return mPluginMethods;
	}

	@Override
	public void registerEventListener(String eventName,
			PluginEventListener listener) {
		// TODO Register modules to be notified when plugin events arrive
		
	}

	@Override
	public List<String> getAllEvents() {
		return mEvents;
	}
	
	@Override
	public long callMethodAsync(String method, List<Object> params, PluginResultListener listener){		
		
		mBroadcast.registerResultListener(this);
		
		mCallBackIdentification.put(mCallMethodID, listener);
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream stream = null;
		try{
			stream = new ObjectOutputStream(bytes);
			stream.writeObject(Integer.valueOf(params.size()));
			for (int i = 0; i < params.size(); i++)
				stream.writeObject(params.get(i));
			byte[] parameters = bytes.toByteArray();

			Intent intent = new Intent(Constants.INTENT_ACTION_CALL_METHOD);
			intent.putExtra(Constants.INTENT_EXTRA_CALL_ID, mCallMethodID);
			intent.putExtra(Constants.INTENT_EXTRA_METHOD_NAME, method);
			intent.putExtra(Constants.INTENT_EXTRA_METHOD_PARAMETERS, parameters);
			mContext.sendBroadcast(intent);

			bytes.close();
			stream.close();
			return mCallMethodID++;
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public void onResult(long id, String plugin, String pluginVersion,
			String methodName, List<String> result) {
		try{	
			mCallBackIdentification.remove(id).onResult(id, plugin, pluginVersion, methodName, result);
		} catch(NullPointerException e){
			e.printStackTrace();
		}
	}

	@Override
	public void onError(long id, String plugin, String pluginVersion, String methodName,
			String errorMessage) {
	}

	@Override
	public List<String> callMethodSync(String method, List<Object> parameters) {
		throw new UnsupportedOperationException("Can't call sync methods on stub.");
	}
}
