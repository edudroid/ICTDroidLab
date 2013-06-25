package hu.edudroid.ict;

import hu.edudroid.ict.plugins.Plugin;
import hu.edudroid.ict.plugins.PluginCall;
import hu.edudroid.ict.plugins.PluginCollection;
import hu.edudroid.ict.plugins.PluginListener;
import hu.edudroid.ict.plugins.PluginMethod;
import hu.edudroid.ict.plugins.PluginPollingBroadcast;
import java.util.ArrayList;
import java.util.Collections;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PluginDetailsActivity extends Activity implements ListAdapter,
		PluginListener, OnClickListener {

	private final String			FILTER_PLUGIN_POLL	= "hu.edudroid.ict.plugin_polling_question";
	private final String			FILTER_NEW_PLUGIN	= "hu.edudroid.ict.plugin_polling_answer";

	private Plugin					mPlugin;
	private ArrayList<PluginMethod>	mMethods;
	private LayoutInflater			mInflater;
	private DataSetObserver			mObserver;
	private PluginPollingBroadcast	mBroadcast;

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugin_details);

		mInflater = getLayoutInflater();
		mMethods = new ArrayList<PluginMethod>();
		((ListView) findViewById(R.id.details_list)).setAdapter(this);
		
		mPlugin = PluginCollection.getInstance()
									.getPluginByHashcode(getIntent().getExtras()
																	.getInt("pluginHash"));

		mBroadcast = PluginPollingBroadcast.getInstance();
		//registerReceiver(mBroadcast, new IntentFilter(FILTER_NEW_PLUGIN));
		refreshMethodList();
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		registerReceiver(mBroadcast, new IntentFilter(FILTER_NEW_PLUGIN));
		mBroadcast.registerPluginDetailsListener(this);
	}

	@Override
	protected void onStop(){
		unregisterReceiver(mBroadcast);
		super.onStop();
	}

	private void refreshMethodList(){
		Intent intent = new Intent(FILTER_PLUGIN_POLL);
		intent.putExtra("action", "reportMethods");
		intent.putExtra("pluginName", mPlugin.getName());
		sendBroadcast(intent);
		Log.d("CORE::PluginDetailsActivity:refreshMethodList","Broadcast sent... PluginName: " + mPlugin.getName());
	}

	private void addMethod(PluginMethod method){
		boolean foundMethodInList=false;
		for(int i=0;i<mMethods.size();i++){
			if(mMethods.get(i).mName.equals(method.mName)){
				Log.e("CORE:PluginDetailsActivity:addMethod","Method already exists in list.");
				foundMethodInList=true;
			}
		}
		if(!foundMethodInList){
			mMethods.add(method);
		}
		Collections.sort(mMethods);
		mObserver.onChanged();
	}

	@Override
	public int getCount(){
		return mMethods.size();
	}

	@Override
	public PluginMethod getItem(int position){
		return mMethods.get(position);
	}

	@Override
	public long getItemId(int position){
		return position;
	}

	@Override
	public int getItemViewType(int position){
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		if (convertView == null)
			convertView = mInflater.inflate(R.layout.view_listitem_methods,
											parent,
											false);

		final PluginMethod method = getItem(position);
		((TextView) convertView.findViewById(R.id.method_name)).setText(method.mName);
		((TextView) convertView.findViewById(R.id.method_details)).setText(method.mDescription);

		convertView.setOnClickListener(this);
		return convertView;
	}

	@Override
	public int getViewTypeCount(){
		return 1;
	}

	@Override
	public boolean hasStableIds(){
		return true;
	}

	@Override
	public boolean isEmpty(){
		return (getCount() == 0);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer){
		mObserver = observer;
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer){
		mObserver = null;
	}

	@Override
	public boolean areAllItemsEnabled(){
		return true;
	}

	@Override
	public boolean isEnabled(int position){
		return true;
	}

	@Override
	public void newPlugin(Plugin plugin){}

	@Override
	public void newPluginMethod(PluginMethod method){
		addMethod(method);
	}

	@Override
	public void onClick(View view){
		final PluginCall pluginCall = new PluginCall("showToast");
		pluginCall.addParameter(new String("Hello "));
		pluginCall.addParameter(new String("Working "));
		pluginCall.addParameter(new String("Plugin!"));
		mPlugin.callMethod(pluginCall);
	}

	public static Intent generateIntent(final int pluginHash,
										final Context context){
		Intent intent = new Intent(context, PluginDetailsActivity.class);
		intent.putExtra("pluginHash", pluginHash);
		return intent;
	}

}
