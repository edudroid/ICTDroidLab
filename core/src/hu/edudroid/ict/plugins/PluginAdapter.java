package hu.edudroid.ict.plugins;

import hu.edudroid.ict.PluginDetailsActivity;
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
import android.view.View;
import android.view.View.OnClickListener;

public class PluginAdapter implements OnClickListener, Plugin, PluginResultListener {

	private final String					INTENT_CALL_PLUGIN_METHOD		= "hu.edudroid.ict.plugin.callmethod";
	private final String					INTENT_EXTRA_METHOD_NAME		= "methodname";
	private final String					INTENT_EXTRA_METHOD_PARAMETERS	= "methodparams";

	private final String					mName;
	private final String					mAuthor;
	private final String					mDescription;
	private final String					mVersionCode;
	private final ArrayList<PluginQuota>	mQuotas;

	private Context							mContext;
	private List<String>					mPluginMethods;
	private List<String>					mEvents;
	
	private Map<Integer,PluginResultListener> mCallBackIdentification;
	private static int mCallMethodID=0;

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
		
		mCallBackIdentification=new HashMap<Integer, PluginResultListener>();
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
		// TODO Auto-generated method stub
		// Egyes eventekhez tárolja a feliratkozást.
		
	}

	@Override
	public List<String> getAllEvents() {
		return mEvents;
	}
	
	@Override
	public long callMethodAsync(String method, List<Object> params, PluginResultListener listener){		
		mCallBackIdentification.put(mCallMethodID, listener);
		
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream stream = null;
		try{
			stream = new ObjectOutputStream(bytes);
			stream.writeObject(Integer.valueOf(params.size()));
			for (int i = 0; i < params.size(); i++)
				stream.writeObject(params.get(i));
			byte[] parameters = bytes.toByteArray();

			Intent intent = new Intent(INTENT_CALL_PLUGIN_METHOD);
			intent.putExtra(INTENT_EXTRA_METHOD_NAME, method);
			intent.putExtra(INTENT_EXTRA_METHOD_PARAMETERS, parameters);
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
	public List<String> callMethodSync(String method, List<Object> parameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onResult(int id, String plugin, String pluginVersion,
			String methodName, String result, String meta) {
			mCallBackIdentification.get(id).onResult(id, plugin, pluginVersion, methodName, result, meta);	
	}

	@Override
	public void onError(String plugin, String pluginVersion, String methodName,
			String errorMessage, String meta) {
	}
}
